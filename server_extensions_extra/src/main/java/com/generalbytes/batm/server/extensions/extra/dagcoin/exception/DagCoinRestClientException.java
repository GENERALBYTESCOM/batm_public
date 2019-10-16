package com.generalbytes.batm.server.extensions.extra.dagcoin.exception;

public class DagCoinRestClientException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DagCoinRestClientException(String message, int errorCode) {
		super();
		this.message = message;
		this.errorCode = errorCode;
	}
	
	private String message;
	private int errorCode;
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	

}
