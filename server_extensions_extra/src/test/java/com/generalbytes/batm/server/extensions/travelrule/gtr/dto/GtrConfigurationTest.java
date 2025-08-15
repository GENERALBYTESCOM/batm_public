package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GtrConfigurationTest {

    @Test
    void testToString() {
        GtrConfiguration configuration = createGtrConfiguration();

        String result = configuration.toString();

        assertEquals("GtrConfiguration(apiUrl=gtr_url, requestIdPrefix=my_request_id_prefix,"
                + " clientCertificatePath=client_certificate_path, gtrServerTrustCertificatePath=gtr_server_trust_certificate_path,"
                + " accessTokenExpirationInMinutes=21, webhooksEnabled=true)", result);
    }

    private GtrConfiguration createGtrConfiguration() {
        GtrConfiguration configuration = new GtrConfiguration();
        configuration.setApiUrl("gtr_url");
        configuration.setRequestIdPrefix("my_request_id_prefix");
        configuration.setClientCertificatePath("client_certificate_path");
        configuration.setClientCertificatePassphrase("client_certificate_passphrase");
        configuration.setGtrServerTrustCertificatePath("gtr_server_trust_certificate_path");
        configuration.setAccessTokenExpirationInMinutes(21);
        configuration.setWebhooksEnabled(true);

        return configuration;
    }

}