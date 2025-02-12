package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.CoinbaseException;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.ICoinbaseAPI;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.ICoinbaseV2API;
import com.generalbytes.batm.server.extensions.util.net.CompatSSLSocketFactory;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.Params;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.ws.rs.HeaderParam;
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
        ICoinbaseAPI mockedCoinbaseApi = mock(ICoinbaseAPI.class);
        SSLContext sslContext = mock(SSLContext.class);
        SSLSocketFactory sslSocketFactory = mock(SSLSocketFactory.class);

        try (MockedStatic<RestProxyFactory> mockedRestProxyFactory = mockStatic(RestProxyFactory.class);
             MockedStatic<SSLContext> mockedSslContext = mockStatic(SSLContext.class)) {
            mockedRestProxyFactory.when(() -> RestProxyFactory.createProxy(any(), anyString(), any())).thenReturn(mockedCoinbaseApi);
            mockedSslContext.when(() -> SSLContext.getInstance(anyString())).thenReturn(sslContext);
            when(sslContext.getSocketFactory()).thenReturn(sslSocketFactory);

            ICoinbaseAPI result = CoinbaseApiFactory.createCoinbaseApiLegacy();

            assertEquals(mockedCoinbaseApi, result);
            mockedSslContext.verify(() -> SSLContext.getInstance("TLS"));
            verify(sslContext).init(null, null, null);
            verify(sslContext).getSocketFactory();
            ArgumentCaptor<ClientConfig> configCaptor = ArgumentCaptor.forClass(ClientConfig.class);
            mockedRestProxyFactory.verify(() -> RestProxyFactory.createProxy(eq(ICoinbaseAPI.class), eq(API_URL), configCaptor.capture()));
            ClientConfig clientConfig = configCaptor.getValue();
            assertNotNull(clientConfig);
            assertTrue(clientConfig.isIgnoreHttpErrorCodes());
            SSLSocketFactory socketFactory = clientConfig.getSslSocketFactory();
            assertTrue(socketFactory instanceof CompatSSLSocketFactory);
        }
    }

    @Test
    public void testCreateCoinbaseApiLegacy_KeyManagementException() throws KeyManagementException {
        SSLContext sslContext = mock(SSLContext.class);

        try (MockedStatic<SSLContext> mockedSslContext = mockStatic(SSLContext.class)) {
            mockedSslContext.when(() -> SSLContext.getInstance(anyString())).thenReturn(sslContext);
            doThrow(new KeyManagementException("Test Exception")).when(sslContext).init(any(), any(), any());

            CoinbaseException exception = assertThrows(CoinbaseException.class, CoinbaseApiFactory::createCoinbaseApiLegacy);

            assertEquals("Unable to create ICoinbaseAPILegacy proxy", exception.getMessage());
            mockedSslContext.verify(() -> SSLContext.getInstance("TLS"));
            verify(sslContext).init(null, null, null);
        }
    }

    @Test
    public void testCreateCoinbaseApiLegacy_NoSuchAlgorithmException() {

        try (MockedStatic<SSLContext> mockedSslContext = mockStatic(SSLContext.class)) {
            mockedSslContext.when(() -> SSLContext.getInstance(anyString())).thenThrow(new NoSuchAlgorithmException("Test Exception"));

            CoinbaseException exception = assertThrows(CoinbaseException.class, CoinbaseApiFactory::createCoinbaseApiLegacy);

            assertEquals("Unable to create ICoinbaseAPILegacy proxy", exception.getMessage());
            mockedSslContext.verify(() -> SSLContext.getInstance("TLS"));
        }
    }

    @Test
    public void testCreateCoinbaseV2ApiLegacy() {
        ICoinbaseV2API mockedCoinbaseApi = mock(ICoinbaseV2API.class);

        try (MockedStatic<RestProxyFactory> mockedRestProxyFactory = mockStatic(RestProxyFactory.class)) {
            mockedRestProxyFactory.when(() -> RestProxyFactory.createProxy(any(), anyString(), any())).thenReturn(mockedCoinbaseApi);

            ICoinbaseV2API result = CoinbaseApiFactory.createCoinbaseV2ApiLegacy();

            assertEquals(mockedCoinbaseApi, result);
            ArgumentCaptor<ClientConfig> configCaptor = ArgumentCaptor.forClass(ClientConfig.class);
            mockedRestProxyFactory.verify(() -> RestProxyFactory.createProxy(eq(ICoinbaseV2API.class), eq(API_URL), configCaptor.capture()));
            ClientConfig clientConfig = configCaptor.getValue();
            assertNotNull(clientConfig);
            assertTrue(clientConfig.isIgnoreHttpErrorCodes());
        }
    }

    @Test
    public void testCreateCoinbaseV3Api() {
        ICoinbaseV3Api mockedCoinbaseApi = mock(ICoinbaseV3Api.class);

        try (MockedStatic<RestProxyFactory> mockedRestProxyFactory = mockStatic(RestProxyFactory.class)) {
            mockedRestProxyFactory.when(() -> RestProxyFactory.createProxy(any(), anyString(), any())).thenReturn(mockedCoinbaseApi);

            ICoinbaseV3Api result = CoinbaseApiFactory.createCoinbaseV3Api();

            assertEquals(mockedCoinbaseApi, result);
            ArgumentCaptor<ClientConfig> configCaptor = ArgumentCaptor.forClass(ClientConfig.class);
            mockedRestProxyFactory.verify(() -> RestProxyFactory.createProxy(eq(ICoinbaseV3Api.class), eq(API_URL), configCaptor.capture()));
            ClientConfig clientConfig = configCaptor.getValue();
            assertNotNull(clientConfig);
            assertEquals(1, clientConfig.getDefaultParamsMap().size());
            Params defaultParams = clientConfig.getDefaultParamsMap().get(HeaderParam.class);
            assertNotNull(defaultParams);
            assertEquals(CoinbaseApiFactory.CB_VERSION, defaultParams.getParamValue("CB-VERSION"));
        }
    }

}