package com.generalbytes.batm.server.extensions.extra.stellar;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.stellar.source.bpventure.BPVentureRateSource;
import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.StellarCoinWallet;
import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto.Wallet;
import com.generalbytes.batm.server.extensions.util.CorporateWalletAPICalls;
import com.generalbytes.batm.server.extensions.util.StellarUtils;

public class StellarCoinExtension extends AbstractExtension {
	private static final Logger log = LoggerFactory.getLogger(StellarCoinExtension.class);
	Set<String> supportedWalletTypes = new HashSet<>(
			Arrays.asList("xlm-public", "xlm-testnet", "usdcxlm-testnet", "usdcxlm-public"));
	String requestBody;

	@Override
	public String getName() {
		return "BATM Stellarcoin extension";
	}

	@Override
	public IWallet createWallet(String walletLogin, String tunnelPassword) {

		log.debug("Stellar:CreateWallet Started");
		if (walletLogin != null && !walletLogin.trim().isEmpty()) {
			try {
				log.debug("Stellar: Step 1");
				StringTokenizer st = new StringTokenizer(walletLogin, ":");
				String walletType = st.nextToken();

				if (supportedWalletTypes.contains(walletType.toLowerCase())) {
					log.debug("Stellar: Step 2");
					String walletId = st.nextToken();
					String apikey = st.nextToken();
					String hostname = st.nextToken();
					Boolean testnet = "xlm-testnet".equalsIgnoreCase(walletType)
							|| "usdcxlm-testnet".equalsIgnoreCase(walletType);
					Wallet wallet = new Wallet();
					wallet.setIsTestnet(testnet);
					wallet.setApiKey(apikey);
					wallet.setId(walletId);
					wallet.setHostname(hostname);
					String pubKey = CorporateWalletAPICalls.getWalletPublicKey(walletId, apikey);
					log.debug("wallet pubkey: " + pubKey);
					wallet.setPubkey(pubKey);
					wallet.setCrypto(StellarUtils.getCryptoBasedOnSelectedWalletType(walletType));
					return new StellarCoinWallet(wallet);

				}
			} catch (Exception e) {
				ExtensionsUtil.logExtensionParamsException("createWallet", getClass().getSimpleName(), walletLogin, e);
			}
		}
		return null;

	}

	@Override
	public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
		log.debug("Stellar: Step 6");
		if (CryptoCurrency.XLM.getCode().equalsIgnoreCase(cryptoCurrency)
				|| CryptoCurrency.USDCXLM.getCode().equalsIgnoreCase(cryptoCurrency)) {
			return new StellarcoinAddressValidator();
		}
		return null;
	}

	@Override
	public Set<String> getSupportedCryptoCurrencies() {
		log.debug("Stellar: Step 7");
		Set<String> result = new HashSet<String>();
		result.add(CryptoCurrency.XLM.getCode());
		return result;
	}

	@Override
	public IRateSource createRateSource(String sourceLogin) {
		if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
			try {
				StringTokenizer st = new StringTokenizer(sourceLogin, ":");
				String rsType = st.nextToken();

				if ("stellar".equalsIgnoreCase(rsType)) {
					BigDecimal rate = BigDecimal.ZERO;
					if (st.hasMoreTokens()) {
						try {
							rate = new BigDecimal(st.nextToken());
						} catch (Throwable e) {
						}
					}
					String preferedFiatCurrency = FiatCurrency.USD.getCode();
					if (st.hasMoreTokens()) {
						preferedFiatCurrency = st.nextToken().toUpperCase();
					}
					return new FixPriceRateSource(rate, preferedFiatCurrency);
				}
				if ("bpventure".equalsIgnoreCase(rsType)) {
					String preferredFiatCurrency = st.hasMoreTokens() ? st.nextToken().toUpperCase()
							: FiatCurrency.CAD.getCode();

					return new BPVentureRateSource(preferredFiatCurrency);
				}

			} catch (Exception e) {
				ExtensionsUtil.logExtensionParamsException("createRateSource", getClass().getSimpleName(), sourceLogin,
						e);
			}

		}
		return null;
	}
}
