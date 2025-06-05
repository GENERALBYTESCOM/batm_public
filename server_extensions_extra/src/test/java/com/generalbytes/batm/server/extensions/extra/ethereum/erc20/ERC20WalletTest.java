package com.generalbytes.batm.server.extensions.extra.ethereum.erc20;

import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.generated.ERC20Interface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ERC20WalletTest {

    @Mock
    private Web3j web3j;
    @Mock
    private ERC20Interface erc20Interface;
    private ERC20Wallet wallet;

    @BeforeEach
    void setUp() {
        try (MockedStatic<Web3j> mockedWeb3j = mockStatic(Web3j.class);
             MockedStatic<ERC20Interface> mockedERC20Interface = mockStatic(ERC20Interface.class)) {
            mockedWeb3j.when(() -> Web3j.build(any())).thenReturn(web3j);
            mockedERC20Interface.when(() -> ERC20Interface.load(
                    eq("contractaddress"),
                    eq(web3j),
                    any(Credentials.class),
                    eq(DummyContractGasProvider.INSTANCE)
            )).thenReturn(erc20Interface);

            wallet = new ERC20Wallet(
                    "projectId",
                    "password",
                    "USDT",
                    1,
                    "contractAddress",
                    null,
                    null
            );


        }
    }

    @Test
    void testGetPreferredCryptocurrency() {
        assertEquals("USDT", wallet.getPreferredCryptoCurrency());
    }

    @Test
    void testGetCryptocurrencies() {
        Set<String> result = wallet.getCryptoCurrencies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains("USDT"));
    }

    @Test
    void testGetCryptoAddress_unsupportedCryptocurrency() {
        assertNull(wallet.getCryptoAddress("unsupportedCryptocurrency"));
    }

    @Test
    void testGetCryptoAddress() {
        assertEquals("0xd48144bf3a67c8ffebec3a73a07ed3bff96bd93f", wallet.getCryptoAddress("USDT"));
    }

    @Test
    void testGetCryptoBalance_unsupportedCryptocurrency() {
        BigDecimal result = wallet.getCryptoBalance("unsupportedCryptocurrency");

        assertNull(result);
        verifyNoInteractions(erc20Interface, web3j);
    }

    @Test
    void testGetCryptoBalance_nullResult() throws Exception {
        mockBalance(null);

        BigDecimal result = wallet.getCryptoBalance("USDT");

        assertNull(result);
    }

    @Test
    void testGetCryptoBalance() throws Exception {
        mockBalance(BigInteger.valueOf(1000));

        BigDecimal result = wallet.getCryptoBalance("USDT");

        assertNotNull(result);
        assertEquals(0, BigDecimal.valueOf(100).compareTo(result));
    }

    @Test
    void testGetCryptoBalance_exception() throws Exception {
        RemoteFunctionCall<BigInteger> getBalanceFunction = mock(RemoteFunctionCall.class);

        when(getBalanceFunction.send()).thenThrow(new RuntimeException("Test Exception"));
        when(erc20Interface.balanceOf(any())).thenReturn(getBalanceFunction);

        BigDecimal result = wallet.getCryptoBalance("USDT");

        assertNull(result);
    }

    @Test
    void testSendCoins_unsupportedCryptocurrency() {
        String result = wallet.sendCoins("destinationAddress", BigDecimal.valueOf(100), "unsupportedCryptocurrency", "description");

        assertNull(result);
        verifyNoInteractions(erc20Interface, web3j);
    }

    private static Object[] provideInsufficientBalance() {
        return new Object[]{
                null,
                BigInteger.valueOf(10)
        };
    }

    @ParameterizedTest
    @MethodSource("provideInsufficientBalance")
    void testSendCoins_insufficientBalance(BigInteger balance) throws Exception {
        mockBalance(balance);

        String result = wallet.sendCoins("destinationAddress", BigDecimal.valueOf(100), "USDT", "description");

        assertNull(result);
        verifyNoInteractions(web3j);
    }

    @Test
    void testSendCoins() throws Exception {
        mockBalance(BigInteger.valueOf(10_000));

        try (MockedStatic<ERC20Interface> mockedERC20Interface = mockStatic(ERC20Interface.class)) {
            mockedERC20Interface.when(() -> ERC20Interface.load(
                    eq("contractaddress"),
                    eq(web3j),
                    any(Credentials.class),
                    any(ERC20ContractGasProvider.class)
            )).thenReturn(erc20Interface);
            TransactionReceipt transactionReceipt = new TransactionReceipt();
            transactionReceipt.setTransactionHash("transactionHash");

            RemoteFunctionCall<TransactionReceipt> transferFunction = mock(RemoteFunctionCall.class);
            when(transferFunction.send()).thenReturn(transactionReceipt);
            when(erc20Interface.transfer("destinationaddress", BigInteger.valueOf(1000))).thenReturn(transferFunction);

            String result = wallet.sendCoins("destinationAddress", BigDecimal.valueOf(100), "USDT", "description");

            assertEquals("transactionHash", result);
        }
    }

    @Test
    void testSendCoins_exceptionOnSend() throws Exception {
        mockBalance(BigInteger.valueOf(10_000));

        try (MockedStatic<ERC20Interface> mockedERC20Interface = mockStatic(ERC20Interface.class)) {
            mockedERC20Interface.when(() -> ERC20Interface.load(
                    eq("contractaddress"),
                    eq(web3j),
                    any(Credentials.class),
                    any(ERC20ContractGasProvider.class)
            )).thenReturn(erc20Interface);
            RemoteFunctionCall<TransactionReceipt> transferFunction = mock(RemoteFunctionCall.class);
            when(transferFunction.send()).thenThrow(new RuntimeException("Test Exception"));
            when(erc20Interface.transfer("destinationaddress", BigInteger.valueOf(1000))).thenReturn(transferFunction);

            String result = wallet.sendCoins("destinationAddress", BigDecimal.valueOf(100), "USDT", "description");

            assertNull(result);
        }
    }

    private void mockBalance(BigInteger balance) throws Exception {
        RemoteFunctionCall<BigInteger> getBalanceFunction = mock(RemoteFunctionCall.class);

        when(getBalanceFunction.send()).thenReturn(balance);
        when(erc20Interface.balanceOf(any())).thenReturn(getBalanceFunction);
    }
}