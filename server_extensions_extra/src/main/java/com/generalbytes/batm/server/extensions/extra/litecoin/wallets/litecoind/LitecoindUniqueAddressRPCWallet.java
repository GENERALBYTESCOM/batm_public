package com.generalbytes.batm.server.extensions.extra.litecoin.wallets.litecoind;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;

public class LitecoindUniqueAddressRPCWallet extends LitecoindRPCWallet implements IGeneratesNewDepositCryptoAddress {
    public LitecoindUniqueAddressRPCWallet(String rpcURL, String accountName) {
        super(rpcURL, accountName);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        return getCryptoAddress(cryptoCurrency);
    }
}
