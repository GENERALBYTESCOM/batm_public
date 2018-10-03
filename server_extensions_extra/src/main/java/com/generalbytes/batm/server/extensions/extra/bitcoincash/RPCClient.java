package com.generalbytes.batm.server.extensions.extra.bitcoincash;

import com.azazar.bitcoin.jsonrpcclient.BitcoinException;
import com.azazar.bitcoin.jsonrpcclient.BitcoinJSONRPCClient;

import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RPCClient extends BitcoinJSONRPCClient {
    public RPCClient(String rpcUrl) throws MalformedURLException {
        super(rpcUrl);
        setHostnameVerifier((s, sslSession) -> true);
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] {new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {}

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {}

                @Override
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }}, null);
            setSslSocketFactory(sslcontext.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public double getEstimateFee(int numberOfBlocks) throws BitcoinException {
        return ((Number)query("estimatefee",numberOfBlocks)).doubleValue();
    }
}
