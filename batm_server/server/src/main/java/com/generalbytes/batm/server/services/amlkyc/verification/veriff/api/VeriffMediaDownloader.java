package com.generalbytes.batm.server.services.amlkyc.verification.veriff.api;

import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class VeriffMediaDownloader {
    private final String publicKey;
    private final VeriffDigest veriffDigest;

    public VeriffMediaDownloader(String publicKey, VeriffDigest veriffDigest) {
        this.publicKey = publicKey;
        this.veriffDigest = veriffDigest;
    }

    /**
     * Download media file contents by media ID obtained from {@link IVeriffApi#getSessionMediaInfo(String)}
     */
    public byte[] downloadMedia(String mediaId) throws IOException {
        HttpsURLConnection con = (HttpsURLConnection) new URL(IVeriffApi.BASE_URL + "/media/" + mediaId).openConnection();
        con.setRequestProperty(IVeriffApi.HEADER_PUBLIC_KEY, publicKey);
        con.setRequestProperty(IVeriffApi.HEADER_SIGNATURE, veriffDigest.digest(mediaId));

        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
            try (InputStream errorStream = con.getErrorStream()) {
                String errorResponse = IOUtils.toString(errorStream, StandardCharsets.US_ASCII);
                throw new IOException("Error downloading media " + mediaId + ": " + con.getResponseCode() + ": " + errorResponse);
            }
        }
        try (InputStream is = con.getInputStream()) {
            return IOUtils.toByteArray(is);
        }
    }
}
