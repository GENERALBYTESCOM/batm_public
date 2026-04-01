package com.generalbytes.batm.server.extensions.extra.ethereum.erc20;

import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.generated.ERC20Interface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.tx.gas.ContractEIP1559GasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;

public class ERC20ContractGasProvider implements ContractEIP1559GasProvider {
    private static final Logger log = LoggerFactory.getLogger(ERC20ContractGasProvider.class);
    private static final long ETH_MAINNET_CHAIN_ID = 1L;
    private static final BigInteger MAX_PRIORITY_FEE_PER_GAS = Convert.toWei("1.5", Convert.Unit.GWEI).toBigInteger();

    private final BigInteger fixedGasLimit;
    private final Web3j w;
    private final String toAddress;
    private final BigInteger tokensAmount; // converted to the smallest token unit (tokens * 10^decimalPlaces)
    private final String fromAddress;
    private final String contractAddress;

    public ERC20ContractGasProvider(String contractAddress, String fromAddress, String toAddress, BigInteger tokensAmount, BigInteger fixedGasLimit, Web3j w) {
        this.contractAddress = contractAddress.toLowerCase();
        this.fromAddress = fromAddress.toLowerCase();
        this.toAddress = toAddress.toLowerCase();
        this.tokensAmount = tokensAmount;
        this.fixedGasLimit = fixedGasLimit;
        this.w = w;
    }

    @Override
    public BigInteger getGasPrice(String contractFunc) {
        return null; // not called
    }

    @Override
    public BigInteger getGasPrice() {
        return null; // not called
    }

    @Override
    public boolean isEIP1559Enabled() {
        return true;
    }

    @Override
    public long getChainId() {
        return ETH_MAINNET_CHAIN_ID;
    }

    @Override
    public BigInteger getMaxPriorityFeePerGas(String contractFunc) {
        return MAX_PRIORITY_FEE_PER_GAS;
    }

    @Override
    public BigInteger getMaxFeePerGas(String contractFunc) {
        try {
            BigInteger baseFee = fetchBaseFee();
            BigInteger maxFeePerGas = baseFee.multiply(BigInteger.TWO).add(MAX_PRIORITY_FEE_PER_GAS);
            log.info("getMaxFeePerGas - baseFee = {} Gwei, maxFeePerGas = {} Gwei",
                Convert.fromWei(new BigDecimal(baseFee), Convert.Unit.GWEI),
                Convert.fromWei(new BigDecimal(maxFeePerGas), Convert.Unit.GWEI)
            );
            return maxFeePerGas;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to fetch baseFee for EIP-1559 transaction", e);
        }
    }

    private BigInteger fetchBaseFee() throws IOException {
        EthBlock.Block pendingBlock = w.ethGetBlockByNumber(DefaultBlockParameterName.PENDING, false).send().getBlock();
        BigInteger baseFee = pendingBlock.getBaseFeePerGas();
        if (baseFee == null || baseFee.compareTo(BigInteger.ZERO) <= 0) {
            throw new IOException("Could not fetch valid baseFee from pending block, got: " + baseFee);
        }
        return baseFee;
    }

    @Override
    public BigInteger getGasLimit(String contractFunc) {
        if (!ERC20Interface.FUNC_TRANSFER.equals(contractFunc)) {
            log.error("Illegal contract function {}", contractFunc);
            return null;
        }

        if (fixedGasLimit != null) {
            log.debug("Using fixed gasLimit: " + fixedGasLimit);
            return fixedGasLimit;
        }

        //get gas estimate for the transaction
        BigInteger transferGasEstimate = getTransferGasEstimate();
        if (transferGasEstimate == null) {
            return null;
        }

        //Make gas limit 10% higher than estimate just to be safe
        return new BigDecimal(transferGasEstimate).multiply(new BigDecimal("1.1")).setScale(0, RoundingMode.UP).toBigInteger();
    }

    private BigInteger getTransferGasEstimate() {
        final Function function = new Function(
            ERC20Interface.FUNC_TRANSFER,
            Arrays.asList(new Address(toAddress), new Uint256(tokensAmount)),
            Collections.emptyList());

        Transaction tx = Transaction.createEthCallTransaction(fromAddress, contractAddress, FunctionEncoder.encode(function));
        try {
            EthEstimateGas estimateGas = w.ethEstimateGas(tx).send();
            if (estimateGas.hasError()) {
                log.error("Error getting gas estimate: {}", estimateGas.getError().getMessage());
                return null;
            }
            BigInteger gasLimit = estimateGas.getAmountUsed();
            log.debug("Calculated gasLimit is: " + gasLimit);
            return gasLimit;

        } catch (IOException e) {
            log.error("Error", e);
        }
        return null;
    }


    @Override
    public BigInteger getGasLimit() {
        return null; //deprecated
    }
}
