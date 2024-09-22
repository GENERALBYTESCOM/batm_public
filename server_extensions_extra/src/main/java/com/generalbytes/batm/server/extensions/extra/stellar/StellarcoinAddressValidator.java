package com.generalbytes.batm.server.extensions.extra.stellar;

import org.apache.commons.lang3.StringUtils;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

public class StellarcoinAddressValidator implements ICryptoAddressValidator {

	@Override
	public boolean isAddressValid(String address) {
		if (address.length() < 56) {
			System.out.println("Error: Stellar public keys should be 56 characters long");
			return false;
		} else if (address.equals("0")) {
			System.out.println(
					"Error: Although technically the correct length, a key of all zeros is not a valid Stellar public key.\n"
							+ "");
			return false;
		}

		else if (!address.startsWith("G")) {
			System.out.println("Error:Stellar public keys should start with the letter G");
			return false;
		} else

		if (!StringUtils.isAlphanumeric(address)) {
			System.out.println("Error: Stellar public keys should only contain alphanumeric characters.");
			return false;
		}
		return true;
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
