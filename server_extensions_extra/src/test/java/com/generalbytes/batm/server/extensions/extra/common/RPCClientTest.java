package com.generalbytes.batm.server.extensions.extra.common;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RPCClientTest {
    private static final char[] KEYSTORE_PASSWORD = "testpass".toCharArray();
    private static final byte[] GET_BLOCK_COUNT_RESPONSE = "{\"result\":42,\"error\":null,\"id\":\"1\"}".getBytes(StandardCharsets.UTF_8);

    private KeyStore selfSignedKeyStore;
    private HttpsServer server;
    private String rpcUrl;

    @BeforeEach
    void startSelfSignedRpcServer() throws Exception {
        selfSignedKeyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = getClass().getResourceAsStream("self-signed-rpc.p12")) {
            selfSignedKeyStore.load(in, KEYSTORE_PASSWORD);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(selfSignedKeyStore, KEYSTORE_PASSWORD);
        SSLContext serverContext = SSLContext.getInstance("TLS");
        serverContext.init(kmf.getKeyManagers(), null, null);

        server = HttpsServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(serverContext));
        server.createContext("/", exchange -> {
            exchange.getRequestBody().readAllBytes();
            exchange.sendResponseHeaders(200, GET_BLOCK_COUNT_RESPONSE.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(GET_BLOCK_COUNT_RESPONSE);
            }
        });
        server.start();
        rpcUrl = "https://user:password@localhost:" + server.getAddress().getPort();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://user:password@node.example.com:8332", "http://user:password@localhost:8332"})
    void testConstructor_defaultTlsValidation(String url) throws Exception {
        RPCClient client = new RPCClient("BTC", url);

        assertThat(client.getHostnameVerifier()).isNull();
        assertThat(client.getSslSocketFactory()).isNull();
    }

    @Test
    void testQuery_untrustedCertificateRejected() throws Exception {
        RPCClient client = new RPCClient("BTC", rpcUrl);

        assertThatThrownBy(client::getBlockCount)
            .isInstanceOf(BitcoinRPCException.class)
            .hasCauseInstanceOf(SSLHandshakeException.class);
    }

    @Test
    void testQuery_trustedCertificateAccepted() throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(selfSignedKeyStore);
        SSLContext clientContext = SSLContext.getInstance("TLS");
        clientContext.init(null, tmf.getTrustManagers(), null);
        RPCClient client = new RPCClient("BTC", rpcUrl);
        client.setSslSocketFactory(clientContext.getSocketFactory());

        assertThat(client.getBlockCount()).isEqualTo(42);
    }
}
