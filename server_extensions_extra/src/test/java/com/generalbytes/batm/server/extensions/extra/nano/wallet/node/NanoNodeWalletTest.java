package com.generalbytes.batm.server.extensions.extra.nano.wallet.node;

import com.generalbytes.batm.server.extensions.extra.nano.NanoExtensionContext;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoRpcClient;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoWsClient;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.RpcException;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.dto.AccountBalance;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.dto.Block;
import com.generalbytes.batm.server.extensions.extra.nano.util.NanoUtil;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NanoNodeWalletTest {

    private static final String TEST_ADDRESS = "testAddress";
    private static final String TEST_CRYPTOCURRENCY = "testCryptoCurrency";

    @Mock
    private NanoExtensionContext nanoExtensionContext;
    @Mock
    private NanoRpcClient nanoRpcClient;
    @Mock
    private NanoWsClient nanoWsClient;
    @Mock
    private NanoUtil nanoUtil;

    private NanoNodeWallet nanoNodeWallet;

    @BeforeEach
    void setUp() {
        nanoNodeWallet = new NanoNodeWallet(
                nanoExtensionContext,
                nanoRpcClient,
                nanoWsClient,
                "testWalletId",
                "testHotWalletAccount"
        );
    }

    @ParameterizedTest
    @ValueSource(classes = {IOException.class, RpcException.class})
    void testGetReceivedAmount_exception(Class<? extends Exception> exceptionType) throws IOException, RpcException {
        when(nanoRpcClient.getBalance(TEST_ADDRESS)).thenThrow(exceptionType);
        when(nanoExtensionContext.getUtil()).thenReturn(nanoUtil);
        when(nanoUtil.parseAddress(TEST_ADDRESS)).thenReturn(TEST_ADDRESS);

        ReceivedAmount receivedAmount = nanoNodeWallet.getReceivedAmount(TEST_ADDRESS, TEST_CRYPTOCURRENCY);

        assertNull(receivedAmount);
    }

    @ParameterizedTest
    @ValueSource(classes = {IOException.class, RpcException.class})
    void testGetReceivedAmount_exceptionOnTransactionHistory(Class<? extends Exception> exceptionType) throws IOException, RpcException {
        AccountBalance accountBalance = new AccountBalance(BigInteger.TEN, BigInteger.TEN, BigInteger.TEN);

        when(nanoRpcClient.getBalance(TEST_ADDRESS)).thenReturn(accountBalance);
        when(nanoExtensionContext.getUtil()).thenReturn(nanoUtil);
        when(nanoUtil.parseAddress(TEST_ADDRESS)).thenReturn(TEST_ADDRESS);
        when(nanoUtil.amountFromRaw(any(BigInteger.class))).thenAnswer(invocation -> {
            BigInteger rawAmount = invocation.getArgument(0);
            return new BigDecimal(rawAmount);
        });
        when(nanoRpcClient.getTransactionHistory(TEST_ADDRESS)).thenThrow(exceptionType);

        ReceivedAmount receivedAmount = nanoNodeWallet.getReceivedAmount(TEST_ADDRESS, TEST_CRYPTOCURRENCY);

        assertEquals(BigDecimal.TEN, receivedAmount.getTotalAmountReceived());
        assertEquals(Integer.MAX_VALUE, receivedAmount.getConfirmations());
        assertTrue(receivedAmount.getTransactionHashes().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            // expected amount, confirmed balance, unconfirmed balance, pending balance, expected confirmations
            // Zero confirmed balance
            "500,0,500,0,0",
            "700,0,500,200,0",
            "200,0,0,200,0",
            "0,0,0,0,0",
            // Non-zero confirmed balance
            "400,400,0,0," + Integer.MAX_VALUE,
            "400,400,100,200," + Integer.MAX_VALUE,
    })
    void testGetReceivedAmount(BigDecimal expectedAmount,
                               BigInteger confirmedBalance,
                               BigInteger unconfirmedBalance,
                               BigInteger pendingBalance,
                               int expectedConfirmations) throws IOException, RpcException {
        AccountBalance accountBalance = new AccountBalance(confirmedBalance, unconfirmedBalance, pendingBalance);

        when(nanoRpcClient.getBalance(TEST_ADDRESS)).thenReturn(accountBalance);
        when(nanoExtensionContext.getUtil()).thenReturn(nanoUtil);
        when(nanoUtil.parseAddress(TEST_ADDRESS)).thenReturn(TEST_ADDRESS);
        when(nanoUtil.amountFromRaw(any(BigInteger.class))).thenAnswer(invocation -> {
            BigInteger rawAmount = invocation.getArgument(0);
            return new BigDecimal(rawAmount);
        });
        when(nanoRpcClient.getTransactionHistory(TEST_ADDRESS)).thenReturn(List.of(
                new Block("receive", TEST_ADDRESS, BigInteger.TEN, "testHash1"),
                new Block("send", TEST_ADDRESS, BigInteger.TEN, "testHash2"), // invalid transaction type
                new Block("receive", TEST_ADDRESS, BigInteger.TEN, "testHash3"),
                new Block("receive", TEST_ADDRESS, BigInteger.TEN, ""), // invalid hash
                new Block("receive", TEST_ADDRESS, BigInteger.TEN, "  "), // invalid hash
                new Block("receive", TEST_ADDRESS, BigInteger.TEN, null) // invalid hash
        ));

        ReceivedAmount receivedAmount = nanoNodeWallet.getReceivedAmount(TEST_ADDRESS, TEST_CRYPTOCURRENCY);

        assertEquals(expectedAmount, receivedAmount.getTotalAmountReceived());
        assertEquals(expectedConfirmations, receivedAmount.getConfirmations());
        assertEquals(2, receivedAmount.getTransactionHashes().size());
        assertEquals("testHash1", receivedAmount.getTransactionHashes().get(0));
        assertEquals("testHash3", receivedAmount.getTransactionHashes().get(1));
    }

}