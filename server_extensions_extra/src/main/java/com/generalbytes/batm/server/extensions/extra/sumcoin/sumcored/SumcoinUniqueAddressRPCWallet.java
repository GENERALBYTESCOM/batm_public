package com.generalbytes.batm.server.extensions.extra.sumcoin.sumcored;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;

public class SumcoinUniqueAddressRPCWallet extends SumcoinRPCWallet implements IGeneratesNewDepositCryptoAddress {
    public SumcoinUniqueAddressRPCWallet(String rpcURL, String accountName) {
        super(rpcURL, accountName);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        return getCryptoAddress(cryptoCurrency);
    }
}
