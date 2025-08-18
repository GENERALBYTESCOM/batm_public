package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrCertificateLoaderServiceTest {

    @Mock
    private TravelRuleExtensionContext extensionContext;
    @InjectMocks
    private GtrCertificateLoaderService loaderService;

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n", "passphrase" })
    void testLoadGtrKeyStore(String passphrase) throws Exception {
        try (MockedStatic<KeyManagerFactory> keyManagerFactoryStaticMock = mockStatic(KeyManagerFactory.class)) {
            KeyStore clientKeyStore = mock(KeyStore.class);
            KeyManagerFactory keyManagerFactory = mock(KeyManagerFactory.class);

            when(extensionContext.loadKeyStoreFromConfigDirectory("PKCS12", Path.of("client_certificate.p12"), passphrase))
                    .thenReturn(clientKeyStore);
            keyManagerFactoryStaticMock.when(() -> KeyManagerFactory.getInstance(any())).thenReturn(keyManagerFactory);

            KeyManagerFactory result = loaderService.loadGtrKeyStore("client_certificate.p12", passphrase);

            assertEquals(keyManagerFactory, result);
            if (StringUtils.isEmpty(passphrase)) {
                verify(keyManagerFactory, times(1)).init(eq(clientKeyStore), isNull());
            } else {
                verify(keyManagerFactory, times(1)).init(clientKeyStore, passphrase.toCharArray());
            }
        }
    }

    @Test
    void testLoadGtrKeyStore_extensionContextLoadingError() {
        try (MockedStatic<KeyManagerFactory> keyManagerFactoryStaticMock = mockStatic(KeyManagerFactory.class)) {
            when(extensionContext.loadKeyStoreFromConfigDirectory("PKCS12", Path.of("client_certificate.p12"), "passphrase"))
                    .thenReturn(null);

            TravelRuleProviderException exception = assertThrows(
                    TravelRuleProviderException.class, () -> loaderService.loadGtrKeyStore("client_certificate.p12", "passphrase")
            );

            assertEquals("key store of type 'PKCS12' with relative path 'client_certificate.p12' failed to load", exception.getMessage());
            keyManagerFactoryStaticMock.verify(() -> KeyManagerFactory.getInstance(any()), never());
        }
    }

    @Test
    void testLoadGtrTrustStore() throws Exception {
        try (MockedStatic<KeyStore> keyStoreStaticMock = mockStatic(KeyStore.class);
             MockedStatic<TrustManagerFactory> trustManagerFactoryStaticMock = mockStatic(TrustManagerFactory.class)
        ) {
            X509Certificate x509Certificate = mock(X509Certificate.class);
            KeyStore trustStore = mock(KeyStore.class);
            TrustManagerFactory trustManagerFactory = mock(TrustManagerFactory.class);

            when(extensionContext.loadX509CertificateFromConfigDirectory(Path.of("gtr_server_trust_certificate.pem")))
                    .thenReturn(x509Certificate);
            keyStoreStaticMock.when(() -> KeyStore.getInstance(any())).thenReturn(trustStore);
            trustManagerFactoryStaticMock.when(() -> TrustManagerFactory.getInstance(any())).thenReturn(trustManagerFactory);

            TrustManagerFactory result = loaderService.loadGtrTrustStore("gtr_server_trust_certificate.pem");

            assertEquals(trustManagerFactory, result);
            verify(trustStore, times(1)).load(isNull(), isNull());
            verify(trustStore, times(1)).setCertificateEntry("gtr_server_trust_certificate", x509Certificate);
            verify(trustManagerFactory, times(1)).init(trustStore);
        }
    }

    @Test
    void testLoadGtrTrustStore_extensionContextLoadingError() {
        try (MockedStatic<KeyStore> keyStoreStaticMock = mockStatic(KeyStore.class);
             MockedStatic<TrustManagerFactory> trustManagerFactoryStaticMock = mockStatic(TrustManagerFactory.class)
        ) {
            when(extensionContext.loadX509CertificateFromConfigDirectory(Path.of("gtr_server_trust_certificate.pem")))
                    .thenReturn(null);

            TravelRuleProviderException exception = assertThrows(
                    TravelRuleProviderException.class, () -> loaderService.loadGtrTrustStore("gtr_server_trust_certificate.pem")
            );

            assertEquals(
                    "certificate of type 'pem' with relative path 'gtr_server_trust_certificate.pem' failed to load",
                    exception.getMessage()
            );
            keyStoreStaticMock.verify(() -> KeyStore.getInstance(any()), never());
            trustManagerFactoryStaticMock.verify(() -> TrustManagerFactory.getInstance(any()), never());
        }
    }

}