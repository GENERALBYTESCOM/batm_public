package com.generalbytes.batm.server.extensions.extra.dash.sources.cddash;
import javax.net.ssl.SSLSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLSocketFactory;

/**
 * {@link SSLSocketFactory} which creates {@link SSLSocket} instances configured
 * to be compatible with the TLS/SSL configuration of the server (TODO: insert
 * name here).
 */
public class CompatSSLSocketFactory extends SSLSocketFactory {

    private final SSLSocketFactory mDelegate;

    /**
     * Creates an instance of the {@code CompatSSLSocketFactory} which uses the
     * provided factory to obtain {@code SSLSocket} instances and then
     * reconfigures them for compatibility.
     */
    public CompatSSLSocketFactory(SSLSocketFactory delegate) {
        mDelegate = delegate;
    }

    /** List of cipher suites needed for compatibility with the server. */
    private static final String[] COMPAT_CIPHER_SUITES = {
            "TLS_RSA_WITH_AES_128_CBC_SHA256",
    };

    @Override
    public String[] getDefaultCipherSuites() {
        String[] original = mDelegate.getDefaultCipherSuites();

        // Add any supported missing cipher suites from COMPAT_CIPHER_SUITES to the
        // end of the list.
        List<String> result = new ArrayList<String>(Arrays.asList(original));
        Set<String> supported =
                new HashSet<String>(Arrays.asList(getSupportedCipherSuites()));
        for (String cipherSuite : COMPAT_CIPHER_SUITES) {
            if ((!result.contains(cipherSuite))
                    && (supported.contains(cipherSuite))) {
                result.add(cipherSuite);
            }
        }
        if (result.size() == original.length) {
            // No changes to the default list
            return original;
        }

        return result.toArray(new String[result.size()]);
    }

    protected void configureSocket(SSLSocket socket) {
        // Uncomment the lines below to modify the list of protocols enabled for
        // this socket.
        // This example enables TLSv2 only.
         socket.setEnabledProtocols(new String[] {"TLSv1.2"});

        socket.setEnabledCipherSuites(getDefaultCipherSuites());
    }

    @Override
    public SSLSocket createSocket(Socket socket, String host, int port,
                                  boolean autoClose) throws IOException {
        SSLSocket sslSocket =
                (SSLSocket) mDelegate.createSocket(socket, host, port, autoClose);
        configureSocket(sslSocket);
        return sslSocket;
    }

    @Override
    public SSLSocket createSocket(String host, int port)
            throws IOException, UnknownHostException {
        SSLSocket sslSocket = (SSLSocket) mDelegate.createSocket(host, port);
        configureSocket(sslSocket);
        return sslSocket;
    }

    @Override
    public SSLSocket createSocket(InetAddress host, int port) throws IOException {
        SSLSocket sslSocket = (SSLSocket) mDelegate.createSocket(host, port);
        configureSocket(sslSocket);
        return sslSocket;
    }

    @Override
    public SSLSocket createSocket(String host, int port, InetAddress localHost,
                                  int localPort) throws IOException, UnknownHostException {
        SSLSocket sslSocket =
                (SSLSocket) mDelegate.createSocket(host, port, localHost, localPort);
        configureSocket(sslSocket);
        return sslSocket;
    }

    @Override
    public SSLSocket createSocket(InetAddress address, int port,
                                  InetAddress localAddress, int localPort) throws IOException {
        SSLSocket sslSocket =
                (SSLSocket) mDelegate.createSocket(
                        address, port, localAddress, localPort);
        configureSocket(sslSocket);
        return sslSocket;
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return mDelegate.getSupportedCipherSuites();
    }
}

