package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.knowm.xchange.Exchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.DVChainException;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.trade.DVChainNewLimitOrder;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.trade.DVChainNewMarketOrder;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.trade.DVChainTrade;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.trade.DVChainTradesResponse;

public class DVChainTradeServiceRaw extends DVChainBaseService {
  public DVChainTradeServiceRaw(Exchange exchange) {
    super(exchange);
  }

  public DVChainTrade newDVChainMarketOrder(DVChainNewMarketOrder order) throws IOException {
    try {
      return dvChain.placeMarketOrder(order, authToken);
    } catch (DVChainException e) {
      throw handleException(e);
    }
  }

  public DVChainTrade newDVChainLimitOrder(DVChainNewLimitOrder order) throws IOException {
    try {
      return dvChain.placeLimitOrder(order, authToken);
    } catch (DVChainException e) {
      throw handleException(e);
    }
  }

  public List<DVChainTrade> getTrades() throws IOException {
    try {
      DVChainTradesResponse tradesResponse = dvChain.getTrades(authToken, "no-cache", "no-cache");
      return tradesResponse
          .getData()
          .stream()
          .filter(t -> t.getStatus().equals("Complete"))
          .collect(Collectors.toList());
    } catch (DVChainException e) {
      throw handleException(e);
    }
  }

  public List<DVChainTrade> getOrders() throws IOException {
    try {
      DVChainTradesResponse tradesResponse = dvChain.getTrades(authToken,"no-cache", "no-cache");
      return tradesResponse
          .getData()
          .stream()
          .filter(t -> t.getStatus().equals("Open"))
          .collect(Collectors.toList());
    } catch (DVChainException e) {
      throw handleException(e);
    }
  }

  public String cancelDVChainOrder(String tradeId) throws IOException {
    try {
      return dvChain.cancelOrder(tradeId, authToken);
    } catch (DVChainException e) {
      throw handleException(e);
    }
  }
}
