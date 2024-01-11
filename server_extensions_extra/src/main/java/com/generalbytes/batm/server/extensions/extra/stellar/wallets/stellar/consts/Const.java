package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.consts;

public class Const {

	public static final String CREATEWALLET = "/wallet/create/";
	public static final String TRANSACTION = "/wallet/%s/sign-envelope/payment/";
	public static final String GETWALLET = "/wallet/%s/";
	public static final String BRODCAST="/wallet/%s/submit-transaction-onchain/";
	public static final String BALANCEAPI="https://horizon-mainnet.stellar.org/accounts/";

}
