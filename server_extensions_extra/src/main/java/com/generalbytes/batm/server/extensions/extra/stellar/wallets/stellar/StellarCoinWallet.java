package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto.Wallet;
import com.generalbytes.batm.server.extensions.util.CorporateWalletAPICalls;
import com.generalbytes.batm.server.extensions.util.StellarAPICalls;
import com.generalbytes.batm.server.extensions.util.StellarUtils;

public class StellarCoinWallet implements IWallet {
	private static final Logger log = LoggerFactory.getLogger(StellarCoinWallet.class);
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
		result.add(wallet.getCrypto());
		return result;
	}

	@Override
	public String getPreferredCryptoCurrency() {
		return wallet.getCrypto();
	}

	@Override
	public BigDecimal getCryptoBalance(String cryptoCurrency) {
		// Replace this with the Stellar address you want to check

		try {

			return StellarAPICalls.getBalance(wallet.getPubkey(),
					StellarUtils.getStellarSupportedAssetCode(wallet.getCrypto()),
					StellarUtils.getAssetIssuer(wallet.getIsTestnet(), wallet.getCrypto()), wallet.getIsTestnet());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {

		try {
			log.debug("Stellar: SendCoinsCalled with params: destinationAddress: {}, amount: {}, cryptoCurrency: {}",
					destinationAddress, amount, cryptoCurrency);
			// Create the payment envelope
			String signedEnvelope = CorporateWalletAPICalls.getPaymentSignedEnvelop(wallet, destinationAddress,
					amount.setScale(7, RoundingMode.HALF_UP),
					StellarUtils.getStellarSupportedAssetCode(wallet.getCrypto()),
					StellarUtils.getAssetIssuer(wallet.getIsTestnet(), wallet.getCrypto()));

			log.debug("Stellar: SignedEnvelop: {} ", signedEnvelope);
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
