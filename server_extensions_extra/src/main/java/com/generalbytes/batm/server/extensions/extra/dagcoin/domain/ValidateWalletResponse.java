package com.generalbytes.batm.server.extensions.extra.dagcoin.domain;

/**
 * Response class for GET /wallet/{walletId}/validation/check
 * 
 * @author shubhrapahwa
 *
 */
public class ValidateWalletResponse {
	
	private boolean isValid;

	public boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(boolean isValid) {
		this.isValid = isValid;
	}
	

}
