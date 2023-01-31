package com.generalbytes.batm.server.extensions.extra.identityverification.onfido.verificationsite;

import com.generalbytes.batm.server.extensions.extra.identityverification.onfido.verificationsite.dto.RegisterApplicantRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class VerificationSiteClient {

    private static final Logger log = LoggerFactory.getLogger(VerificationSiteClient.class);

    private VerificationSiteAPI api;
    private String masterUrl;

    public VerificationSiteClient(String verificationSiteUrl, String masterUrl) {
        this(masterUrl, createAPI(verificationSiteUrl));
    }

    public VerificationSiteClient(String masterUrl, VerificationSiteAPI api) {
        this.masterUrl = masterUrl;
        this.api = api;
    }

    public void notifyAboutApplicant(String applicantId, String sdkToken) {
        log.info("Notifying verification_site about applicant {}", applicantId);
        api.registerApplicant(new RegisterApplicantRequest(applicantId, sdkToken, masterUrl));
    }

    private static VerificationSiteAPI createAPI(String verificationSiteUrl) {
        try {
            ClientConfig config = new ClientConfig();
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            }, new SecureRandom());
            config.setHostnameVerifier((hostname, session) -> true);
            config.setIgnoreHttpErrorCodes(true);
            config.setSslSocketFactory(sslcontext.getSocketFactory());
            return RestProxyFactory.createProxy(VerificationSiteAPI.class, verificationSiteUrl, config);
        } catch (Throwable t) {
            log.error("constructor - Cannot create instance.", t);
            return null;
        }
    }

}
