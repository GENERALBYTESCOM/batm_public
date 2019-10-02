package com.generalbytes.batm.server.extensions.extra.dagcoin.sources.dag;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dagcoin.domain.DagCoinParameters;
import com.dagcoin.exception.DagCoinRestClientException;
import com.dagcoin.service.DagCoinApiClientService;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;

public class DagCoinRateSource implements IRateSource {

	private static final Logger log = LoggerFactory.getLogger(DagCoinRateSource.class);

	private BigDecimal rate;
	private String preferredFiatCurrency = FiatCurrency.EUR.getCode();
	private DagCoinParameters params;

	public DagCoinRateSource(String preferedFiatCurrency, DagCoinParameters params) {
		this.params = params;
		if (FiatCurrency.EUR.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
			this.preferredFiatCurrency = FiatCurrency.EUR.getCode();
		}
	}

	@Override
	public Set<String> getCryptoCurrencies() {
		Set<String> result = new HashSet<String>();
		result.add(CryptoCurrency.DAG.getCode());
		return result;
	}

	@Override
	public Set<String> getFiatCurrencies() {
		Set<String> result = new HashSet<String>();
		result.add(FiatCurrency.EUR.getCode());
		return result;
	}

	@Override
	public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
		log.info("GetExchangeRate - cryptoCurrency : " + cryptoCurrency + " :: fiatCurrency : " + fiatCurrency);
		try {
			DagCoinApiClientService service = new DagCoinApiClientService(this.params);
			this.rate = service.getExchangeRate().getRate();
		} catch (DagCoinRestClientException e) {
			log.error("Error occured in getting exchange rate - " + e.getMessage() + " :: " + e.getErrorCode());
		}
		return this.rate;
	}

	@Override
	public String getPreferredFiatCurrency() {
		return this.preferredFiatCurrency;
	}

}
