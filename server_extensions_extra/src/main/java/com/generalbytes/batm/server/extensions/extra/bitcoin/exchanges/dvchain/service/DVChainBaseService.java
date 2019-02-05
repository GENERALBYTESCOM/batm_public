package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.service;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.DVChain;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.DVChainException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.service.BaseExchangeService;
import org.knowm.xchange.service.BaseService;
import si.mazi.rescu.RestProxyFactory;

public class DVChainBaseService extends BaseExchangeService implements BaseService {
  protected final DVChain dvChain;
  protected final String authToken;

  protected DVChainBaseService(Exchange exchange) {

    super(exchange);
    dvChain =
        RestProxyFactory.createProxy(
            DVChain.class, exchange.getExchangeSpecification().getSslUri());
    authToken = exchange.getExchangeSpecification().getSecretKey();
  }

  protected ExchangeException handleException(DVChainException exception) {
      return new ExchangeException(exception);
  }
}
