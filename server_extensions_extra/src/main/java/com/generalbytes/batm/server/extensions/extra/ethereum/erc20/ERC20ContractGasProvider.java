package com.generalbytes.batm.server.extensions.extra.ethereum.erc20;

import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.generated.ERC20Interface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.gas.ContractGasProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;

public class ERC20ContractGasProvider implements ContractGasProvider {
    private static final Logger log = LoggerFactory.getLogger(ERC20ContractGasProvider.class);

    private final BigDecimal gasPriceMultiplier;
    private final BigInteger fixedGasLimit;
    private final Web3j w;
    private final String toAddress;
    private final BigInteger tokensAmount; // converted to the smallest token unit (tokens * 10^decimalPlaces)
    private final String fromAddress;
    private final String contractAddress;

    public ERC20ContractGasProvider(String contractAddress, String fromAddress, String toAddress, BigInteger tokensAmount, BigInteger fixedGasLimit, BigDecimal gasPriceMultiplier, Web3j w) {
        this.contractAddress = contractAddress.toLowerCase();
        this.fromAddress = fromAddress.toLowerCase();
        this.toAddress = toAddress.toLowerCase();
        this.tokensAmount = tokensAmount;
        this.gasPriceMultiplier = gasPriceMultiplier;
        this.fixedGasLimit = fixedGasLimit;
        this.w = w;
    }

    @Override
    public BigInteger getGasPrice(String contractFunc) {
        try {
            BigInteger gasPriceWei = w.ethGasPrice().send().getGasPrice();
            BigInteger gasPriceMultiplied = new BigDecimal(gasPriceWei).multiply(gasPriceMultiplier).toBigInteger();
            log.info("gas price: {} * {} = {} wei", gasPriceWei, gasPriceMultiplier, gasPriceMultiplied);
            return gasPriceMultiplied;
        } catch (IOException e) {
            log.error("error getting gas price, using default", e);
            return ManagedTransaction.GAS_PRICE;
        }
    }

    @Override
    public BigInteger getGasPrice() {
        return null; // not called
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
