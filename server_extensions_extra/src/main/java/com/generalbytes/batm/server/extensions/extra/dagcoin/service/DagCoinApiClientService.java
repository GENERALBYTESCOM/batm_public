package com.generalbytes.batm.server.extensions.extra.dagcoin.service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.CustomerWalletBalanceResponse;
import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.DagCoinParameters;
import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.ExchangeRateResponse;
import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.PaperWalletResponse;
import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.TransactionRequest;
import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.TransactionResponse;
import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.ValidateWalletResponse;
import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.WalletBalanceResponse;
import com.generalbytes.batm.server.extensions.extra.dagcoin.exception.DagCoinRestClientException;
import com.generalbytes.batm.server.extensions.extra.dagcoin.util.CryptoEngine;

/**
 * 
 * Client service class for querying DagCoin APIs
 * 
 * @author shubhrapahwa
 *
 */
public class DagCoinApiClientService {

	private static final Logger log = LoggerFactory.getLogger(DagCoinApiClientService.class.getName());
	private DagCoinParameters params;
	private CryptoEngine cryptoEngine;
	
	public DagCoinApiClientService(DagCoinParameters params) throws DagCoinRestClientException {
		log.info("DagCoinApiClientService - Params - " + 
			"URL - " + params.getApiUrl() + " :: " + 
			"Encryption Key - " + params.getEncryptionKey() + " :: " + 
			"Private Key - " + params.getPrivateKey() + " :: " + 
			"Public Key - " + params.getPublicKey() + " :: " + 
			"Encryption Key - " + params.getEncryptionKey());
		this.params = params;
		this.cryptoEngine = new CryptoEngine(params);
	}

	/**
	 * Returns the exchange rate DAG/EUR
	 * 
	 * @return {@link ExchangeRateResponse}
	 * @throws DagCoinRestClientException - in case of an error
	 */
	public ExchangeRateResponse getExchangeRate() throws DagCoinRestClientException {
		log.info("Getting exchange rate for DAG/EUR");
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(this.params.getApiUrl()).path("/dagcoin/exchangeRate");
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).headers(this.cryptoEngine.getHeaders("{}"));

