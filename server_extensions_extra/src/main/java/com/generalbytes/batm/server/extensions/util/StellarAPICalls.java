package com.generalbytes.batm.server.extensions.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StellarAPICalls {
	private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.util.StellarAPICalls");
	private static OkHttpClient client = new OkHttpClient(); // Made this static

	// Private constructor to prevent instantiation
	private StellarAPICalls() {
	}

	public static BigDecimal getBalance(String accountId, String assetCode, String assetIssuer, boolean isTestnet) {
		String url = isTestnet ? "https://horizon-testnet.stellar.org/accounts/"
				: "https://horizon.stellar.org/accounts/";
		url += accountId;

		Request request = new Request.Builder()
				.url(url)
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				log.error("Failed to fetch balance: " + response);
				return null;
			}

			JsonElement jsonResponse = JsonParser.parseString(response.body().string());
			JsonArray balances = jsonResponse.getAsJsonObject().getAsJsonArray("balances");

			for (JsonElement balanceElement : balances) {
				JsonObject balance = balanceElement.getAsJsonObject();

				if (assetCode.equals("XLM") && balance.get("asset_type").getAsString().equals("native")) {
					return new BigDecimal(balance.get("balance").getAsString()).setScale(7, BigDecimal.ROUND_DOWN);
				} else if (balance.get("asset_code").getAsString().equals(assetCode)
						&& balance.get("asset_issuer").getAsString().equals(assetIssuer)) {
					return new BigDecimal(balance.get("balance").getAsString()).setScale(7, BigDecimal.ROUND_DOWN);
				}
			}

			return new BigDecimal("0");
		} catch (Exception e) {
			log.error("Error fetching balance", e);
			return null;
		}
	}
}