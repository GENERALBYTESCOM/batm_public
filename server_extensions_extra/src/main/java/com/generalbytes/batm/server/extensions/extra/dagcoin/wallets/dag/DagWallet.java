package com.generalbytes.batm.server.extensions.extra.dagcoin.wallets.dag;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.DagCoinParameters;
import com.generalbytes.batm.server.extensions.extra.dagcoin.exception.DagCoinRestClientException;
import com.generalbytes.batm.server.extensions.extra.dagcoin.service.DagCoinApiClientService;

public class DagWallet implements IWallet {

	private static final Logger log = LoggerFactory.getLogger(DagWallet.class);
	private static final String CRYPTO_CURRENCY = CryptoCurrency.DAG.getCode();

	private String walletId;
	private DagCoinApiClientService service;

	public DagWallet(String walletId, DagCoinParameters params) {
		this.walletId = walletId;
		try {
			this.service = new DagCoinApiClientService(params);
		} catch (DagCoinRestClientException e) {
			log.error("Error in instantiating DagCoinApiService - " + e.getErrorCode() + " :: " + e.getMessage());
			return;
		}
	}

	@Override
	public String getCryptoAddress(String cryptoCurrency) {
		return this.walletId;
	}

	@Override
	public Set<String> getCryptoCurrencies() {
		Set<String> result = new HashSet<String>();
		result.add(CRYPTO_CURRENCY);
		return result;
	}

	@Override
	public String getPreferredCryptoCurrency() {
		return CRYPTO_CURRENCY;
	}

	@Override
	public BigDecimal getCryptoBalance(String cryptoCurrency) {
		log.info("Getting wallet balance for - " + this.walletId);
		try {
			return this.service.getCustomerBalance(this.walletId).getBalance();
		} catch (DagCoinRestClientException e) {
			log.error("Error in getting wallet balance - " + cryptoCurrency + " ERROR - " + e.getMessage() + " :: "
					+ e.getErrorCode());
			return null;
		}
	}

	@Override
	public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
		log.info("Adding money to wallet - destinationAddress = " + destinationAddress + " amount = " + amount.toString()
				+ " crypto = " + cryptoCurrency + " description = " + description);
		try {
			return this.service.makeTransaction(destinationAddress, amount.toString(), cryptoCurrency).getTransactionId();
		} catch (DagCoinRestClientException e) {
			log.error("Error in sending coins in wallet - " + destinationAddress + " ERROR - " + e.getMessage() + " :: "
					+ e.getErrorCode());
			return null;
		}
	}

}
