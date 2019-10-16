package com.generalbytes.batm.server.extensions.extra.dagcoin.util;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.DagCoinParameters;
import com.generalbytes.batm.server.extensions.extra.dagcoin.exception.DagCoinRestClientException;

public class CryptoEngine {

	private static final Logger log = LoggerFactory.getLogger(CryptoEngine.class.getName());
	private byte[] iv;
	private String key;
	private String publicKey;
	private String secretKey;
	
	public CryptoEngine(DagCoinParameters params) {
		this.iv = "0000000000000000".getBytes();
		this.key = params.getEncryptionKey();
		this.publicKey = params.getPublicKey();
		this.secretKey = params.getPrivateKey();
	}

	public String encrypt(String content) throws DagCoinRestClientException {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec secKeySpec = new SecretKeySpec(DatatypeConverter.parseHexBinary(this.key), "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(this.iv);
			cipher.init(Cipher.ENCRYPT_MODE, secKeySpec, ivSpec);
			byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));
			return DatatypeConverter.printHexBinary(encrypted);
		} catch (Exception e) {
			log.error("Error in encryption - " + e.getMessage());
			throw new DagCoinRestClientException("Error in encryption", 422);
		}
	}

	public String decrypt(String encrypted) throws DagCoinRestClientException {
		try {
			Cipher decipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec secKeySpec = new SecretKeySpec(DatatypeConverter.parseHexBinary(this.key), "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(this.iv);
			decipher.init(Cipher.DECRYPT_MODE, secKeySpec, ivSpec);
			byte[] decrypted = decipher.doFinal(DatatypeConverter.parseHexBinary(encrypted));
			return new String(decrypted);
		} catch (Exception e) {
			log.error("Error in decryption - " + e.getMessage());
			throw new DagCoinRestClientException("Error in decryption", 422);
		}
	}
	
	public MultivaluedMap<String, Object> getHeaders(String body) throws DagCoinRestClientException {
		MultivaluedMap<String, Object> map = new MultivaluedHashMap<String, Object>();
		map.add("publickey", this.publicKey);
		map.add("signature", computeHash(body));
		return map;
	}
	
	public boolean validateHeaders(MultivaluedMap<String, Object> headers, String responseBody) throws DagCoinRestClientException {
		String public_key = (String)headers.get("publickey").get(0);
		String signature = (String)headers.get("signature").get(0);
		
		if (!public_key.equalsIgnoreCase(this.publicKey)) return false;
		if (!computeHash(responseBody).equalsIgnoreCase(signature)) return false;

		return true;
	}
	
	private String computeHash(String body) throws DagCoinRestClientException {
		try {
			Mac hasher = Mac.getInstance("HmacSHA256");
			hasher.init(new SecretKeySpec(this.secretKey.getBytes(), "HmacSHA256"));
			byte[] hash = hasher.doFinal(body.getBytes());
			return DatatypeConverter.printHexBinary(hash);
		} catch (Exception e) {
			log.error("Error in computing hash - " + e.getMessage());
			throw new DagCoinRestClientException("Error in creating hash - " + e.getMessage(), 422);
		}
	}

}
