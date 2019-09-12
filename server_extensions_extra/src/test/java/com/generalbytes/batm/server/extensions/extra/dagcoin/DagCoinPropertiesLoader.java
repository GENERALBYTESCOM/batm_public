package com.generalbytes.batm.server.extensions.extra.dagcoin;

import java.io.IOException;
import java.util.Properties;

public class DagCoinPropertiesLoader {
	
	public static String getTestWalletAddress() {
		Properties prop = new Properties();
		try {
			prop.load(DagCoinPropertiesLoader.class.getResourceAsStream("/dagCoinConfig.properties"));
			return prop.getProperty("WALLET_ID");
		} catch (IOException ex) {
			return "OO275RAQFDMM7S7WSWOZQ3LBYIQK4I5J";
		}
	}

}
