package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.CoinbaseException;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.ICoinbaseAPILegacy;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.ICoinbaseV2APILegacy;
import com.generalbytes.batm.server.extensions.util.net.CompatSSLSocketFactory;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoinbaseApiFactoryTest {

    private static final String API_URL = "https://api.coinbase.com";

    @Test
    public void testCreateCoinbaseApiLegacy_valid() throws KeyManagementException {
        CoinbaseApiFactory apiFactory = new CoinbaseApiFactory();
        ICoinbaseAPILegacy mockedCoinbaseApi = mock(ICoinbaseAPILegacy.class);
        SSLContext sslContext = mock(SSLContext.class);
        SSLSocketFactory sslSocketFactory = mock(SSLSocketFactory.class);

        try (MockedStatic<RestProxyFactory> mockedRestProxyFactory = mockStatic(RestProxyFactory.class);
             MockedStatic<SSLContext> mockedSslContext = mockStatic(SSLContext.class)) {
            mockedRestProxyFactory.when(() -> RestProxyFactory.createProxy(any(), anyString(), any())).thenReturn(mockedCoinbaseApi);
            mockedSslContext.when(() -> SSLContext.getInstance(anyString())).thenReturn(sslContext);
            when(sslContext.getSocketFactory()).thenReturn(sslSocketFactory);

            ICoinbaseAPILegacy result = apiFactory.createCoinbaseApiLegacy();

            assertEquals(mockedCoinbaseApi, result);
            mockedSslContext.verify(() -> SSLContext.getInstance("TLS"));
            verify(sslContext).init(null, null, null);
            verify(sslContext).getSocketFactory();
            ArgumentCaptor<ClientConfig> configCaptor = ArgumentCaptor.forClass(ClientConfig.class);
            mockedRestProxyFactory.verify(() -> RestProxyFactory.createProxy(eq(ICoinbaseAPILegacy.class), eq(API_URL), configCaptor.capture()));
            ClientConfig clientConfig = configCaptor.getValue();
            assertNotNull(clientConfig);
            assertTrue(clientConfig.isIgnoreHttpErrorCodes());
            SSLSocketFactory socketFactory = clientConfig.getSslSocketFactory();
            assertTrue(socketFactory instanceof CompatSSLSocketFactory);
        }
    }

    @Test
    public void testCreateCoinbaseApiLegacy_KeyManagementException() throws KeyManagementException {
        CoinbaseApiFactory apiFactory = new CoinbaseApiFactory();
        SSLContext sslContext = mock(SSLContext.class);

        try (MockedStatic<SSLContext> mockedSslContext = mockStatic(SSLContext.class)) {
            mockedSslContext.when(() -> SSLContext.getInstance(anyString())).thenReturn(sslContext);
            doThrow(new KeyManagementException("Test Exception")).when(sslContext).init(any(), any(), any());

            CoinbaseException exception = assertThrows(CoinbaseException.class, apiFactory::createCoinbaseApiLegacy);

            assertEquals("Unable to create ICoinbaseAPILegacy proxy", exception.getMessage());
            mockedSslContext.verify(() -> SSLContext.getInstance("TLS"));
            verify(sslContext).init(null, null, null);
        }
    }

    @Test
    public void testCreateCoinbaseApiLegacy_NoSuchAlgorithmException() {
        CoinbaseApiFactory apiFactory = new CoinbaseApiFactory();

        try (MockedStatic<SSLContext> mockedSslContext = mockStatic(SSLContext.class)) {
            mockedSslContext.when(() -> SSLContext.getInstance(anyString())).thenThrow(new NoSuchAlgorithmException("Test Exception"));

            CoinbaseException exception = assertThrows(CoinbaseException.class, apiFactory::createCoinbaseApiLegacy);

            assertEquals("Unable to create ICoinbaseAPILegacy proxy", exception.getMessage());
            mockedSslContext.verify(() -> SSLContext.getInstance("TLS"));
        }
    }

    @Test
    public void testCreateCoinbaseV2ApiLegacy() {
        CoinbaseApiFactory apiFactory = new CoinbaseApiFactory();
        ICoinbaseV2APILegacy mockedCoinbaseApi = mock(ICoinbaseV2APILegacy.class);

        try (MockedStatic<RestProxyFactory> mockedRestProxyFactory = mockStatic(RestProxyFactory.class)) {
            mockedRestProxyFactory.when(() -> RestProxyFactory.createProxy(any(), anyString(), any())).thenReturn(mockedCoinbaseApi);

            ICoinbaseV2APILegacy result = apiFactory.createCoinbaseV2ApiLegacy();

            assertEquals(mockedCoinbaseApi, result);
            ArgumentCaptor<ClientConfig> configCaptor = ArgumentCaptor.forClass(ClientConfig.class);
            mockedRestProxyFactory.verify(() -> RestProxyFactory.createProxy(eq(ICoinbaseV2APILegacy.class), eq(API_URL), configCaptor.capture()));
            ClientConfig clientConfig = configCaptor.getValue();
            assertNotNull(clientConfig);
            assertTrue(clientConfig.isIgnoreHttpErrorCodes());
        }
    }

}