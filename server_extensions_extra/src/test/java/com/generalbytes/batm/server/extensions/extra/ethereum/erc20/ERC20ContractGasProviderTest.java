package com.generalbytes.batm.server.extensions.extra.ethereum.erc20;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ERC20ContractGasProviderTest {

    @Mock
    private Web3j web3j;

    private ERC20ContractGasProvider gasProvider;

    private static final String CONTRACT_ADDRESS = "0xdac17f958d2ee523a2206206994597c13d831ec7";
    private static final String FROM_ADDRESS = "0xd48144bf3a67c8ffebec3a73a07ed3bff96bd93f";
    private static final String TO_ADDRESS = "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48";
    private static final BigInteger TOKENS_AMOUNT = BigInteger.valueOf(1_000_000);
    private static final BigInteger BASE_FEE = Convert.toWei("10", Convert.Unit.GWEI).toBigInteger();

    @BeforeEach
    void setUp() {
        gasProvider = new ERC20ContractGasProvider(CONTRACT_ADDRESS, FROM_ADDRESS, TO_ADDRESS, TOKENS_AMOUNT, null, web3j);
    }

    @Test
    void testIsEIP1559Enabled() {
        assertTrue(gasProvider.isEIP1559Enabled());
        verifyNoInteractions(web3j);
    }

    @Test
    void testGetChainId() {
        assertEquals(1L, gasProvider.getChainId());
        verifyNoInteractions(web3j);
    }

    @Test
    void testGetMaxPriorityFeePerGas() {
        BigInteger expected = Convert.toWei("1.5", Convert.Unit.GWEI).toBigInteger();

        assertEquals(expected, gasProvider.getMaxPriorityFeePerGas("transfer"));
        verifyNoInteractions(web3j);
    }

    @Test
    void testGetMaxFeePerGas_success() throws Exception {
        mockBaseFee(BASE_FEE);

        BigInteger result = gasProvider.getMaxFeePerGas("transfer");

        // 2 * 10 Gwei + 1.5 Gwei = 21.5 Gwei
        assertEquals(Convert.toWei("21.5", Convert.Unit.GWEI).toBigInteger(), result);
    }

    @Test
    void testGetMaxFeePerGas_baseFeeIsNull() throws Exception {
        mockBaseFee(null);

        UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> gasProvider.getMaxFeePerGas("transfer"));
        assertEquals("Failed to fetch baseFee for EIP-1559 transaction", exception.getMessage());
    }

    @Test
    void testGetMaxFeePerGas_baseFeeIsZero() throws Exception {
        mockBaseFee(BigInteger.ZERO);

        UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> gasProvider.getMaxFeePerGas("transfer"));
        assertEquals("Failed to fetch baseFee for EIP-1559 transaction", exception.getMessage());
    }

    @Test
    void testGetMaxFeePerGas_ioException() throws Exception {
        Request<?, EthBlock> blockRequest = mock(Request.class);
        when(web3j.ethGetBlockByNumber(DefaultBlockParameterName.PENDING, false)).thenReturn((Request) blockRequest);
        when(blockRequest.send()).thenThrow(new IOException("network error"));

        UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> gasProvider.getMaxFeePerGas("transfer"));
        assertEquals("Failed to fetch baseFee for EIP-1559 transaction", exception.getMessage());
    }

    @Test
    void testGetGasLimit_illegalFunction() {
        assertNull(gasProvider.getGasLimit("approve"));
        verifyNoInteractions(web3j);
    }

    @Test
    void testGetGasLimit_fixedGasLimit() {
        ERC20ContractGasProvider fixedProvider = new ERC20ContractGasProvider(
            CONTRACT_ADDRESS, FROM_ADDRESS, TO_ADDRESS, TOKENS_AMOUNT, BigInteger.valueOf(100_000), web3j);

        assertEquals(BigInteger.valueOf(100_000), fixedProvider.getGasLimit("transfer"));
        verifyNoInteractions(web3j);
    }

    @Test
    void testGetGasLimit_estimateSucceeds() throws Exception {
        mockGasEstimate(BigInteger.valueOf(60_000));

        // ceil(60_000 * 1.1) = 66_000
        assertEquals(BigInteger.valueOf(66_000), gasProvider.getGasLimit("transfer"));
    }

    @Test
    void testGetGasLimit_estimateHasError() throws Exception {
        mockGasEstimateError("gas required exceeds allowance (14124)");

        assertNull(gasProvider.getGasLimit("transfer"));
    }

    @Test
    void testGetGasLimit_estimateThrowsIoException() throws Exception {
        Request<?, EthEstimateGas> request = mock(Request.class);
        when(web3j.ethEstimateGas(any())).thenReturn((Request) request);
        when(request.send()).thenThrow(new IOException("network error"));

        assertNull(gasProvider.getGasLimit("transfer"));
    }

    private void mockBaseFee(BigInteger baseFee) throws Exception {
        Request<?, EthBlock> blockRequest = mock(Request.class);
        EthBlock ethBlock = mock(EthBlock.class);
        EthBlock.Block block = mock(EthBlock.Block.class);
        when(web3j.ethGetBlockByNumber(DefaultBlockParameterName.PENDING, false)).thenReturn((Request) blockRequest);
        when(blockRequest.send()).thenReturn(ethBlock);
        when(ethBlock.getBlock()).thenReturn(block);
        when(block.getBaseFeePerGas()).thenReturn(baseFee);
    }

    private void mockGasEstimate(BigInteger gasAmount) throws Exception {
        Request<?, EthEstimateGas> request = mock(Request.class);
        EthEstimateGas estimateGas = mock(EthEstimateGas.class);
        when(web3j.ethEstimateGas(any())).thenReturn((Request) request);
        when(request.send()).thenReturn(estimateGas);
        when(estimateGas.hasError()).thenReturn(false);
        when(estimateGas.getAmountUsed()).thenReturn(gasAmount);
    }

    private void mockGasEstimateError(String errorMessage) throws Exception {
        Request<?, EthEstimateGas> request = mock(Request.class);
        EthEstimateGas estimateGas = mock(EthEstimateGas.class);
        Response.Error error = mock(Response.Error.class);
        when(web3j.ethEstimateGas(any())).thenReturn((Request) request);
        when(request.send()).thenReturn(estimateGas);
        when(estimateGas.hasError()).thenReturn(true);
        when(estimateGas.getError()).thenReturn(error);
        when(error.getMessage()).thenReturn(errorMessage);
    }
}
