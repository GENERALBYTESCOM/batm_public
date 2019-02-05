package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.service;

import java.io.IOException;
import java.util.Collection;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.DVChainAdapters;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.marketdata.DVChainMarketData;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.marketdata.DVChainMarketResponse;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.marketdata.MarketDataService;

public class DVChainMarketDataService extends DVChainMarketDataServiceRaw
    implements MarketDataService {
  public DVChainMarketDataService(Exchange exchange) {
    super(exchange);
  }

  @Override
  public OrderBook getOrderBook(CurrencyPair currencyPair, Object... args)
      throws IOException {
    DVChainMarketResponse marketResponse = getMarketData();
    DVChainMarketData marketData =
        marketResponse.getMarketData().get(currencyPair.base.getSymbol());
    return DVChainAdapters.adaptOrderBook(marketData, marketData.getExpiresAt(), currencyPair);
  }

  @Override
  public Trades getTrades(CurrencyPair currencyPair, Object... args) throws NotYetImplementedForExchangeException {
    throw new NotYetImplementedForExchangeException();
  }

  @Override
  public Ticker getTicker(CurrencyPair currencyPair, Object... args) throws NotYetImplementedForExchangeException {
    throw new NotYetImplementedForExchangeException();
  }
}
