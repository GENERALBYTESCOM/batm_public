package com.generalbytes.batm.server.extensions.extra.nano.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @author Karl Oczadly
 */
public class NanoRpcClient {

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
        String frontier;
        try {
            frontier = query(
                JSON_MAPPER.createObjectNode()
                    .put("action",  "account_info")
                    .put("account", account))
                    .get("confirmation_height_frontier").asText();
        } catch (RpcException e) {
            if ("Account not found".equals(e.getMessage())) {
                return BigInteger.ZERO; // Account hasn't been opened
            } else {
                throw e;
            }
        }
        // Get balance of frontier block
        return new BigInteger(query(
            JSON_MAPPER.createObjectNode()
                .put("action", "block_info")
                .put("hash",   frontier))
                .get("balance").asText());
    }

    /** Returns pocketed + pending balance, including unconfirmed blocks. */
    public BigInteger getTotalBalanceUnconfirmed(String account) throws IOException, RpcException {
        ObjectNode response = query(
            JSON_MAPPER.createObjectNode()
                .put("action",  "account_balance")
                .put("account", account));
        // Return balance + pending
        return new BigInteger(response.get("balance").asText())
                .add(new BigInteger(response.get("pending").asText()));
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
        String rawResponse = httpPost(jsonStr);
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
