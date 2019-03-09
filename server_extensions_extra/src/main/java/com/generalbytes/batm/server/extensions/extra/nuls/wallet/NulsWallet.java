package com.generalbytes.batm.server.extensions.extra.nuls.wallet;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.lisk.wallets.liskbinancewallet.BinanceWallet;

import java.util.HashSet;
import java.util.Set;

public class NulsWallet extends BinanceWallet {

    public NulsWallet(String address, String binanceApiKey, String binanceApiSecret) {
        super(address,binanceApiKey,binanceApiSecret);
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CryptoCurrency.NULS.getCode();
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.NULS.getCode());
        return result;
    }
}
