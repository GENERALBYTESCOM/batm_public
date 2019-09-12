package com.generalbytes.batm.server.extensions.extra.dagcoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dagcoin.exception.DagCoinRestClientException;
import com.dagcoin.service.DagCoinApiClientService;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

public class DagCoinAddressValidator implements ICryptoAddressValidator {

	private static final Logger log = LoggerFactory.getLogger(DagCoinAddressValidator.class);

	@Override
	public boolean isAddressValid(String address) {
		log.info("Checking if wallet address if valid - " + address);
		try {
			DagCoinApiClientService service = new DagCoinApiClientService();
			return service.validateWalletAddress(address).getIsValid();
		} catch (DagCoinRestClientException ex) {
			log.error("Error in validating wallet address" + ex.getErrorCode() + " :: " + ex.getMessage());
			return false;
		}
	}

	@Override
	public boolean mustBeBase58Address() {
		return false;
	}

	@Override
	public boolean isPaperWalletSupported() {
		return false;
	}

}
