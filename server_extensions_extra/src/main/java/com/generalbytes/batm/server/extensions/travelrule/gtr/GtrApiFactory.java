package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.SecureRandom;

/**
 * Factory for creating Global Travel Rule (GTR) API proxy.
 */
@Slf4j
@AllArgsConstructor
public class GtrApiFactory {

    private final GtrConfiguration configuration;
    private final GtrCertificateLoaderService certificateLoaderService;

    /**
     * Get Global Travel Rule (GTR) API proxy.
     */
    public GtrApi getGtrApi() {
        try {
            KeyManagerFactory keyManagerFactory = certificateLoaderService.loadGtrKeyStore(
                    configuration.getClientCertificatePath(), configuration.getClientCertificatePassphrase()
            );
            TrustManagerFactory trustManagerFactory = certificateLoaderService.loadGtrTrustStore(
                    configuration.getGtrServerTrustCertificatePath()
            );
            return createGtrApiProxy(keyManagerFactory, trustManagerFactory);
        } catch (TravelRuleProviderException e) {
            log.warn("getGtrApi - {}", e.getMessage());
        } catch (Exception e) {
            log.error("getGtrApi - ", e);
        }
        throw new TravelRuleProviderException("Failed to create GTR API proxy");
    }

    private GtrApi createGtrApiProxy(KeyManagerFactory keyManagerFactory, TrustManagerFactory trustManagerFactory) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setSslSocketFactory(sslContext.getSocketFactory());

        return RestProxyFactory.createProxy(GtrApi.class, configuration.getApiUrl(), clientConfig);
    }

}
