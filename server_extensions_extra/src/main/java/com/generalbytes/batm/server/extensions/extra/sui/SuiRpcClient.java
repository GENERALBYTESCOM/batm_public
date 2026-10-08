package com.generalbytes.batm.server.extensions.extra.sui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Minimal JSON-RPC 2.0 client for a local SUI node.
 * Uses OkHttp + Jackson (both already in the project).
 *
 * SUI node default RPC port: 9000
 */
class SuiRpcClient {

    private static final Logger log = LoggerFactory.getLogger(SuiRpcClient.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final long MIST_PER_SUI = 1_000_000_000L;

    private final String rpcUrl;
    private final OkHttpClient httpClient;
    private final ObjectMapper mapper;
    private final AtomicLong idCounter = new AtomicLong(1);

    SuiRpcClient(String rpcUrl) {
        this.rpcUrl = rpcUrl;
        this.httpClient = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }

    /**
     * Returns the SUI balance of the given address in SUI (not MIST).
     */
    BigDecimal getSuiBalance(String address) throws IOException {
        ObjectNode params = mapper.createObjectNode();
        params.put("owner", address);
        params.put("coinType", "0x2::sui::SUI");

        JsonNode result = call("suix_getBalance", mapper.createArrayNode().add(address).add("0x2::sui::SUI"));

        if (result == null || result.isNull()) {
            return BigDecimal.ZERO;
        }
        String totalBalance = result.path("totalBalance").asText("0");
        return new BigDecimal(totalBalance).divide(BigDecimal.valueOf(MIST_PER_SUI));
    }

    /**
     * Builds an unsigned transaction to transfer SUI, signs it with the given key bytes, and submits it.
     * Returns the transaction digest on success.
     */
    String transferSui(String senderAddress, String recipientAddress, BigDecimal amountSui,
                       byte[] privateKeyBytes, byte[] publicKeyBytes) throws IOException {
        long mistAmount = amountSui.multiply(BigDecimal.valueOf(MIST_PER_SUI)).longValue();

        // Step 1: build the unsigned transaction via unsafe_transferSui
        ArrayNode buildParams = mapper.createArrayNode()
            .add(senderAddress)
            .add(recipientAddress)
            .add(String.valueOf(mistAmount))
            .addNull(); // gas budget (null = auto)

        JsonNode buildResult = call("unsafe_transferSui", buildParams);
        if (buildResult == null) {
            throw new IOException("Failed to build SUI transfer transaction");
        }

        String txBytesBase64 = buildResult.path("txBytes").asText();
        if (txBytesBase64 == null || txBytesBase64.isEmpty()) {
            throw new IOException("Empty txBytes in unsafe_transferSui response");
        }

        // Step 2: sign the transaction bytes with Ed25519
        byte[] txBytes = Base64.getDecoder().decode(txBytesBase64);
        String signatureBase64 = SuiSigner.signTransaction(txBytes, privateKeyBytes, publicKeyBytes);

        // Step 3: execute the signed transaction
        ArrayNode execParams = mapper.createArrayNode()
            .add(txBytesBase64)
            .add(mapper.createArrayNode().add(signatureBase64));
        ObjectNode options = mapper.createObjectNode();
        options.put("showEffects", true);
        execParams.add(options);
        execParams.add("WaitForLocalExecution");

        JsonNode execResult = call("sui_executeTransactionBlock", execParams);
        if (execResult == null) {
            throw new IOException("Failed to execute SUI transaction");
        }

        String digest = execResult.path("digest").asText(null);
        if (digest == null || digest.isEmpty()) {
            throw new IOException("No digest in sui_executeTransactionBlock response: " + execResult);
        }
        return digest;
    }

    private JsonNode call(String method, ArrayNode params) throws IOException {
        ObjectNode body = mapper.createObjectNode();
        body.put("jsonrpc", "2.0");
        body.put("id", idCounter.getAndIncrement());
        body.put("method", method);
        body.set("params", params);

        String bodyStr = mapper.writeValueAsString(body);
        log.debug("SUI RPC -> {}: {}", method, bodyStr);

        Request request = new Request.Builder()
            .url(rpcUrl)
            .post(RequestBody.create(bodyStr, JSON))
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("SUI RPC HTTP error: " + response.code() + " for method " + method);
            }
            String responseBody = response.body().string();
            log.debug("SUI RPC <- {}: {}", method, responseBody);
            JsonNode json = mapper.readTree(responseBody);
            if (json.has("error")) {
                throw new IOException("SUI RPC error for " + method + ": " + json.get("error"));
            }
            return json.get("result");
        }
    }
}
