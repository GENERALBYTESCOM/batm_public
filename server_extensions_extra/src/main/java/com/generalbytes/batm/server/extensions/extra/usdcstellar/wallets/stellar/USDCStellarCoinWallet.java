package com.generalbytes.batm.server.extensions.extra.usdcstellar.wallets.stellar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto.Wallet;
import com.generalbytes.batm.server.extensions.util.CorporateWalletAPICalls;
import com.generalbytes.batm.server.extensions.util.StellarAPICalls;
import com.generalbytes.batm.server.extensions.util.StellarUtils;

public class USDCStellarCoinWallet implements IWallet {
	private static final Logger log = LoggerFactory.getLogger(USDCStellarCoinWallet.class);
	private static final String CRYPTO_CURRENCY = CryptoCurrency.USDCXLM.getCode();
	Wallet wallet;

	public USDCStellarCoinWallet(Wallet wallet) {
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
		// Replace this with the Stellar address you want to check

		try {
			return StellarAPICalls.getBalance(wallet.getPubkey(), "USDC",
					StellarUtils.getAssetIssuer(wallet.getIsTestnet(), cryptoCurrency), wallet.getIsTestnet());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {

		try {
			// Create the payment envelope
			String signedEnvelope = CorporateWalletAPICalls.getPaymentSignedEnvelop(wallet, destinationAddress,
					amount.setScale(7, RoundingMode.HALF_UP), "XLM", "");

			// Submit the signed envelope to the Stellar network
			String transactionHash = CorporateWalletAPICalls.submitTransaction(wallet, signedEnvelope);

			// Return the transaction hash as the result
			return transactionHash;
		} catch (Exception e) {
			log.error("Error sending coins: {}", e.getMessage());
			return null;
		}

	}

}
