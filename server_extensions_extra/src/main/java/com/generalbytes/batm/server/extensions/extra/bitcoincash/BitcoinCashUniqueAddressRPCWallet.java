package com.generalbytes.batm.server.extensions.extra.bitcoincash;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;

public class BitcoinCashUniqueAddressRPCWallet extends BitcoinCashRPCWallet implements IGeneratesNewDepositCryptoAddress {
    public BitcoinCashUniqueAddressRPCWallet(String rpcURL, String accountName) {
        super(rpcURL, accountName);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        return getCryptoAddress(cryptoCurrency);
    }
}
