/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.examples.rest;



import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

/**
 * This class demonstrates how to call your REST services from Java and get your JSON response unmarshalled
 */
public class SecuredRESTServiceClient {
    @Path("/extensions")
    @Produces(MediaType.APPLICATION_JSON)
    interface ISecuredServiceAPI {
        @GET
        @Path("/example/helloworld")
        MyExtensionExampleResponse getServerVersion();

        @GET
        @Path("/secured/helloworld")
        MyExtensionExampleResponse getServerVersion(@QueryParam("api_key") String apiKey, @QueryParam("nonce") String nonce, @QueryParam("signature") String signature, @QueryParam("serial_number") String serialNumber);
    }


    /**
     * For simplicity this Trust Manager will trust all server certificates.
     * In real world scenario you have to implement here trusting only your server certificate
     */
    static TrustManager trm = new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
    };

    /**
     * This hostname verifier resolves the issue of having certificate issued for different hostname.
     */
    static HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession sslSession) {
            return true;
        }
    };

    private static ClientConfig createClientConfigurationWithCustomTrustManager() {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { trm }, null);
            ClientConfig cc = new ClientConfig();
            cc.setSslSocketFactory(sc.getSocketFactory());

            cc.setHostnameVerifier(hostnameVerifier);
            return cc;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        ISecuredServiceAPI service = RestProxyFactory.createProxy(ISecuredServiceAPI.class, "https://localhost:7743/", createClientConfigurationWithCustomTrustManager());

        //lets call unsecured service
        MyExtensionExampleResponse response = service.getServerVersion();
        System.out.println("Response from unsecured service = " + response);

        //lets call secured service
        String nonce = System.currentTimeMillis() + "";
        String serialNumber = "BT100001";
        String signature = SecuredRESTServiceExample.generateSignature(nonce + serialNumber + SecuredRESTServiceExample.API_KEY, SecuredRESTServiceExample.API_SECRET);
        response = service.getServerVersion(SecuredRESTServiceExample.API_KEY, nonce, signature, serialNumber);
        System.out.println("Response from secured service = " + response);
    }
}
