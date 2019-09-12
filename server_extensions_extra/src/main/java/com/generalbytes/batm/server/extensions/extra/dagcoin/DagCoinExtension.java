package com.generalbytes.batm.server.extensions.extra.dagcoin;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.dagcoin.dagpaper.DagPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.extra.dagcoin.sources.dag.DagCoinRateSource;
import com.generalbytes.batm.server.extensions.extra.dagcoin.wallets.dag.DagWallet;

public class DagCoinExtension extends AbstractExtension {

	private static final Logger log = LoggerFactory.getLogger(DagCoinExtension.class);

	@Override
	public String getName() {
		return "BATM DagCoin extension";
	}

	@Override
	public IWallet createWallet(String walletLogin) {
		log.info("Creating wallet - Wallet login - " + walletLogin);

		if (walletLogin != null && !walletLogin.trim().isEmpty()) {
			// walletname:walletId
			StringTokenizer st = new StringTokenizer(walletLogin, ":");
			String walletName = st.nextToken();
			if ("dagd".equals(walletName)) {
				String walletId = st.nextToken();
				if (walletId != null) {
					return new DagWallet(walletId);
				}
			}

			if ("dagdemo".equalsIgnoreCase(walletName)) {
				// fiatCurrency:walletId
				String fiatCurrency = st.nextToken();
				String walletAddress = "";
				if (st.hasMoreTokens()) {
					walletAddress = st.nextToken();
				}

				if (fiatCurrency != null && walletAddress != null) {
					return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.DAG.getCode(),
							walletAddress);
				}
			}
		}

		return null;
	}

	@Override
	public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
		log.info("Create address validator - " + cryptoCurrency);
		if (CryptoCurrency.DAG.getCode().equalsIgnoreCase(cryptoCurrency)) {
			return new DagCoinAddressValidator();
		}
		return null;
	}

	public IRateSource createRateSource(String sourceLogin) {
		log.info("Create rate source - " + sourceLogin);

		if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
			StringTokenizer st = new StringTokenizer(sourceLogin, ":");
			String exchangeType = st.nextToken();
			if ("dagrate".equalsIgnoreCase(exchangeType)) {
				String preferredFiatCurrency = FiatCurrency.EUR.getCode();
				if (st.hasMoreTokens()) {
					preferredFiatCurrency = st.nextToken().toUpperCase();
				}
				return new DagCoinRateSource(preferredFiatCurrency);
			}
		}
		return null;
	}

	@Override
	public Set<String> getSupportedCryptoCurrencies() {
		Set<String> result = new HashSet<>();
		result.add(CryptoCurrency.DAG.getCode());
		return result;
	}

	@Override
	public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
		if (CryptoCurrency.DAG.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new DagPaperWalletGenerator("dag", ctx);
        }
        return null;
	}

}