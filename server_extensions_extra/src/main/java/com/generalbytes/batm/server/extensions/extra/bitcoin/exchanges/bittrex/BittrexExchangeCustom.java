package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex;

import org.knowm.xchange.bittrex.BittrexExchange;
import org.knowm.xchange.bittrex.service.BittrexMarketDataService;
import org.knowm.xchange.bittrex.service.BittrexTradeService;
import org.knowm.xchange.client.ExchangeRestProxyBuilder;

/**
 * Temporary solution. Remove when withdrawal will be available in org.knowm.xchange:xchange-bitfinex
 */
//TODO: BATM-2327 remove this class
public class BittrexExchangeCustom extends BittrexExchange {

    protected void initServices() {
        BittrexAuthenticatedCustom bittrex = ExchangeRestProxyBuilder.forInterface(BittrexAuthenticatedCustom.class, this.getExchangeSpecification()).build();
        this.marketDataService = new BittrexMarketDataService(this, bittrex, this.getResilienceRegistries());
        this.accountService = new BittrexAccountServiceCustom(this, bittrex, this.getResilienceRegistries());
        this.tradeService = new BittrexTradeService(this, bittrex, this.getResilienceRegistries());
    }
}
