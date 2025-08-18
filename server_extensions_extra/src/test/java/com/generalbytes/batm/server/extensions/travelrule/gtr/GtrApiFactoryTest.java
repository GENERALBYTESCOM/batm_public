package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrApiFactoryTest {

    @Mock
    private GtrConfiguration configuration;
    @Mock
    private GtrCertificateLoaderService certificateLoaderService;
    @InjectMocks
    private GtrApiFactory gtrApiFactory;

    @Test
    void testGetGtrApi() throws Exception {
        try (MockedStatic<SSLContext> sslContextStaticMock = mockStatic(SSLContext.class);
             MockedStatic<RestProxyFactory> restProxyFactoryStaticMock = mockStatic(RestProxyFactory.class)
        ) {
            KeyManagerFactory keyManagerFactory = mock(KeyManagerFactory.class);
            TrustManagerFactory trustManagerFactory = mock(TrustManagerFactory.class);

            when(configuration.getApiUrl()).thenReturn("apiUrl");
            when(configuration.getClientCertificatePath()).thenReturn("client_certificate.p12");
            when(configuration.getClientCertificatePassphrase()).thenReturn("passphrase");
            when(configuration.getGtrServerTrustCertificatePath()).thenReturn("gtr_server_trust_certificate.pem");
            when(certificateLoaderService.loadGtrKeyStore("client_certificate.p12", "passphrase")).thenReturn(keyManagerFactory);
            when(certificateLoaderService.loadGtrTrustStore("gtr_server_trust_certificate.pem")).thenReturn(trustManagerFactory);

            SSLContext sslContext = mock(SSLContext.class);
            GtrApi gtrApi = mock(GtrApi.class);

            sslContextStaticMock.when(() -> SSLContext.getInstance("TLS")).thenReturn(sslContext);
            restProxyFactoryStaticMock.when(() -> RestProxyFactory.createProxy(eq(GtrApi.class), eq("apiUrl"), any())).thenReturn(gtrApi);

            GtrApi resultGtrApi = gtrApiFactory.getGtrApi();

            assertEquals(gtrApi, resultGtrApi);
            verify(sslContext, times(1)).init(any(), any(), any());
        }
    }

    @Test
    void testGetGtrApi_loadingError_keyStore() throws Exception {
        try (MockedStatic<SSLContext> sslContextStaticMock = mockStatic(SSLContext.class);
             MockedStatic<RestProxyFactory> restProxyFactoryStaticMock = mockStatic(RestProxyFactory.class)
        ) {
            when(configuration.getClientCertificatePath()).thenReturn("client_certificate.p12");
            when(configuration.getClientCertificatePassphrase()).thenReturn("passphrase");
            when(certificateLoaderService.loadGtrKeyStore("client_certificate.p12", "passphrase")).thenThrow(RuntimeException.class);

            TravelRuleProviderException exception = assertThrows(TravelRuleProviderException.class, () -> gtrApiFactory.getGtrApi());
            assertLoadingError(exception, sslContextStaticMock, restProxyFactoryStaticMock);
        }
    }

    @Test
    void testGetGtrApi_loadingError_trustStore() throws Exception {
        try (MockedStatic<SSLContext> sslContextStaticMock = mockStatic(SSLContext.class);
             MockedStatic<RestProxyFactory> restProxyFactoryStaticMock = mockStatic(RestProxyFactory.class)
        ) {
            KeyManagerFactory keyManagerFactory = mock(KeyManagerFactory.class);

            when(configuration.getClientCertificatePath()).thenReturn("client_certificate.p12");
            when(configuration.getClientCertificatePassphrase()).thenReturn("passphrase");
            when(configuration.getGtrServerTrustCertificatePath()).thenReturn("gtr_server_trust_certificate.pem");
            when(certificateLoaderService.loadGtrKeyStore("client_certificate.p12", "passphrase")).thenReturn(keyManagerFactory);
            when(certificateLoaderService.loadGtrTrustStore("gtr_server_trust_certificate.pem")).thenThrow(RuntimeException.class);

            TravelRuleProviderException exception = assertThrows(TravelRuleProviderException.class, () -> gtrApiFactory.getGtrApi());
            assertLoadingError(exception, sslContextStaticMock, restProxyFactoryStaticMock);
        }
    }

    private void assertLoadingError(Exception exception,
                                    MockedStatic<SSLContext> sslContextStaticMock,
                                    MockedStatic<RestProxyFactory> restProxyFactoryStaticMock
    ) {
        assertEquals("Failed to create GTR API proxy", exception.getMessage());
        sslContextStaticMock.verify(() -> SSLContext.getInstance(any()), never());
        restProxyFactoryStaticMock.verify(() -> RestProxyFactory.createProxy(any(), any(), any()), never());
    }

}