package com.generalbytes.batm.server.extensions.util;

import java.io.IOException;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto.Wallet;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CorporateWalletAPICalls {
	private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.util.CorporateWalletAPICalls");
	private static OkHttpClient client = new OkHttpClient(); // Made this static

	// Private constructor to prevent instantiation
	private CorporateWalletAPICalls() {
	}

	public static String getWalletPublicKey(String walletId, String apiKey) {
		OkHttpClient client = new OkHttpClient();
		String url = "https://cw-uat.bpventures.us/wallet/" + walletId + "/";

		Request request = new Request.Builder()
				.url(url)
				.get()
				.addHeader("accept", "application/json")
				.addHeader("Authorization", apiKey)
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Unexpected code " + response);
			}
			String responseBody = response.body().string();
			JsonElement jsonResponse = JsonParser.parseString(responseBody);
			String publicKey = jsonResponse.getAsJsonObject().get("pubkey").getAsString();

			return publicKey;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getPaymentSignedEnvelop(Wallet wallet, String destination, BigDecimal amount, String assetCode,
			String assetIssuer) {
		log.debug(
				"Stellar: getPaymentSignedEnvelop Called with params: destination: {}, amount: {}, assetCode: {}, assetIssuer: {}",
				destination, amount, assetCode, assetIssuer);
		String url = "https://" + wallet.getHostname() + "/wallet/" + wallet.getId() + "/sign-envelope/payment/";

		MediaType JSON = MediaType.get("application/json; charset=utf-8");
		String json = "{\n" +
				"  \"asset_code\": \"" + assetCode + "\",\n" +
				"  \"asset_issuer\": \"" + assetIssuer + "\",\n" +
				"  \"destination\": \"" + destination + "\",\n" +
				"  \"amount\": \"" + amount + "\",\n" +
				"  \"create_account\": true,\n" +
				"  \"check_balance\": true,\n" +
				"  \"base_fee\": 10000\n" +
				"}";

		RequestBody body = RequestBody.create(json, JSON);
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.addHeader("accept", "application/json")
				.addHeader("Authorization", wallet.getApiKey())
				.addHeader("Content-Type", "application/json")
				.build();

		log.debug("Stellar getpaymentSignedEnvelop request: " + request.toString());
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Unexpected code " + response);
			}
			// Assuming 'response' is the Response object
			String responseBody = response.body().string();
			log.debug("response body: " + responseBody);

			// Parse the response body as JSON
			JsonElement responseFromAPI = JsonParser.parseString(responseBody);
			log.debug("response body: " + responseFromAPI);

			// Extract the 'signed_envelope' field
			String signedEnvelope = responseFromAPI.getAsJsonObject().get("signed_envelope").getAsString();

			return signedEnvelope;
		} catch (IOException e) {
			log.error("stellar Error getting signed envelope", e);
			e.printStackTrace();
			return null;
		}
	}

	public static String submitTransaction(Wallet wallet, String envelope) {
		OkHttpClient client = new OkHttpClient();

		String url = "https://" + wallet.getHostname() + "/wallet/" + wallet.getId() + "/submit-transaction-onchain/";

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create("{ \"envelope\":\"" + envelope + "\" }", mediaType);

		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.addHeader("accept", "application/json")
				.addHeader("Authorization", wallet.getApiKey())
				.addHeader("Content-Type", "application/json")
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				log.error("Failed to submit transaction: " + response);
				return null;
			}

			String responseBody = response.body().string();
			log.debug("Stellar: submitTransaction response: " + responseBody);

			JsonElement jsonResponse = JsonParser.parseString(responseBody);
			String transactionHref = jsonResponse.getAsJsonObject()
					.getAsJsonObject("horizon_response")
					.getAsJsonObject("_links")
					.getAsJsonObject("transaction")
					.get("href").getAsString();

			return transactionHref;
		} catch (Exception e) {
			log.error("Error submitting transaction", e);
			return null;
		}
	}
}