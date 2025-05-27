package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd;

import com.generalbytes.batm.server.coinutil.CoinUnit;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Invoice;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.PaymentRequest;
import com.generalbytes.batm.server.extensions.util.net.HexStringCertTrustManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.Params;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLSocketFactory;
import javax.ws.rs.HeaderParam;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LndWalletTest {

    @Mock
    private LndAPI api;
    private LndWallet wallet;
    @Captor
    private ArgumentCaptor<ClientConfig> clientConfigCaptor;

    @BeforeEach
    void setUp() throws GeneralSecurityException {
        try (MockedStatic<RestProxyFactory> mockedProxyFactory = mockStatic(RestProxyFactory.class)) {
            mockedProxyFactory.when(() -> RestProxyFactory.createProxy(eq(LndAPI.class), any(), any())).thenReturn(api);

            wallet = new LndWallet("http://localhost:8080", "macaroon", null, "feeLimit");
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"certHexString"})
    void testConstruction(String certHexString) throws GeneralSecurityException {
        SSLSocketFactory sslSocketFactoryMock = mock(SSLSocketFactory.class);

        try (MockedStatic<RestProxyFactory> mockedProxyFactory = mockStatic(RestProxyFactory.class);
             MockedStatic<HexStringCertTrustManager> mockedTrustManager = mockStatic(HexStringCertTrustManager.class)) {
            mockedProxyFactory.when(() -> RestProxyFactory.createProxy(any(), any(), any())).thenReturn(api);
            if (certHexString != null) {
                mockedTrustManager.when(() -> HexStringCertTrustManager.getSslSocketFactory(any())).thenReturn(sslSocketFactoryMock);
            }

            new LndWallet("http://localhost:8080", "macaroon", certHexString, "feeLimit");

            mockedProxyFactory.verify(() -> RestProxyFactory.createProxy(eq(LndAPI.class), eq("http://localhost:8080"), clientConfigCaptor.capture()));
            ClientConfig clientConfig = clientConfigCaptor.getValue();
            assertNotNull(clientConfig);
            Params defaultHeaders = clientConfig.getDefaultParamsMap().get(HeaderParam.class);
            assertNotNull(defaultHeaders);
            assertEquals("macaroon", defaultHeaders.getParamValue("Grpc-Metadata-macaroon"));
            if (certHexString != null) {
                assertEquals(sslSocketFactoryMock, clientConfig.getSslSocketFactory());
            } else {
                assertNull(clientConfig.getSslSocketFactory());
            }
            assertEquals(30_000, clientConfig.getHttpConnTimeout());
            assertEquals(45_000, clientConfig.getHttpReadTimeout());
        }
    }

    @Test
    void testGetReceivedAmount_unsupportedCryptocurrency() {
        String unsupportedCryptoCurrency = "unsupportedCryptoCurrency"; // Anything other than LBTC

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> wallet.getReceivedAmount("address", unsupportedCryptoCurrency));

        assertEquals("unsupportedCryptoCurrency not supported", exception.getMessage());
    }

    @Test
    void testGetReceivedAmount_notSettled() throws IOException {
        String address = "address";
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.payment_hash = "hash";
        Invoice invoice = new Invoice();
        invoice.settled = false;

        when(api.decodePaymentRequest(address)).thenReturn(paymentRequest);
        when(api.getInvoice("hash")).thenReturn(invoice);

        BigDecimal result = wallet.getReceivedAmount(address, "LBTC");

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testGetReceivedAmount() throws IOException {
        String address = "address";
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.payment_hash = "hash";
        Invoice invoice = new Invoice();
        invoice.settled = true;
        invoice.amt_paid_msat = "1000";

        when(api.decodePaymentRequest(address)).thenReturn(paymentRequest);
        when(api.getInvoice("hash")).thenReturn(invoice);

        try (MockedStatic<CoinUnit> mockedCoinUnit = mockStatic(CoinUnit.class)) {
            mockedCoinUnit.when(() -> CoinUnit.mSatToBitcoin(anyLong())).thenReturn(new BigDecimal("0.001"));

            BigDecimal result = wallet.getReceivedAmount(address, "LBTC");

            assertEquals(new BigDecimal("0.001"), result);
            mockedCoinUnit.verify(() -> CoinUnit.mSatToBitcoin(1000L));
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {
        ErrorResponseException.class,
        HttpStatusIOException.class,
        ConnectException.class,
        IOException.class
    })
    void testGetReceivedAmount_exception(Class<? extends Exception> exceptionClass) throws IOException {
        String address = "address";
        Exception exception = mock(exceptionClass);
        if (exceptionClass == HttpStatusIOException.class) {
            when(((HttpStatusIOException) exception).getHttpBody()).thenReturn("HTTP error");
        }

        when(api.decodePaymentRequest(address)).thenThrow(exception);

        BigDecimal result = wallet.getReceivedAmount("address", "LBTC");

        assertNull(result);
    }

}