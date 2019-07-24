package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitcoind;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BATMBitcoindRPCWalletWithUniqueAddresses extends BATMBitcoindRPCWallet implements IGeneratesNewDepositCryptoAddress {
    private static final Logger log = LoggerFactory.getLogger(BATMBitcoindRPCWalletWithUniqueAddresses.class);

    public BATMBitcoindRPCWalletWithUniqueAddresses(String rpcURL, String cryptoCurrency) {
        super(rpcURL, cryptoCurrency);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        return getNewCryptoAddress(cryptoCurrency, label);
    }
}
