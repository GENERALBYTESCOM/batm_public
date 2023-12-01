package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto.Transaction;
import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto.Wallet;

import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StellarCoinWallet implements IWallet {
	private static final Logger log = LoggerFactory.getLogger(StellarCoinWallet.class);
	private static final String CRYPTO_CURRENCY = CryptoCurrency.XLM.getCode();
	Wallet wallet;

	public StellarCoinWallet(Wallet wallet) {
		this.wallet = wallet;

	}

	@Override
	public String getCryptoAddress(String cryptoCurrency) {

		return wallet.getPubkey();
	}

	@Override
	public Set<String> getCryptoCurrencies() {
		Set<String> result = new HashSet<>();
		result.add(CRYPTO_CURRENCY);
		return result;
	}

	@Override
	public String getPreferredCryptoCurrency() {
		return CRYPTO_CURRENCY;
	}

	@Override
	public BigDecimal getCryptoBalance(String cryptoCurrency) {

		return null;
	}

	@Override
	public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
		return description;

		
	}

	

}
