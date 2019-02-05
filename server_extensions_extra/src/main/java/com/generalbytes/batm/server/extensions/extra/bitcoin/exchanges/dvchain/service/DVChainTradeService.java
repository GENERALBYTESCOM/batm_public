package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.service;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.DVChainAdapters;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.marketdata.DVChainLevel;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.marketdata.DVChainMarketData;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.marketdata.DVChainMarketResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.trade.DVChainNewLimitOrder;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.trade.DVChainNewMarketOrder;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.exceptions.FundsExceededException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public class DVChainTradeService extends DVChainTradeServiceRaw implements TradeService {
  private DVChainMarketDataService marketDataService;

  public DVChainTradeService(DVChainMarketDataService marketDataService, Exchange exchange) {
    super(exchange);
    this.marketDataService = marketDataService;
  }

  @Override
  public boolean cancelOrder(String orderId) throws IOException {
    return cancelDVChainOrder(orderId).equals("");
  }

  @Override
  public boolean cancelOrder(CancelOrderParams orderParams) throws NotYetImplementedForExchangeException {
    throw new NotYetImplementedForExchangeException();
  }


  private BigDecimal getPriceForMarketOrder(List<DVChainLevel> levels, MarketOrder marketOrder) {
    BigDecimal quantity = marketOrder.getOriginalAmount();
    for (DVChainLevel level : levels) {
      if (quantity.compareTo(level.getMaxQuantity()) <= 0) {
        return marketOrder.getType() == Order.OrderType.BID
            ? level.getBuyPrice()
            : level.getSellPrice();
      }
    }
    throw new FundsExceededException();
  }

  @Override
  public String placeMarketOrder(MarketOrder marketOrder) throws IOException {
    DVChainMarketResponse marketResponse = marketDataService.getMarketData();
    DVChainMarketData marketData =
        marketResponse.getMarketData().get(marketOrder.getCurrencyPair().base.getSymbol());
    List<DVChainLevel> levels = marketData.getLevels();
    String side = marketOrder.getType() == Order.OrderType.BID ? "Buy" : "Sell";
    DVChainNewMarketOrder dvChainNewMarketOrder =
        new DVChainNewMarketOrder(
            side,
            getPriceForMarketOrder(levels, marketOrder),
            marketOrder.getOriginalAmount(),
            marketOrder.getCurrencyPair().base.getSymbol());
    return newDVChainMarketOrder(dvChainNewMarketOrder).toString();
  }

  @Override
  public String placeLimitOrder(LimitOrder limitOrder) throws IOException {
    String side = limitOrder.getType() == Order.OrderType.BID ? "Buy" : "Sell";
    DVChainNewLimitOrder dvChainNewLimitOrder =
        new DVChainNewLimitOrder(
            side,
            limitOrder.getLimitPrice(),
            limitOrder.getOriginalAmount(),
            limitOrder.getCurrencyPair().base.getSymbol());
    return newDVChainLimitOrder(dvChainNewLimitOrder).toString();
  }

  @Override
  public OpenOrders getOpenOrders() throws IOException {
    return DVChainAdapters.adaptOpenOrders(getOrders());
  }

  @Override
  public OpenOrders getOpenOrders(OpenOrdersParams openOrders) throws NotYetImplementedForExchangeException {
    throw new NotYetImplementedForExchangeException();
  }

  @Override
  public UserTrades getTradeHistory(TradeHistoryParams params) throws IOException {
    return DVChainAdapters.adaptTradeHistory(getTrades());
  }

  @Override
  public TradeHistoryParams createTradeHistoryParams() throws NotYetImplementedForExchangeException {
    throw new NotYetImplementedForExchangeException();
  }

  @Override
  public OpenOrdersParams createOpenOrdersParams() throws NotYetImplementedForExchangeException {
    throw new NotYetImplementedForExchangeException();
  }

  @Override
  public Collection<Order> getOrder(String... orderIds) throws NotYetImplementedForExchangeException {
   throw new NotYetImplementedForExchangeException();
  }
}
