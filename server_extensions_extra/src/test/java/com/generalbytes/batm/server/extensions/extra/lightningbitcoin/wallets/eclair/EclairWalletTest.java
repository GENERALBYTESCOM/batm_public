package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair;

import com.generalbytes.batm.server.coinutil.CoinUnit;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.ReceivedInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

import javax.ws.rs.HeaderParam;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EclairWalletTest {

    @Mock
    private EclairAPI api;
    private EclairWallet wallet;
    @Captor
    private ArgumentCaptor<ClientConfig> clientConfigCaptor;

    @BeforeEach
    void setUp() {
        try (MockedStatic<RestProxyFactory> mockedProxyFactory = mockStatic(RestProxyFactory.class)) {
            mockedProxyFactory.when(() -> RestProxyFactory.createProxy(any(), any(), any())).thenReturn(api);

            wallet = new EclairWallet("http", "localhost", 8080, "password");
        }
    }

    @Test
    void testConstruction() {
        try (MockedStatic<RestProxyFactory> mockedProxyFactory = mockStatic(RestProxyFactory.class)) {
            mockedProxyFactory.when(() -> RestProxyFactory.createProxy(any(), any(), any())).thenReturn(api);

            new EclairWallet("http", "localhost", 8080, "password");

            mockedProxyFactory.verify(() -> RestProxyFactory.createProxy(eq(EclairAPI.class), eq("http://localhost:8080/"), clientConfigCaptor.capture()));
            ClientConfig clientConfig = clientConfigCaptor.getValue();
            assertNotNull(clientConfig);
            Params headerParams = clientConfig.getDefaultParamsMap().get(HeaderParam.class);
            assertNotNull(headerParams);
            assertEquals("Basic OnBhc3N3b3Jk", headerParams.getParamValue("Authorization"));
        }
    }

    @Test
    void testGetReceivedAmount_unsupportedCryptocurrency() {
        String unsupportedCryptoCurrency = "unsupportedCryptoCurrency"; // Anything other than LBTC

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> wallet.getReceivedAmount("address", unsupportedCryptoCurrency));

        assertEquals("unsupportedCryptoCurrency not supported", exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = ReceivedInfo.Status.Type.class, mode = EnumSource.Mode.EXCLUDE, names = {"received"})
    void testGetReceivedAmount_invalidStatusType(ReceivedInfo.Status.Type statusType) throws IOException {
        String address = "address";
        ReceivedInfo receivedInfo = new ReceivedInfo();
        receivedInfo.status = new ReceivedInfo.Status();
        receivedInfo.status.type = statusType;

        when(api.getReceivedInfoByInvoice(address)).thenReturn(receivedInfo);

        BigDecimal result = wallet.getReceivedAmount(address, "LBTC");

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testGetReceivedAmount() throws IOException {
        String address = "address";
        long amount = 1000L;
        ReceivedInfo receivedInfo = new ReceivedInfo();
        receivedInfo.status = new ReceivedInfo.Status();
        receivedInfo.status.type = ReceivedInfo.Status.Type.received;
        receivedInfo.status.amount = amount;

        when(api.getReceivedInfoByInvoice(address)).thenReturn(receivedInfo);

        try (MockedStatic<CoinUnit> mockedCoinUnit = mockStatic(CoinUnit.class)) {
            mockedCoinUnit.when(() -> CoinUnit.mSatToBitcoin(amount)).thenReturn(BigDecimal.TEN);

            BigDecimal result = wallet.getReceivedAmount(address, "LBTC");

            assertEquals(BigDecimal.TEN, result);
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {
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

        when(api.getReceivedInfoByInvoice(address)).thenThrow(exception);

        BigDecimal result = wallet.getReceivedAmount(address, "LBTC");

        assertNull(result);
    }

    @Test
    void testGetReceivedAmount_ErrorResponseException_notFound() throws IOException {
        String address = "address";
        ErrorResponseException exception = new ErrorResponseException();
        exception.error = "Not found";

        when(api.getReceivedInfoByInvoice(address)).thenThrow(exception);

        BigDecimal result = wallet.getReceivedAmount(address, "LBTC");

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testGetReceivedAmount_ErrorResponseException_other() throws IOException {
        String address = "address";
        ErrorResponseException exception = new ErrorResponseException();
        exception.error = "Some other error";

        when(api.getReceivedInfoByInvoice(address)).thenThrow(exception);

        BigDecimal result = wallet.getReceivedAmount(address, "LBTC");

        assertNull(result);
    }

}