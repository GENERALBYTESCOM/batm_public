package com.generalbytes.batm.server.extensions.extra.elrond;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.CoinZixExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class ElrondExtension extends AbstractExtension {
    private IExtensionContext ctx;
    private static final Logger log = LoggerFactory.getLogger(ElrondExtension.class);

    @Override
    public void init(IExtensionContext ctx) {
        this.ctx = ctx;
        log.info("Elrond ctx - {} -", ctx);
    }

    @Override
    public String getName() {
        return "BATM Elrond extra extension";
    }


    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.EGLD.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new ElrondAddressValidator();
        }
        return null;
    }

}
