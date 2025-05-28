package com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp;

import com.generalbytes.batm.common.currencies.SlpToken;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.ISlpdbApi;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto.IncomingTransactionsSlpdbResponse;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto.StatusSlpdbResponse;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.GetAddressUnspentElectrumResponse;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElectronCashSlpWalletTest {

    private static final String ADDRESS = "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2";
    private static final String CRYPTOCURRENCY = SlpToken.SPICE.name();

    @Mock
    private ISlpdbApi slpdbApi;
    @Mock
    private IElectronCashSlpApi api;
    private ElectronCashSlpWallet wallet;

    @BeforeEach
    void setUp() {
        ArgumentCaptor<ClientConfig> clientConfigCaptor = ArgumentCaptor.forClass(ClientConfig.class);

        try (MockedStatic<RestProxyFactory> restProxyFactoryMock = mockStatic(RestProxyFactory.class);
             MockedStatic<ISlpdbApi> slpdbApiMock = mockStatic(ISlpdbApi.class)) {
            restProxyFactoryMock.when(() -> RestProxyFactory.createProxy(any(), anyString(), any(), any())).thenReturn(api);
            slpdbApiMock.when(ISlpdbApi::create).thenReturn(List.of(slpdbApi));

            wallet = new ElectronCashSlpWallet("user", "password", "localhost", 27017);

            restProxyFactoryMock.verify(() -> RestProxyFactory.createProxy(
                eq(IElectronCashSlpApi.class),
                eq("http://localhost:27017/"),
                clientConfigCaptor.capture(),
                notNull()
            ));
            ClientConfig clientConfig = clientConfigCaptor.getValue();
            Params headerParams = clientConfig.getDefaultParamsMap().get(HeaderParam.class);
            assertNotNull(headerParams);
            assertEquals("Basic dXNlcjpwYXNzd29yZA==", headerParams.getParamValue("Authorization"));
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"unsupportedCryptocurrency"})
    void testGetReceivedAmount_unsupportedCryptocurrency(String unsupportedCryptocurrency) {
        ReceivedAmount receivedAmount = wallet.getReceivedAmount(ADDRESS, unsupportedCryptocurrency);

        assertNull(receivedAmount);
    }

    private static Object[] provideInvalidUnspentResults() {
        return new Object[]{
            List.of(),
            // Value is anything except 546
            List.of(createGetAddressUnspentResult(123L)),
        };
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUnspentResults")
    void testGetReceivedAmount_noUnspent(List<GetAddressUnspentElectrumResponse.GetAddressUnspentResult> results) throws IOException {
        GetAddressUnspentElectrumResponse unspentResponse = new GetAddressUnspentElectrumResponse();
        unspentResponse.result = results;

        when(api.getAddressUnspent(any())).thenReturn(unspentResponse);

        ReceivedAmount receivedAmount = wallet.getReceivedAmount(ADDRESS, CRYPTOCURRENCY);

        assertEquals(BigDecimal.ZERO, receivedAmount.getTotalAmountReceived());
        assertEquals(0, receivedAmount.getConfirmations());
        assertNull(receivedAmount.getTransactionHashes());
    }

    @Test
    void testGetReceivedAmount_noIncomingTransactions() throws IOException {
        GetAddressUnspentElectrumResponse unspentResponse = new GetAddressUnspentElectrumResponse();
        GetAddressUnspentElectrumResponse.GetAddressUnspentResult result = createGetAddressUnspentResult(546L);
        unspentResponse.result = List.of(result);

        IncomingTransactionsSlpdbResponse incomingResponse = new IncomingTransactionsSlpdbResponse();
        incomingResponse.u = List.of();
        incomingResponse.c = List.of();

        when(api.getAddressUnspent(any())).thenReturn(unspentResponse);
        when(slpdbApi.getIncoimngTransactions(any())).thenReturn(incomingResponse);

        ReceivedAmount receivedAmount = wallet.getReceivedAmount(ADDRESS, CRYPTOCURRENCY);

        assertEquals(BigDecimal.ZERO, receivedAmount.getTotalAmountReceived());
        assertEquals(0, receivedAmount.getConfirmations());
        assertNull(receivedAmount.getTransactionHashes());
    }

    @Test
    void testGetReceivedAmount_noConfirmedIncomingTransactions() throws IOException {
        GetAddressUnspentElectrumResponse unspentResponse = new GetAddressUnspentElectrumResponse();
        GetAddressUnspentElectrumResponse.GetAddressUnspentResult result = createGetAddressUnspentResult(546L);
        unspentResponse.result = List.of(result);

        IncomingTransactionsSlpdbResponse incomingResponse = new IncomingTransactionsSlpdbResponse();
        incomingResponse.u = List.of(
            createUnconfirmedTransactionResult(BigDecimal.ONE),
            createUnconfirmedTransactionResult(BigDecimal.TEN)
        );
        incomingResponse.c = List.of();

        when(api.getAddressUnspent(any())).thenReturn(unspentResponse);
        when(slpdbApi.getIncoimngTransactions(any())).thenReturn(incomingResponse);

        ReceivedAmount receivedAmount = wallet.getReceivedAmount(ADDRESS, CRYPTOCURRENCY);

        assertEquals(BigDecimal.valueOf(11), receivedAmount.getTotalAmountReceived());
        assertEquals(0, receivedAmount.getConfirmations());
        assertNull(receivedAmount.getTransactionHashes());
    }

    @Test
    void testGetReceivedAmount_confirmedIncomingTransactions() throws IOException {
        GetAddressUnspentElectrumResponse unspentResponse = new GetAddressUnspentElectrumResponse();
        GetAddressUnspentElectrumResponse.GetAddressUnspentResult result = createGetAddressUnspentResult(546L);
        unspentResponse.result = List.of(result);

        IncomingTransactionsSlpdbResponse incomingResponse = new IncomingTransactionsSlpdbResponse();
        incomingResponse.u = List.of(
            createUnconfirmedTransactionResult(BigDecimal.ONE),
            createUnconfirmedTransactionResult(BigDecimal.TEN)
        );
        incomingResponse.c = List.of(
            createConfirmedTransactionResult(BigDecimal.ONE, 10),
            createConfirmedTransactionResult(BigDecimal.TEN, 20)
        );

        StatusSlpdbResponse statusResponse = new StatusSlpdbResponse();
        statusResponse.s = List.of(createStatusResult());

        when(api.getAddressUnspent(any())).thenReturn(unspentResponse);
        when(slpdbApi.getIncoimngTransactions(any())).thenReturn(incomingResponse);
        when(slpdbApi.getStatus(any())).thenReturn(statusResponse);

        ReceivedAmount receivedAmount = wallet.getReceivedAmount(ADDRESS, CRYPTOCURRENCY);

        assertEquals(BigDecimal.valueOf(22), receivedAmount.getTotalAmountReceived());
        assertEquals(31, receivedAmount.getConfirmations());
        assertNull(receivedAmount.getTransactionHashes());
    }

    @ParameterizedTest
    @ValueSource(classes = {
        HttpStatusIOException.class,
        ConnectException.class,
        RuntimeException.class
    })
    void testGetReceivedAmount_exception(Class<? extends Exception> exceptionClass) throws IOException {
        Exception exception = mock(exceptionClass);
        if (exceptionClass == HttpStatusIOException.class) {
            when(((HttpStatusIOException) exception).getHttpBody()).thenReturn("HTTP error");
            when(((HttpStatusIOException) exception).getHttpStatusCode()).thenReturn(500);
        }

        when(api.getAddressUnspent(any())).thenThrow(exception);

        ReceivedAmount result = wallet.getReceivedAmount(ADDRESS, CRYPTOCURRENCY);

        assertNull(result);
    }

    private static StatusSlpdbResponse.StatusResult createStatusResult() {
        StatusSlpdbResponse.StatusResult statusResult = new StatusSlpdbResponse.StatusResult();
        statusResult.bchBlockHeight = 50;
        return statusResult;
    }

    private static IncomingTransactionsSlpdbResponse.TransactionResult createUnconfirmedTransactionResult(BigDecimal amount) {
        IncomingTransactionsSlpdbResponse.TransactionResult transactionResult = new IncomingTransactionsSlpdbResponse.TransactionResult();
        transactionResult.amount = amount;
        transactionResult.height = null; // unconfirmed
        return transactionResult;
    }

    private static IncomingTransactionsSlpdbResponse.TransactionResult createConfirmedTransactionResult(BigDecimal amount, int height) {
        IncomingTransactionsSlpdbResponse.TransactionResult transactionResult = new IncomingTransactionsSlpdbResponse.TransactionResult();
        transactionResult.amount = amount;
        transactionResult.height = height;
        return transactionResult;
    }

    private static GetAddressUnspentElectrumResponse.GetAddressUnspentResult createGetAddressUnspentResult(long value) {
        GetAddressUnspentElectrumResponse.GetAddressUnspentResult result = new GetAddressUnspentElectrumResponse.GetAddressUnspentResult();
        result.value = value;
        return result;
    }

}