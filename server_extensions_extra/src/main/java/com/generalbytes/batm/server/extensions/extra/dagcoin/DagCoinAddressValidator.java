package com.generalbytes.batm.server.extensions.extra.dagcoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.DagCoinParameters;
import com.generalbytes.batm.server.extensions.extra.dagcoin.exception.DagCoinRestClientException;
import com.generalbytes.batm.server.extensions.extra.dagcoin.service.DagCoinApiClientService;

public class DagCoinAddressValidator implements ICryptoAddressValidator {

	private static final Logger log = LoggerFactory.getLogger(DagCoinAddressValidator.class);
	private DagCoinParameters params;
	
	public DagCoinAddressValidator(DagCoinParameters params) {
		log.info("Inside DagCoinAddressValidator, with params - " +
				params.getApiUrl() + params.getEncryptionKey() + params.getPublicKey() + params.getPrivateKey());
		this.params = params;
	}

	@Override
	public boolean isAddressValid(String address) {
		log.info("Inside isAddressValid - Checking if wallet address if valid - " + address);
		try {
			DagCoinApiClientService service = new DagCoinApiClientService(this.params);
			return service.validateWalletAddress(address).getIsValid();
		} catch (DagCoinRestClientException ex) {
			log.error("Error in validating wallet address" + ex.getErrorCode() + " :: " + ex.getMessage());
			return false;
		}
	}

	@Override
	public boolean mustBeBase58Address() {
		log.info("Inside mustBeBase58Address, returning false");
		return false;
	}

	@Override
	public boolean isPaperWalletSupported() {
		log.info("Inside isPaperWalletSupported, returning false");
		return false;
	}

}
