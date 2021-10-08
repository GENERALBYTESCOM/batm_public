package com.generalbytes.batm.server.extensions.extra.ethereum.erc20;

import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;

public enum DummyContractGasProvider implements ContractGasProvider {
    INSTANCE;

    @Override
    public BigInteger getGasPrice(String contractFunc) {
        throw new UnsupportedOperationException("DummyContractGasProvider cannot be used");
    }

    @Override
    public BigInteger getGasPrice() {
        throw new UnsupportedOperationException("DummyContractGasProvider cannot be used");
    }

    @Override
    public BigInteger getGasLimit(String contractFunc) {
        throw new UnsupportedOperationException("DummyContractGasProvider cannot be used");
    }

    @Override
    public BigInteger getGasLimit() {
        throw new UnsupportedOperationException("DummyContractGasProvider cannot be used");
    }
}
