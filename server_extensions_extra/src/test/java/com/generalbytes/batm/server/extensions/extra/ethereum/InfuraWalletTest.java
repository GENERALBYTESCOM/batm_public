package com.generalbytes.batm.server.extensions.extra.ethereum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.RawTransactionManager;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InfuraWalletTest {

    private static final String VALID_DESTINATION = "0xd48144bf3a67c8ffebec3a73a07ed3bff96bd93f";
    private static final BigDecimal SEND_AMOUNT = BigDecimal.ONE;
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(21_000L);
    private static final BigInteger BASE_FEE = BigInteger.valueOf(10_000_000_000L); // 10 Gwei

    @Mock
    private Web3j web3j;

    private InfuraWallet wallet;

    @BeforeEach
    void setUp() {
        try (MockedStatic<Web3j> mockedWeb3j = mockStatic(Web3j.class)) {
            mockedWeb3j.when(() -> Web3j.build(any())).thenReturn(web3j);
            wallet = new InfuraWallet("projectId", "password");
        }
    }

    @Test
    void testSendCoins_unknownCryptoCurrency() {
        String result = wallet.sendCoins(VALID_DESTINATION, SEND_AMOUNT, "BTC", "desc");

        assertNull(result);
        verifyNoInteractions(web3j);
    }

    @Test
    void testSendCoins_nullDestinationAddress() {
        String result = wallet.sendCoins(null, SEND_AMOUNT, "ETH", "desc");

        assertNull(result);
        verifyNoInteractions(web3j);
    }

    @Test
    void testSendCoins_invalidEthAddress() {
        String result = wallet.sendCoins("notAnAddress", SEND_AMOUNT, "ETH", "desc");

        assertNull(result);
        verifyNoInteractions(web3j);
    }

    @Test
    void testSendCoins_nonIntegerWeiAmount() {
        BigDecimal nonIntegerWeiAmount = new BigDecimal("1.0000000000000000001");

        String result = wallet.sendCoins(VALID_DESTINATION, nonIntegerWeiAmount, "ETH", "desc");

        assertNull(result);
        verifyNoInteractions(web3j);
    }

    @Test
    void testSendCoins_errorEstimatingGas() throws Exception {
        Request<?, EthEstimateGas> request = mock(Request.class);
        EthEstimateGas estimateGas = mock(EthEstimateGas.class);
        Response.Error error = mock(Response.Error.class);
        when(web3j.ethEstimateGas(any())).thenReturn((Request) request);
        when(request.send()).thenReturn(estimateGas);
        when(estimateGas.hasError()).thenReturn(true);
        when(estimateGas.getError()).thenReturn(error);
        when(error.getMessage()).thenReturn("gas estimate error");

        String result = wallet.sendCoins(VALID_DESTINATION, SEND_AMOUNT, "ETH", "desc");

        assertNull(result);
    }

    @Test
    void testSendCoins_errorFetchingBaseFee() throws Exception {
        mockGasEstimate();

        Request<?, EthBlock> blockRequest = mock(Request.class);
        EthBlock ethBlock = mock(EthBlock.class);
        EthBlock.Block block = mock(EthBlock.Block.class);
        when(web3j.ethGetBlockByNumber(DefaultBlockParameterName.PENDING, false)).thenReturn((Request) blockRequest);
        when(blockRequest.send()).thenReturn(ethBlock);
        when(ethBlock.getBlock()).thenReturn(block);
        when(block.getBaseFeePerGas()).thenReturn(null);

        String result = wallet.sendCoins(VALID_DESTINATION, SEND_AMOUNT, "ETH", "desc");

        assertNull(result);
    }

    @Test
    void testSendCoins_transactionError() throws Exception {
        mockGasEstimate();
        mockBaseFee();

        try (MockedConstruction<RawTransactionManager> mockedTxManager = mockConstruction(RawTransactionManager.class,
                (mockTxMgr, context) -> {
                    EthSendTransaction tx = mock(EthSendTransaction.class);
                    Response.Error error = mock(Response.Error.class);
                    when(error.getMessage()).thenReturn("tx error");
                    when(tx.hasError()).thenReturn(true);
                    when(tx.getError()).thenReturn(error);
                    when(mockTxMgr.sendEIP1559Transaction(anyLong(), any(), any(), any(), any(), any(), any(), anyBoolean()))
                        .thenReturn(tx);
                })) {

            String result = wallet.sendCoins(VALID_DESTINATION, SEND_AMOUNT, "ETH", "desc");

            assertNull(result);
        }
    }

    @Test
    void testSendCoins_success() throws Exception {
        mockGasEstimate();
        mockBaseFee();

        try (MockedConstruction<RawTransactionManager> mockedTxManager = mockConstruction(RawTransactionManager.class,
                (mockTxMgr, context) -> {
                    EthSendTransaction tx = mock(EthSendTransaction.class);
                    when(tx.hasError()).thenReturn(false);
                    when(tx.getTransactionHash()).thenReturn("0xtxhash");
                    when(mockTxMgr.sendEIP1559Transaction(anyLong(), any(), any(), any(), any(), any(), any(), anyBoolean()))
                        .thenReturn(tx);
                })) {

            String result = wallet.sendCoins(VALID_DESTINATION, SEND_AMOUNT, "ETH", "desc");

            assertEquals("0xtxhash", result);
        }
    }

    private void mockGasEstimate() throws Exception {
        Request<?, EthEstimateGas> request = mock(Request.class);
        EthEstimateGas estimateGas = mock(EthEstimateGas.class);
        when(web3j.ethEstimateGas(any())).thenReturn((Request) request);
        when(request.send()).thenReturn(estimateGas);
        when(estimateGas.hasError()).thenReturn(false);
        when(estimateGas.getAmountUsed()).thenReturn(GAS_LIMIT);
    }

    private void mockBaseFee() throws Exception {
        Request<?, EthBlock> blockRequest = mock(Request.class);
        EthBlock ethBlock = mock(EthBlock.class);
        EthBlock.Block block = mock(EthBlock.Block.class);
        when(web3j.ethGetBlockByNumber(DefaultBlockParameterName.PENDING, false)).thenReturn((Request) blockRequest);
        when(blockRequest.send()).thenReturn(ethBlock);
        when(ethBlock.getBlock()).thenReturn(block);
        when(block.getBaseFeePerGas()).thenReturn(BASE_FEE);
    }
}
