package com.generalbytes.batm.server.extensions.extra.nano.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.dto.AccountBalance;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.dto.Block;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Performs RPC queries to an external node through the HTTP/JSON interface.
 */
@Slf4j
public class NanoRpcClient {

    private static final String ACCOUNT_NOT_FOUND_ERROR_MESSAGE = "Account not found";
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS)
        .build();
    private static final MediaType JSON_TYPE = MediaType.parse("application/json");

    private final URL url;

    public NanoRpcClient(URL url) {
        this.url = url;
    }

    public List<Block> getTransactionHistory(String account) throws IOException, RpcException {
        try {
            ArrayNode history = (ArrayNode) query(JSON_MAPPER.createObjectNode()
                .put("action", "account_history")
                .put("account", account)
                .put("count", -1)
            ).get("history");
            List<Block> blocks = new ArrayList<>(history.size());
            for (JsonNode blockNode : history) {
                ObjectNode block = (ObjectNode) blockNode;
                blocks.add(new Block(
                    block.get("type").asText(),
                    block.get("account").asText(),
                    new BigInteger(block.get("amount").asText()),
                    getHash(block)
                ));
            }
            return blocks;
        } catch (RpcException e) {
            if (ACCOUNT_NOT_FOUND_ERROR_MESSAGE.equals(e.getMessage())) {
                return Collections.emptyList(); // Account hasn't been opened
            }
            throw e;
        }
    }

    private String getHash(ObjectNode blockNode) {
        JsonNode hashNode = blockNode.get("hash");
        if (hashNode == null || hashNode.isNull()) {
            return null;
        }
        return hashNode.asText();
    }

    /**
     * Returns true if the account has at least 1 block.
     */
    public boolean doesAccountExist(String account) throws IOException, RpcException {
        try {
            return query(JSON_MAPPER.createObjectNode()
                .put("action", "account_block_count")
                .put("account", account)
            ).get("block_count").asInt() > 0;
        } catch (RpcException e) {
            if (ACCOUNT_NOT_FOUND_ERROR_MESSAGE.equals(e.getMessage())) {
                return false; // Account hasn't been opened
            }
            throw e;
        }
    }

    /**
     * Returns the total confirmed pocketed balance (zero if account isn't opened).
     */
    public AccountBalance getBalance(String account) throws IOException, RpcException {
        // Get account info (unconf balance + confirmation frontier)
        ObjectNode accountInfo;
        try {
            accountInfo = query(JSON_MAPPER.createObjectNode()
                .put("action", "account_info")
                .put("account", account)
                .put("pending", true));
        } catch (RpcException e) {
            if (ACCOUNT_NOT_FOUND_ERROR_MESSAGE.equals(e.getMessage())) {
                // Account hasn't been opened
                return new AccountBalance(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);
            } else {
                throw e;
            }
        }
        BigInteger unconfBalance = new BigInteger(accountInfo.get("balance").asText());
        BigInteger unconfPending = new BigInteger(accountInfo.get("pending").asText());

        if (accountInfo.get("confirmation_height").asText().equals("0")) {
            // Account has no confirmed blocks
            return new AccountBalance(BigInteger.ZERO, unconfBalance, unconfPending);
        }
        // Get balance of frontier block (confirmed balance)
        BigInteger confBalance = new BigInteger(query(JSON_MAPPER.createObjectNode()
            .put("action", "block_info")
            .put("hash", accountInfo.get("confirmation_height_frontier").asText())
        ).get("balance").asText());
        return new AccountBalance(confBalance, unconfBalance, unconfPending);
    }

    /**
     * Creates a new account in the given wallet.
     */
    public String newWalletAccount(String walletId) throws IOException, RpcException {
        return query(JSON_MAPPER.createObjectNode()
            .put("action", "account_create")
            .put("wallet", walletId)
        ).get("account").asText();
    }

    /**
     * Sends the specified funds from the given wallet to the provided destination account.
     */
    public String sendFromWallet(String walletId, String sourceAcc, String destAcc, BigInteger amountRaw, String uid)
        throws IOException, RpcException {
        return query(JSON_MAPPER.createObjectNode()
            .put("action", "send")
            .put("wallet", walletId)
            .put("source", sourceAcc)
            .put("destination", destAcc)
            .put("amount", amountRaw)
            .put("id", uid)
        ).get("block").asText();
    }

    private ObjectNode query(JsonNode json) throws IOException, RpcException {
        String jsonStr = JSON_MAPPER.writeValueAsString(json);
        log.debug("Sending RPC request {}", json);

        String rawResponse = httpPost(jsonStr);
        log.debug("Received RPC response: {}", rawResponse);
        JsonNode response = JSON_MAPPER.readTree(rawResponse);
        if (!response.isObject()) {
            throw new RpcException("Response is not a JSON object.");
        }

        ObjectNode responseJson = (ObjectNode) response;
        if (responseJson.has("error")) {
            throw new RpcException(responseJson.get("error").asText());
        }
        return responseJson;
    }

    private String httpPost(String jsonBody) throws IOException {
        Call call = HTTP_CLIENT.newCall(new Request.Builder()
            .url(url)
            .post(RequestBody.create(JSON_TYPE, jsonBody))
            .build());
        try (Response response = call.execute()) {
            return response.body().string();
        }
    }

}