		Response response = invocationBuilder.get(Response.class);
		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			log.error("Error in getting exchange rate - " + response.getStatusInfo().toString() + " :: "
					+ response.getStatus());
			throw new DagCoinRestClientException(response.getStatusInfo().toString(), response.getStatus());
		}
		
		String responseBody = response.readEntity(String.class);
		if (!this.cryptoEngine.validateHeaders(response.getHeaders(), responseBody)) {
			log.error("Error in validaing headers");
			throw new DagCoinRestClientException("HMAC Authentication failed", 401);
		}

		ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse();
		try {
			exchangeRateResponse = new ObjectMapper().readValue(responseBody, ExchangeRateResponse.class);
		} catch (Exception e) {
			log.error("Error in mapping response for getExchangeRate - " + response.getStatusInfo().toString() + " :: "
					+ response.getStatus());
			throw new DagCoinRestClientException("Error in mapping response", 422);
		}
		return exchangeRateResponse;
	}

	/**
	 * Validates given walletID, returns true if walletId is valid, false otherwise
	 * 
	 * @param walletId - wallet address to be validated
	 * @return {@link ValidateWalletResponse}
	 * @throws DagCoinRestClientException - if there is an error
	 */
	public ValidateWalletResponse validateWalletAddress(String walletId) throws DagCoinRestClientException {
		log.info("Validating wallet with ID - " + walletId);
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(this.params.getApiUrl())
				.path("/wallet/" + this.cryptoEngine.encrypt(walletId) + "/validation/check");
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).headers(this.cryptoEngine.getHeaders("{}"));

		Response response = invocationBuilder.get(Response.class);
		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			log.error("Error in validating wallet address: " + walletId + " - " + response.getStatusInfo().toString()
					+ " :: " + response.getStatus());
			throw new DagCoinRestClientException(response.getStatusInfo().toString(), response.getStatus());
		}
		
		String responseBody = response.readEntity(String.class);
		if (!this.cryptoEngine.validateHeaders(response.getHeaders(), responseBody)) {
			log.error("Error in validaing headers");
			throw new DagCoinRestClientException("HMAC Authentication failed", 401);
		}

		ValidateWalletResponse validateWalletResponse = new ValidateWalletResponse();
		try {
			validateWalletResponse = new ObjectMapper().readValue(responseBody, ValidateWalletResponse.class);
		} catch (Exception e) {
			log.error("Error in mapping response for validateWalletAddress - " + response.getStatusInfo().toString() + " :: "
					+ response.getStatus());
			throw new DagCoinRestClientException("Error in mapping response", 422);
		}
		return validateWalletResponse;
	}

	/**
	 * Returns the wallet balance
	 * 
	 * @param walletId - wallet address whose balance needs to be retrieved
	 * @return {@link WalletBalanceResponse}
	 * @throws DagCoinRestClientException - if there is an error
	 */
	public WalletBalanceResponse getBalance(String walletId) throws DagCoinRestClientException {
		log.info("Getting wallet balance for - " + walletId);
		
		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(this.params.getApiUrl()).path("/wallet/" + this.cryptoEngine.encrypt(walletId) + "/balance/get");
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).headers(this.cryptoEngine.getHeaders("{}"));

		Response response = invocationBuilder.post(Entity.json("{}"), Response.class);
		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			log.error("Error in getting wallet balance: " + walletId + " - " + response.getStatusInfo().toString()
					+ " :: " + response.getStatus());
			throw new DagCoinRestClientException(response.getStatusInfo().toString(), response.getStatus());
		}
		
		String responseBody = response.readEntity(String.class);
		if (!this.cryptoEngine.validateHeaders(response.getHeaders(), responseBody)) {
			log.error("Error in validaing headers");
			throw new DagCoinRestClientException("HMAC Authentication failed", 401);
		}

		String decryptedResponse = this.cryptoEngine.decrypt(responseBody);
		WalletBalanceResponse walletBalanceResponse = new WalletBalanceResponse();
		try {
			walletBalanceResponse = new ObjectMapper().readValue(decryptedResponse, WalletBalanceResponse.class);
		} catch (Exception e) {
			log.error("Error in mapping response for getBalance - " + response.getStatusInfo().toString() + " :: "
					+ response.getStatus());
			throw new DagCoinRestClientException("Error in mapping response", 422);
		}
		return walletBalanceResponse;
	}
	
	/**
	 * Returns the balance of customer balance
	 * 
	 * @param walletId - wallet address for retrieving customer balance
	 * @return {@link CustomerWalletBalanceResponse}
	 * @throws DagCoinRestClientException - if there is an error
	 */
	public CustomerWalletBalanceResponse getCustomerBalance(String walletId) throws DagCoinRestClientException {
		log.info("Getting customer wallet balance for - " + walletId);
		
		Client client = ClientBuilder.newClient();

		WebTarget webTarget = client.target(this.params.getApiUrl()).path("/customer/wallet/" + this.cryptoEngine.encrypt(walletId) + "/balance/get");
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).headers(this.cryptoEngine.getHeaders("{}"));

		Response response = invocationBuilder.post(Entity.json("{}"), Response.class);
		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			log.error("Error in getting wallet balance: " + walletId + " - " + response.getStatusInfo().toString()
					+ " :: " + response.getStatus());
			throw new DagCoinRestClientException(response.getStatusInfo().toString(), response.getStatus());
		}
		
		String responseBody = response.readEntity(String.class);
		if (!this.cryptoEngine.validateHeaders(response.getHeaders(), responseBody)) {
			log.error("Error in validaing headers");
			throw new DagCoinRestClientException("HMAC Authentication failed", 401);
		}

		String decryptedResponse = this.cryptoEngine.decrypt(responseBody);
		CustomerWalletBalanceResponse customerWalletBalanceResponse = new CustomerWalletBalanceResponse();
		try {
			customerWalletBalanceResponse = new ObjectMapper().readValue(decryptedResponse, CustomerWalletBalanceResponse.class);
		} catch (Exception e) {
			log.error("Error in mapping response for getBalance - " + response.getStatusInfo().toString() + " :: "
					+ response.getStatus());
			throw new DagCoinRestClientException("Error in mapping response", 422);
		}
		return customerWalletBalanceResponse;
	}

	/**
	 * Adds money to the given wallet ID
	 * 
	 * @param recipientWalletId - wallet address of the recipient
	 * @param amount - amount to be transferred/added
	 * @param currency - of the amount
	 * @return {@link TransactionResponse}
	 * @throws DagCoinRestClientException - if there is an error
	 */
	public TransactionResponse makeTransaction(String recipientWalletId, String amount, String currency)
			throws DagCoinRestClientException {
		log.info("Making transaction - " +
			" WalletID = " + recipientWalletId + 
			" Amount = " + amount +
			" Currency = " + currency);
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(this.params.getApiUrl()).path("/transaction/make");

		TransactionRequest tr = new TransactionRequest(this.cryptoEngine.encrypt(recipientWalletId),
				this.cryptoEngine.encrypt(currency), this.cryptoEngine.encrypt(amount));
		
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).headers(this.cryptoEngine.getHeaders(convertObjectToString(tr)));

		Response response = invocationBuilder.post(Entity.entity(tr, MediaType.APPLICATION_JSON_TYPE), Response.class);
		
		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			log.error("Error in making transaction: " + recipientWalletId + " - " + response.getStatusInfo().toString()
					+ " :: " + response.getStatus());
			throw new DagCoinRestClientException(response.getStatusInfo().toString(), response.getStatus());
		}
		
		String responseBody = response.readEntity(String.class);
		if (!this.cryptoEngine.validateHeaders(response.getHeaders(), responseBody)) {
			log.error("Error in validaing headers");
			throw new DagCoinRestClientException("HMAC Authentication failed", 401);
		}

		String decryptedResponse = this.cryptoEngine.decrypt(responseBody);
		TransactionResponse transactionResponse = new TransactionResponse();
		try {
			transactionResponse = new ObjectMapper().readValue(decryptedResponse, TransactionResponse.class);
		} catch (Exception e) {
			log.error("Error in mapping response for makeTransaction - " + response.getStatusInfo().toString() + " :: "
					+ response.getStatus());
			throw new DagCoinRestClientException("Error in mapping response", 422);
		}
		return transactionResponse;
	}
	
	/**
	 * Generates and returns a new paper wallet
	 * 
	 * @return {@link PaperWalletResponse}
	 * @throws DagCoinRestClientException - if there is an error
	 */
	public PaperWalletResponse generatePaperWallet() throws DagCoinRestClientException {
		log.info("Generating paper wallet");
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(this.params.getApiUrl()).path("/paper/wallet/generate");
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).headers(this.cryptoEngine.getHeaders("{}"));

		Response response = invocationBuilder.post(Entity.json("{}"), Response.class);
		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			log.error("Error in generating wallet - " + response.getStatusInfo().toString() + " :: "
					+ response.getStatus());
			throw new DagCoinRestClientException(response.getStatusInfo().toString(), response.getStatus());
		}
		
		String responseBody = response.readEntity(String.class);
		if (!this.cryptoEngine.validateHeaders(response.getHeaders(), responseBody)) {
			log.error("Error in validaing headers");
			throw new DagCoinRestClientException("HMAC Authentication failed", 401);
		}

		String decryptedResponse = this.cryptoEngine.decrypt(responseBody);
		PaperWalletResponse paperWalletResponse = new PaperWalletResponse();
		try {
			paperWalletResponse = new ObjectMapper().readValue(decryptedResponse, PaperWalletResponse.class);
		} catch (Exception e) {
			log.error("Error in generating paper wallet - " + response.getStatusInfo().toString() + " :: "
					+ response.getStatus());
			throw new DagCoinRestClientException("Error in generating paper wallet", 422);
		}
		return paperWalletResponse;
	}
	
	/**
	 * Utility method to convert object to string
	 * 
	 * @param obj - object to be converted to string
	 * @return
	 * @throws DagCoinRestClientException - in case of any exception
	 */
	private String convertObjectToString(Object obj) throws DagCoinRestClientException {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch(Exception e) {
			log.error("Could not convert object to string" + e.getMessage());
			throw new DagCoinRestClientException("Error in converting object to string - " + e.getMessage(), 422);
		}
	}

}
