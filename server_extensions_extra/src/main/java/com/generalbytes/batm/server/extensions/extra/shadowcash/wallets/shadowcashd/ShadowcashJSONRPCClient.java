package com.generalbytes.batm.server.extensions.extra.shadowcash.wallets.shadowcashd;

import com.azazar.bitcoin.jsonrpcclient.BitcoinException;
import com.azazar.bitcoin.jsonrpcclient.BitcoinJSONRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * @author ludx
 */
public class ShadowcashJSONRPCClient extends BitcoinJSONRPCClient {

    private static final Logger log = LoggerFactory.getLogger(ShadowcashJSONRPCClient.class);

    public ShadowcashJSONRPCClient(String rpcUrl) throws MalformedURLException {
        super(new URL(rpcUrl));
    }

    public ShadowInfo getInfo() throws BitcoinException {
        Map<String, String> info = (Map<String, String>)this.query("getinfo", new Object[0]);
        //log.debug( "INFO: {}", info);
        return null;
    }

    public static interface ShadowInfo {
        int protocolversion();
        int walletversion();
        double balance();
        int blocks();
        int timeoffset();
        int connections();
        String proxy();
        boolean testnet();
        int keypoololdest();
        int keypoolsize();
        int unlocked_until();
        double paytxfee();
        double relayfee();
        String errors();

        int version();
        //double difficulty();
        String mode();
        String state();
        double shadowbalance();
        double newmint();
        double stake();
        double reserve();
        double moneysupply();
        double shadowsupply();

        String datareceived();
        String datasent();
        String ip();

        double mininput();
    }

}
