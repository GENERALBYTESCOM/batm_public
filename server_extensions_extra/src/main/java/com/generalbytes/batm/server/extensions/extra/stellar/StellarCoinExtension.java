package com.generalbytes.batm.server.extensions.extra.stellar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IWallet;

public class StellarCoinExtension extends AbstractExtension {
	private static final Logger log = LoggerFactory.getLogger(StellarCoinExtension.class);

	@Override
	public String getName() {
		return "BATM Stellarcoin extension";
	}

	@Override
	public IWallet createWallet(String walletLogin, String tunnelPassword) {
		return null;

	}

}
