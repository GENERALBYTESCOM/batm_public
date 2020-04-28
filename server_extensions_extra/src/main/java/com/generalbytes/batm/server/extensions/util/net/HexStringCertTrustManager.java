/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.util.net;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class HexStringCertTrustManager implements X509TrustManager {
    private static final Logger log = LoggerFactory.getLogger(HexStringCertTrustManager.class);

    private final X509Certificate cert;

    public HexStringCertTrustManager(String certHexString) throws CertificateException {
        this.cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(Hex.decode(certHexString)));
    }

    public static SSLSocketFactory getSslSocketFactory(String certHexString) throws GeneralSecurityException {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{new HexStringCertTrustManager(certHexString)}, new java.security.SecureRandom());
        return sc.getSocketFactory();
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        boolean match = false;
        try {
            for (X509Certificate c : chain) {
                if (c.equals(cert)) {
                    match = true;
                }
            }
        } catch (Exception e) {
            throw new CertificateException();
        }

        if (!match)
            throw new CertificateException();
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] trustedCerts = new X509Certificate[1];
        try {
            trustedCerts[0] = cert;
        } catch (Exception e) {
            log.error("", e);
        }

        return trustedCerts;

    }
}
