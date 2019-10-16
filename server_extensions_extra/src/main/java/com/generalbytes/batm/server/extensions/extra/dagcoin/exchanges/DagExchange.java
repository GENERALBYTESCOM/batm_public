package com.generalbytes.batm.server.extensions.extra.dagcoin.exchanges;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.ITask;

public class DagExchange implements IRateSourceAdvanced, IExchangeAdvanced {
	
	private static final Logger log = LoggerFactory.getLogger(DagExchange.class);
	
	public DagExchange() {
		log.info("Created dummy exchange");
	}

	@Override
	public Set<String> getCryptoCurrencies() {
		log.info("Returning crypto currencies from DagExchange");
		
		Set<String> cryptoCurrencies = new HashSet<String>();
		cryptoCurrencies.add(CryptoCurrency.DAG.code);
		return cryptoCurrencies;
	}

	@Override
	public Set<String> getFiatCurrencies() {
		log.info("Returning fiat currencies from DagExchange");
		
		Set<String> fiatCurrencies = new HashSet<String>();
		fiatCurrencies.add(FiatCurrency.EUR.getCode());
		return fiatCurrencies;
	}

	@Override
	public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
		log.info("Returning exchange rate last (dummy value)");
		return new BigDecimal("1");
	}

	@Override
	public String getPreferredFiatCurrency() {
		log.info("Returning PreferredFiatCurrency (EUR)");
		return FiatCurrency.EUR.getCode();
	}

	@Override
	public BigDecimal getCryptoBalance(String cryptoCurrency) {
		log.info("Returning CryptoBalance (dummy value)");
		return new BigDecimal("1");
	}

	@Override
	public BigDecimal getFiatBalance(String fiatCurrency) {
		log.info("Returning FiatBalance (dummy value)");
		return new BigDecimal("1");
	}

	@Override
	public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse,
			String description) {
		log.info("purchaseCoins (dummy value)");
		return "SUCCESS";
	}

	@Override
	public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse,
			String description) {
		log.info("sellCoins (dummy value)");
		return "SUCCESS";
	}

	@Override
	public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
		log.info("sendCoins (dummy value)");
		return "SUCCESS";
	}

	@Override
	public String getDepositAddress(String cryptoCurrency) {
		log.info("DepositAddress (dummy value)");
		return "XXXXX";
	}

	@Override
	public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse,
			String description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse,
			String description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
		// TODO Auto-generated method stub
		return null;
	}

}
