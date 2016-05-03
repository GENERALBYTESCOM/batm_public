package com.generalbytes.batm.server.extensions.extra.shadowcash;


import org.bitcoinj.params.MainNetParams;

/**
 * @author ludx
 */
public class ShadowcashMainNetParams extends MainNetParams {

    private static ShadowcashMainNetParams instance;

    public ShadowcashMainNetParams() {
        this.id = "shadowcash.main";
        this.addressHeader = 63;
        this.p2shHeader = 125;
        this.acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        this.spendableCoinbaseDepth = 500;
        this.dumpedPrivateKeyHeader = 191;

    }

    public static synchronized ShadowcashMainNetParams get() {
        if(instance == null) {
            instance = new ShadowcashMainNetParams();
        }
        return instance;
    }
}