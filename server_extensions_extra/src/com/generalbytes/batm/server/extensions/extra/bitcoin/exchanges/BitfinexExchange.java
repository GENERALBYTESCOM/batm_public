package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IExchange;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.NotAvailableFromExchangeException;
import com.xeiam.xchange.NotYetImplementedForExchangeException;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.trade.MarketOrder;
import com.xeiam.xchange.service.polling.PollingAccountService;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import com.xeiam.xchange.service.polling.PollingTradeService;

public class BitfinexExchange implements IExchange {

	private static final Logger log = LoggerFactory.getLogger("com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.BitfinexEchange");
	private Exchange exchange = null;
	private String keyID;
	private String keySecret;

	public BitfinexExchange(String keyID, String keySecret) {
		this.keyID = keyID;
		this.keySecret = keySecret;
	}
	
	private synchronized Exchange getExchange() {
		if (this.exchange == null) {
			ExchangeSpecification bfxSpec = new com.xeiam.xchange.bitfinex.v1.BitfinexExchange().getDefaultExchangeSpecification();
			bfxSpec.setApiKey(this.keyID);
			bfxSpec.setSecretKey(this.keySecret);
			this.exchange = ExchangeFactory.INSTANCE.createExchange(bfxSpec);
		}
		return this.exchange;
	}

	public Set<String> getCryptoCurrencies() {
		Set<String> cryptoCurrencies = new HashSet<String>();
		cryptoCurrencies.add(ICurrencies.BTC);
		return cryptoCurrencies;
	}

	public Set<String> getFiatCurrencies() {
		Set<String> fiatCurrencies = new HashSet<String>();
		fiatCurrencies.add(ICurrencies.USD);
		return fiatCurrencies;
	}

	public String getPreferredFiatCurrency() {
		return ICurrencies.USD;
	}

	public BigDecimal getCryptoBalance(String cryptoCurrency) {
		// [TODO] Can be extended to support LTC and DRK (and other currencies supported by BFX)
		if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
			return BigDecimal.ZERO;
		}
		log.debug("Calling Bitfinex exchange (getBalance)");

		try {
			return getExchange().getPollingAccountService().getAccountInfo().getBalance(cryptoCurrency);
		} catch (ExchangeException | NotAvailableFromExchangeException | NotYetImplementedForExchangeException | IOException e) {
			e.printStackTrace();
			log.error("Bitfinex exchange (getBalance) failed with message: " + e.getMessage());
		}
		return null;
	}

	public BigDecimal getFiatBalance(String fiatCurrency) {
		if (!ICurrencies.USD.equalsIgnoreCase(fiatCurrency)) {
			return BigDecimal.ZERO;
		}
		log.debug("Calling Bitfinex exchange (getBalance)");

		try {
			return getExchange().getPollingAccountService().getAccountInfo().getBalance(fiatCurrency);
		} catch (ExchangeException | NotAvailableFromExchangeException | NotYetImplementedForExchangeException | IOException e) {
			e.printStackTrace();
			log.error("Bitfinex exchange (getBalance) failed with message: " + e.getMessage());
		}
		return null;
	}

	public final String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
		if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
			log.error("Bitfinex supports only BTC");
			return null;
		}

		log.info("Calling bitfinex exchange (withdrawal destination: " + destinationAddress + " amount: " + amount + " " + cryptoCurrency + ")");
		
		PollingAccountService accountService = getExchange().getPollingAccountService();
		try {
			accountService.withdrawFunds(destinationAddress, amount, destinationAddress);
		} catch (ExchangeException | NotAvailableFromExchangeException | NotYetImplementedForExchangeException | IOException e) {
			e.printStackTrace();
			log.error("Bitfinex exchange (withdrawal) failed with message: " + e.getMessage());
		}
		return null;
	}

	public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
		if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
			log.error("Bitfinex implementation supports only BTC");
			return null;
		}
		if (!ICurrencies.USD.equalsIgnoreCase(fiatCurrencyToUse)) {
			log.error("Bitfinex supports only USD");
			return null;
		}

		log.info("Calling Bitfinex exchange (purchase " + amount + " " + cryptoCurrency + ")");
		PollingAccountService accountService = getExchange().getPollingAccountService();
		PollingTradeService tradeService = getExchange().getPollingTradeService();
		PollingMarketDataService marketDataService = getExchange().getPollingMarketDataService();
		
		try {
			log.debug("AccountInfo as String: " + accountService.getAccountInfo().toString());

			CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrencyToUse);
			Ticker ticker = marketDataService.getTicker(currencyPair, new Object[0]);
			log.debug("ticker = " + ticker);
			log.debug(ticker.getLast().toString());
			
			MarketOrder order = new MarketOrder(OrderType.BID, amount, currencyPair);
			log.debug("marketOrder = " + order);

			String orderId = tradeService.placeMarketOrder(order);
			log.debug("orderId = " + (String) orderId + " " + order);
			
		} catch (ExchangeException | NotAvailableFromExchangeException | NotYetImplementedForExchangeException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
