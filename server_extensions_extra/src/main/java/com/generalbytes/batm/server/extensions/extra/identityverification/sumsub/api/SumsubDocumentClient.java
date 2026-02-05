package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api;

import com.generalbytes.batm.server.coinutil.Hex;
import com.generalbytes.batm.server.extensions.common.sumsub.SumsubException;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * HTTP client for downloading document images from Sumsub API.
 * This class exists because Rescu REST proxy {@link ISumSubApi} does not support binary responses.
 *
 * <p><a href="https://docs.sumsub.com/reference/get-document-images">Get document images</a>
 */
@Slf4j
public class SumsubDocumentClient {

    private static final String ALGORITHM = "HmacSHA256";
    private static final String HEADER_APP_TOKEN = "X-App-Token";
    private static final String HEADER_APP_TS = "X-App-Access-Ts";
    private static final String HEADER_APP_SIG = "X-App-Access-Sig";
    private static final String DEFAULT_CONTENT_TYPE = "image/jpeg";

    private final String token;
    private final Mac mac;
    private final String baseUrl;

    public SumsubDocumentClient(String token, String secret, String baseUrl) {
        this.token = token;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.mac = createMac(secret);
    }

    private Mac createMac(String secret) {
        try {
            Mac macInstance = Mac.getInstance(ALGORITHM);
            macInstance.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));
            return macInstance;
        } catch (InvalidKeyException e) {
            throw new SumsubException("Failed to initialize SumsubDocumentClient, is the secret key configured properly?", e);
        } catch (NoSuchAlgorithmException e) {
            throw new SumsubException(e);
        }
    }

    /**
     * Downloads a document image by inspection ID and image ID.
     *
     * @param inspectionId the inspection ID from the webhook
     * @param imageId      the image ID from {@link com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectionImage#getImageId()}
     * @return the downloaded content and its content type
     */
    DownloadedDocument downloadDocument(String inspectionId, String imageId) throws IOException {
        HttpURLConnection httpConnection = createHttpConnection(inspectionId, imageId);
        validateResponseCode(httpConnection, imageId);
        String contentType = getContentType(httpConnection);

        try (InputStream is = httpConnection.getInputStream()) {
            byte[] content = ByteStreams.toByteArray(is);
            return new DownloadedDocument(content, contentType);
        }
    }

    private HttpURLConnection createHttpConnection(String inspectionId, String imageId) throws IOException {
        String path = "/resources/inspections/" + inspectionId + "/resources/" + imageId;
        String url = baseUrl + path;

        long timestamp = System.currentTimeMillis() / 1000;
        String timestampString = String.valueOf(timestamp);
        String signature = computeSignature(timestampString, path);
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty(HEADER_APP_TOKEN, token);
        httpConnection.setRequestProperty(HEADER_APP_TS, timestampString);
        httpConnection.setRequestProperty(HEADER_APP_SIG, signature);
        return httpConnection;
    }

    private String getContentType(HttpURLConnection httpConnection) {
        String contentType = httpConnection.getContentType();
        if (contentType != null && contentType.contains(";")) {
            contentType = contentType.split(";")[0].trim();
        }
        if (contentType == null || contentType.isBlank()) {
            contentType = DEFAULT_CONTENT_TYPE;
        }
        return contentType;
    }

    private void validateResponseCode(HttpURLConnection httpConnection, String imageId) throws IOException {
        if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            try (InputStream errorStream = httpConnection.getErrorStream()) {
                String errorResponse = errorStream != null ? new String(ByteStreams.toByteArray(errorStream), StandardCharsets.UTF_8) : "";
                throw new IOException("Error downloading document " + imageId + ": " + httpConnection.getResponseCode() + ": " + errorResponse);
            }
        }
    }

    private String computeSignature(String ts, String path) {
        String combined = ts + "GET" + path;
        mac.update(combined.getBytes(StandardCharsets.UTF_8));
        return Hex.bytesToHexString(mac.doFinal());
    }
}
