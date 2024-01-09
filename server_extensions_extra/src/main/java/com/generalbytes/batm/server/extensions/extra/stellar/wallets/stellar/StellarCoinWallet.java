package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.consts.Const;
import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto.Wallet;
import com.google.logging.type.HttpRequest;

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
		// Replace this with the Stellar address you want to check
        String stellarAddress = wallet.getPubkey();

        try {
            // Set up the Horizon testnet URL
            String horizonURL = "https://horizon-mainnet.stellar.org/accounts/" + stellarAddress;

            // Create an HTTP connection
            URL url = new URL(horizonURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse and print the balance information
            return new BigDecimal(response.toString());
            

        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}
	

	@Override
	public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {

		String paymentUrl = String.format(Const.TRANSACTION, wallet.getId());
		String requestBody = "{\"asset_code\":\"XLM\",\"asset_issuer\":\"\",\"destination\":\"" + destinationAddress
				+ "\",\"amount\":\"" + amount
				+ "\",\"memo_type\":\"text\",\"memo\":\"xyz\",\"create_account\":true,\"check_balance\":true,\"base_fee\":10000}";
		String apitoken = Const.KEY;
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(paymentUrl))
				.header("Content-Type", "application/json").header("Authorization", apitoken)
				.POST(ofString(requestBody)).build();
		HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());

		if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
			String broadUrl = String.format(Const.BRODCAST, wallet.getId());
			String requestbodyenvelope = "{\"envelope\":\"" + envelope + "\"}";
			String apitoken = Const.KEY;
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(broadUrl))
					.header("Content-Type", "application/json").header("Authorization", apitoken)
					.POST(ofString(requestBody)).build();
			HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());
			System.out.println("Response is " + httpResponse);
			if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
				System.out.println("API Response:\n" + httpResponse.body());
				return httpResponse.body();
			} else {
				System.out.println("Error Response Code: " + httpResponse.statusCode());
				System.out.println("Error Response Body: " + httpResponse.body());
				return null;
			}
			return httpResponse.body();
		} else {

			return null;
		}

	}

}
