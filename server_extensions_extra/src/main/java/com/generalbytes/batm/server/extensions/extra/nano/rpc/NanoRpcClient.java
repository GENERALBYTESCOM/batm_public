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
    public BigInteger getBalanceConfirmed(String account) throws IOException, RpcException {
        // Get confirmed frontier hash (returned `balance` isn't guaranteed to be confirmed)
        ObjectNode accountInfoResponse;
        try {
            accountInfoResponse = query(
                JSON_MAPPER.createObjectNode()
                    .put("action",  "account_info")
                    .put("account", account));
        } catch (RpcException e) {
            if ("Account not found".equals(e.getMessage())) {
                return BigInteger.ZERO; // Account hasn't been opened
            } else {
                throw e;
            }
        }
        if (accountInfoResponse.get("confirmation_height").asText().equals("0")) {
            return BigInteger.ZERO; // Account has no confirmed blocks
        }

        // Get balance of frontier block
        return new BigInteger(query(
            JSON_MAPPER.createObjectNode()
                .put("action", "block_info")
                .put("hash",   accountInfoResponse.get("confirmation_height_frontier").asText()))
                .get("balance").asText());
    }

    /** Returns pocketed (+ pending) balance, including unconfirmed blocks. */
    public BigInteger getBalanceUnconfirmed(String account, boolean includePending) throws IOException, RpcException {
        ObjectNode response = query(
            JSON_MAPPER.createObjectNode()
                .put("action",  "account_balance")
                .put("account", account));

        BigInteger balance = new BigInteger(response.get("balance").asText());
        if (includePending) {
            return balance.add(new BigInteger(response.get("pending").asText()));
        } else {
            return balance;
        }
    }

    /** Creates a new account in the given wallet. */
    public String newWalletAccount(String walletId) throws IOException, RpcException {
        return query(
            JSON_MAPPER.createObjectNode()
                .put("action", "account_create")
                .put("wallet", walletId))
                .get("account").asText();
    }

    /** Sends the specified funds from the given wallet to the provided destination account. */
    public String sendFromWallet(String walletId, String sourceAcc, String destAcc, BigInteger amountRaw, String uid)
            throws IOException, RpcException {
        return query(
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
        return query(
            JSON_MAPPER.createObjectNode()
                .put("action", "deterministic_key")
                .put("seed",   seed)
                .put("index",  index))
                .get("account").asText();
    }

    /** Returns true if the address string is valid. */
    public boolean isAddressValid(String addr) throws IOException, RpcException {
        return query(
            JSON_MAPPER.createObjectNode()
                .put("action",  "validate_account_number")
                .put("account", addr))
                .get("valid").asInt() == 1;
    }



    private ObjectNode query(JsonNode json) throws IOException, RpcException {
        String jsonStr = JSON_MAPPER.writeValueAsString(json);
        log.debug("Sending RPC request {}", json);
        String rawResponse = httpPost(jsonStr);
        log.debug("Received RPC response: {}", rawResponse);
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

}
