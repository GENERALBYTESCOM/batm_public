package com.generalbytes.batm.server.extensions.extra.stellar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

public class StellarcoinAddressValidator implements ICryptoAddressValidator {
	private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.StellarcoinAddressValidator");

	@Override
	public boolean isAddressValid(String address) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mustBeBase58Address() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPaperWalletSupported() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
