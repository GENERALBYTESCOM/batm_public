package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.consts.Const;
import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto.SiginEnvelopeResponse;
import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto.TransactionResponse;
import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto.Wallet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
			String horizonURL = Const.BALANCEAPI + stellarAddress;

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

		URL url = null;
		try {
			url = new URL(paymentUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Set the request method to POST
		try {
			connection.setRequestMethod("POST");
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Authorization", wallet.getApiKey());
		connection.setDoOutput(true);

		// Write the request body
		try (OutputStream os = connection.getOutputStream()) {
			byte[] input = requestBody.getBytes("utf-8");
			os.write(input, 0, input.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Get the response
		try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
			StringBuilder response = new StringBuilder();
			String responseLine;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}

			if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ").create();
				SiginEnvelopeResponse apiResponse = gson.fromJson(response.toString(), SiginEnvelopeResponse.class);

				String broadUrl = String.format(Const.BRODCAST, wallet.getId());

				String envelopeBody = "{\"envelope\":\"" + apiResponse.getSignedEnvelope() + "\"}";

				URL braodCasturl = new URL(broadUrl);
				HttpURLConnection broadcastConnection = (HttpURLConnection) url.openConnection();

				// Set the request method to POST
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Authorization", wallet.getApiKey());
				connection.setDoOutput(true);

				// Write the request body
				try (OutputStream os = connection.getOutputStream()) {
					byte[] input = requestBody.getBytes("utf-8");
					os.write(input, 0, input.length);
				}

				// Get the response
				try (BufferedReader brreader = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"))) {
					StringBuilder broadCastResponse = new StringBuilder();
					String broadCastresponseLine;
					while ((broadCastresponseLine = brreader.readLine()) != null) {
						broadCastResponse.append(broadCastresponseLine.trim());
					}

					if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
						// Assuming you have the JSON response in a String variable called jsonResponse
						gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create();
						TransactionResponse transactionResponse = gson.fromJson(broadCastResponse.toString(),
								TransactionResponse.class);
						return transactionResponse.toString();
					}
				} finally {
					connection.disconnect();
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			connection.disconnect();
		}
		return null;

	}

}
