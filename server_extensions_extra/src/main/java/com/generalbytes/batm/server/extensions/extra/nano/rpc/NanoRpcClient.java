package com.generalbytes.batm.server.extensions.extra.nano.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @author Karl Oczadly
 */
public class NanoRpcClient {

    private static final Logger log = LoggerFactory.getLogger(NanoRpcClient.class);

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS).build();
    private static final MediaType JSON_TYPE = MediaType.parse("application/json");

    private final URL url;

    public NanoRpcClient(URL url) {
        this.url = url;
    }


    /** Returns the total confirmed pocketed balance (zero if account isn't opened). */
    public BalanceResponse getBalance(String account) throws IOException, RpcException {
        // Get account info (unconf balance + confirmation frontier)
        ObjectNode accountInfo;
        try {
            accountInfo = query(false,
                JSON_MAPPER.createObjectNode()
                    .put("action",  "account_info")
                    .put("account", account)
                    .put("pending", true));
        } catch (RpcException e) {
            if ("Account not found".equals(e.getMessage())) {
                // Account hasn't been opened
                return new BalanceResponse(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);
            } else {
                throw e;
            }
        }
        BigInteger unconfBalance = new BigInteger(accountInfo.get("balance").asText());
        BigInteger unconfPending = new BigInteger(accountInfo.get("pending").asText());

        if (accountInfo.get("confirmation_height").asText().equals("0")) {
            // Account has no confirmed blocks
            return new BalanceResponse(BigInteger.ZERO, unconfBalance, unconfPending);
        }
        // Get balance of frontier block (confirmed balance)
        BigInteger confBalance = new BigInteger(query(false,
            JSON_MAPPER.createObjectNode()
                .put("action", "block_info")
                .put("hash",   accountInfo.get("confirmation_height_frontier").asText()))
                .get("balance").asText());
        return new BalanceResponse(confBalance, unconfBalance, unconfPending);
    }

    /** Creates a new account in the given wallet. */
    public String newWalletAccount(String walletId) throws IOException, RpcException {
        return query(false,
            JSON_MAPPER.createObjectNode()
                .put("action", "account_create")
                .put("wallet", walletId))
                .get("account").asText();
    }

    /** Sends the specified funds from the given wallet to the provided destination account. */
    public String sendFromWallet(String walletId, String sourceAcc, String destAcc, BigInteger amountRaw, String uid)
            throws IOException, RpcException {
        return query(false,
            JSON_MAPPER.createObjectNode()
                .put("action",      "send")
                .put("wallet",      walletId)
                .put("source",      sourceAcc)
                .put("destination", destAcc)
                .put("amount",      amountRaw)
                .put("id",          uid))
                .get("block").asText();
    }

    /** Creates an account from the given seed. */
    public String accountFromSeed(String seed, long index) throws IOException, RpcException {
        return query(true,
            JSON_MAPPER.createObjectNode()
                .put("action", "deterministic_key")
                .put("seed",   seed)
                .put("index",  index))
                .get("account").asText();
    }

    /** Returns true if the address string is valid. */
    public boolean isAddressValid(String addr) throws IOException, RpcException {
        return query(false,
            JSON_MAPPER.createObjectNode()
                .put("action",  "validate_account_number")
                .put("account", addr))
                .get("valid").asInt() == 1;
    }


    private ObjectNode query(boolean confidential, JsonNode json) throws IOException, RpcException {
        String jsonStr = JSON_MAPPER.writeValueAsString(json);
        if (confidential) {
            log.debug("Sending RPC request [REDACTED]");
        } else {
            log.debug("Sending RPC request {}", json);
        }
        String rawResponse = httpPost(jsonStr);
        if (!confidential) log.debug("Received RPC response: {}", rawResponse);
        JsonNode response = JSON_MAPPER.readTree(rawResponse);
        if (!response.isObject())
            throw new RpcException("Response is not a JSON object.");
        ObjectNode responseJson = (ObjectNode)response;
        if (responseJson.has("error"))
            throw new RpcException(responseJson.get("error").asText());
        return responseJson;
    }

    private String httpPost(String jsonBody) throws IOException {
        Call call = HTTP_CLIENT.newCall(
            new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON_TYPE, jsonBody))
                .build());
        try (Response response = call.execute()){
            return response.body().string();
        }
    }


    public static class RpcException extends Exception {
        public RpcException(String message) {
            super(message);
        }
    }

    public static class BalanceResponse {
        public final BigInteger confBalance, unconfBalance, unconfPending;

        public BalanceResponse(BigInteger confBalance, BigInteger unconfBalance, BigInteger unconfPending) {
            this.confBalance = confBalance;
            this.unconfBalance = unconfBalance;
            this.unconfPending = unconfPending;
        }
    }

}
