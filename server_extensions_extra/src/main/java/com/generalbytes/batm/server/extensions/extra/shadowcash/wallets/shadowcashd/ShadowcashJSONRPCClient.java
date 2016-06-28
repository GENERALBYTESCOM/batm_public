package com.generalbytes.batm.server.extensions.extra.shadowcash.wallets.shadowcashd;

import com.azazar.bitcoin.jsonrpcclient.BitcoinException;
import com.azazar.bitcoin.jsonrpcclient.BitcoinJSONRPCClient;
import com.azazar.bitcoin.jsonrpcclient.BitcoinRPCException;
import com.azazar.krotjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * @author ludx
 */
public class ShadowcashJSONRPCClient extends BitcoinJSONRPCClient {

    private static final Logger log = LoggerFactory.getLogger(ShadowcashJSONRPCClient.class);
    private ObjectMapper mapper = new ObjectMapper();

    public ShadowcashJSONRPCClient(String rpcUrl) throws MalformedURLException {
        super(new URL(rpcUrl));
    }

    public ShadowInfo getInfo() throws BitcoinException {
        Map<String, String> info = (Map<String, String>)this.query("getinfo", new Object[0]);
        //log.debug( "INFO: {}", info);
        //ShadowInfo shadowInfo = (ShadowInfo)this.query("getinfo", new Object[0]);
        final ShadowInfo shadowInfo = mapper.convertValue(info, ShadowInfo.class);
        return shadowInfo;
    }

    public Object loadResponse(InputStream in, Object expectedID, boolean close) throws IOException, BitcoinException {
        try {
            String r = new String(loadStream(in, close), QUERY_CHARSET);
            try {
                Map response = (Map) JSON.parse(r);
                // shadowcoind returns null as id, not "1" as has been hardcoded as expectedID
                //if (!expectedID.equals(response.get("id"))){
                //    throw new BitcoinRPCException("Wrong response ID (expected: "+String.valueOf(expectedID) + ", response: "+response.get("id")+")");
                //}
                if (response.get("error") != null){
                    throw new BitcoinException(JSON.stringify(response.get("error")));
                }
                return response.get("result");
            } catch (ClassCastException ex) {
                throw new BitcoinRPCException("Invalid server response format (data: \"" + r + "\")");
            }
        } finally {
            if (close){
                in.close();
            }
        }
    }

    private static byte[] loadStream(InputStream in, boolean close) throws IOException {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for(;;) {
            int nr = in.read(buffer);
            if (nr == -1){
                break;
            }
            if (nr == 0){
                throw new IOException("Read timed out");
            }
            o.write(buffer, 0, nr);
        }
        return o.toByteArray();
    }

    public BigDecimal getBalanceAsBigDecimal() throws BitcoinException {
        return new BigDecimal(this.query("getbalance", new Object[0]).toString());
    }


    public double getBalance() throws BitcoinException {
        return ((Number)this.query("getbalance", new Object[0])).doubleValue();
    }

    public static class Difficulty {
        @JsonProperty("proof-of-work")
        private Double proofOfWork;
        @JsonProperty("proof-of-stake")
        private Double proofOfStake;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShadowInfo {
        private String version;
        private String mode;
        private String state;
        private int protocolversion;
        private int walletversion;
        private BigDecimal balance;
        private BigDecimal shadowbalance;
        private BigDecimal newmint;
        private BigDecimal stake;
        private BigDecimal reserve;
        private int blocks;
        private int timeoffset;
        private BigDecimal moneysupply;
        private BigDecimal shadowsupply;
        private int connections;
        private String datareceived;
        private String datasent;
        private String proxy;
        private String ip;
        //private Difficulty difficulty;
        private boolean testnet;
        private int keypoolsize;
        private BigDecimal paytxfee;
        private BigDecimal mininput;
        private String errors;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public int getProtocolversion() {
            return protocolversion;
        }

        public void setProtocolversion(int protocolversion) {
            this.protocolversion = protocolversion;
        }

        public int getWalletversion() {
            return walletversion;
        }

        public void setWalletversion(int walletversion) {
            this.walletversion = walletversion;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public BigDecimal getShadowbalance() {
            return shadowbalance;
        }

        public void setShadowbalance(BigDecimal shadowbalance) {
            this.shadowbalance = shadowbalance;
        }

        public BigDecimal getNewmint() {
            return newmint;
        }

        public void setNewmint(BigDecimal newmint) {
            this.newmint = newmint;
        }

        public BigDecimal getStake() {
            return stake;
        }

        public void setStake(BigDecimal stake) {
            this.stake = stake;
        }

        public BigDecimal getReserve() {
            return reserve;
        }

        public void setReserve(BigDecimal reserve) {
            this.reserve = reserve;
        }

        public int getBlocks() {
            return blocks;
        }

        public void setBlocks(int blocks) {
            this.blocks = blocks;
        }

        public int getTimeoffset() {
            return timeoffset;
        }

        public void setTimeoffset(int timeoffset) {
            this.timeoffset = timeoffset;
        }

        public BigDecimal getMoneysupply() {
            return moneysupply;
        }

        public void setMoneysupply(BigDecimal moneysupply) {
            this.moneysupply = moneysupply;
        }

        public BigDecimal getShadowsupply() {
            return shadowsupply;
        }

        public void setShadowsupply(BigDecimal shadowsupply) {
            this.shadowsupply = shadowsupply;
        }

        public int getConnections() {
            return connections;
        }

        public void setConnections(int connections) {
            this.connections = connections;
        }

        public String getDatareceived() {
            return datareceived;
        }

        public void setDatareceived(String datareceived) {
            this.datareceived = datareceived;
        }

        public String getDatasent() {
            return datasent;
        }

        public void setDatasent(String datasent) {
            this.datasent = datasent;
        }

        public String getProxy() {
            return proxy;
        }

        public void setProxy(String proxy) {
            this.proxy = proxy;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public boolean isTestnet() {
            return testnet;
        }

        public void setTestnet(boolean testnet) {
            this.testnet = testnet;
        }

        public int getKeypoolsize() {
            return keypoolsize;
        }

        public void setKeypoolsize(int keypoolsize) {
            this.keypoolsize = keypoolsize;
        }

        public BigDecimal getPaytxfee() {
            return paytxfee;
        }

        public void setPaytxfee(BigDecimal paytxfee) {
            this.paytxfee = paytxfee;
        }

        public BigDecimal getMininput() {
            return mininput;
        }

        public void setMininput(BigDecimal mininput) {
            this.mininput = mininput;
        }

        public String getErrors() {
            return errors;
        }

        public void setErrors(String errors) {
            this.errors = errors;
        }
    }

}
