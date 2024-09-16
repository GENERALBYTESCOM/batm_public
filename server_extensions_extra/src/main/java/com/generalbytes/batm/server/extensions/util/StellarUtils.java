package com.generalbytes.batm.server.extensions.util;

import java.util.HashMap;
import java.util.Map;


import com.generalbytes.batm.common.currencies.CryptoCurrency;

public class StellarUtils {
	private static final Map<String, String> assetIssuersTestnet = new HashMap<>();
	private static final Map<String, String> assetIssuersPubnet = new HashMap<>();

	static {
		assetIssuersTestnet.put("USDCXLM", "GBBD47IF6LWK7P7MDEVSCWR7DPUWV3NY3DTQEVFL4NAT4AQH3ZLLFLA5");
		assetIssuersPubnet.put("USDCXLM", "GA5ZSEJYB37JRC5AVCIA5MOP4RHTM335X2KGX3IHOJAPP5RE34K4KZVN");
		// Add more asset issuers for testnet if needed
	}

	// Private constructor to prevent instantiation
	private StellarUtils() {
	}

	public static String getCryptoBasedOnSelectedWalletType(String walletType) {

		if ("xlm-public".equalsIgnoreCase(walletType) || "xlm-testnet".equalsIgnoreCase(walletType)) {
			return CryptoCurrency.XLM.getCode();
		} else if ("usdcxlm-public".equalsIgnoreCase(walletType) || "usdcxlm-testnet".equalsIgnoreCase(walletType)) {
			return CryptoCurrency.USDCXLM.getCode();
		} else {
			return "";
		}
	}

	public static String getStellarSupportedAssetCode(String walletAssetCode) {

		if ("USDCXLM".equalsIgnoreCase(walletAssetCode)) {
			return "USDC";
		} else {
			return walletAssetCode;
		}
	}

	public static String getAssetIssuer(Boolean isTestnet, String assetCode) {
		Map<String, String> assetIssuers = isTestnet ? assetIssuersTestnet : assetIssuersPubnet;
		return assetIssuers.getOrDefault(assetCode, "");
	}
}