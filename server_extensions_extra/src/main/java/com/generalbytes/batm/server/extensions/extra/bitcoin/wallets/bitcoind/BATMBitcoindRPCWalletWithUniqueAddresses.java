package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitcoind;

import com.generalbytes.batm.server.extensions.HasUniqueReceivingCryptoAddresses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BATMBitcoindRPCWalletWithUniqueAddresses extends BATMBitcoindRPCWallet implements HasUniqueReceivingCryptoAddresses {
    private static final Logger log = LoggerFactory.getLogger(BATMBitcoindRPCWalletWithUniqueAddresses.class);

    public BATMBitcoindRPCWalletWithUniqueAddresses(String rpcURL, String cryptoCurrency) {
        super(rpcURL, cryptoCurrency);
    }

    @Override
    public String getUniqueReceivingCryptoAddress(String cryptoCurrency, String label) {
        return getNewCryptoAddress(cryptoCurrency, label);
    }
}
