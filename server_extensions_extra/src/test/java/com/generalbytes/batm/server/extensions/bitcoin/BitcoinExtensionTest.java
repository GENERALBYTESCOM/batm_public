package com.generalbytes.batm.server.extensions.bitcoin;

import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.BitcoinExtension;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.BitgoWallet;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class BitcoinExtensionTest {

    private static final Logger log = LoggerFactory.getLogger(BitcoinExtensionTest.class);

    @Test
    public void bitgoFullTokenTest() {
        String wallet = "bitgo:http://localhost:3080:v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21:5b20e3a9266bbe80095757489d84a6bb:Vranec8586";
        StringTokenizer st = new StringTokenizer(wallet,":");
        String walletType = st.nextToken();
        String first = st.nextToken();
        String protocol = null;
        String host = "";
        String fullHost = null;
        if(first != null && first.startsWith("http")) {
            protocol = first ;
            host = st.nextToken();
            fullHost = protocol + ":" + host;
        } else {
            host = first;
            fullHost = host;
        }

        String port = "";
        String token = "";
        String next = st.nextToken();
        if(next != null && next.length() > 6) {
            token = next;
        } else {
            port = next;
            token = st.nextToken();
        }
        String walletAddress = st.nextToken();
        String walletPassphrase = st.nextToken();

        log.info("wallet = {}, protocol = {}, host = {}, port = {}, fullHost = {}, token = {}, address = {}, passphrase = {}, next = {}", walletType, protocol, host, port, fullHost, token, walletAddress, walletPassphrase, next);

        Assert.assertEquals("http", protocol);
        Assert.assertEquals("//localhost", host);
        Assert.assertEquals("http://localhost", fullHost);
        Assert.assertEquals("3080", port);

        final BitcoinExtension bitcoinExtension = new BitcoinExtension();
        final IWallet bitgowallet = bitcoinExtension.createWallet(wallet);
        Assert.assertTrue(bitgowallet instanceof BitgoWallet);
        final BitgoWallet bitgoWallet = (BitgoWallet)bitgowallet;
        Assert.assertNotNull(bitgoWallet);
        Assert.assertEquals("http://localhost:3080/api", bitgoWallet.getUrl());
        Assert.assertEquals("5b20e3a9266bbe80095757489d84a6bb", bitgoWallet.getWalletId());
        log.info("wallet = " + bitgoWallet);
    }

    @Test
    public void bitgoNoPortTokenTest() {
        String wallet = "bitgo:http://localhost:v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21:5b20e3a9266bbe80095757489d84a6bb:Vranec8586";
        StringTokenizer st = new StringTokenizer(wallet,":");
        String walletType = st.nextToken();
        String first = st.nextToken();
        String protocol = "";
        String host = "";
        String fullHost = "";
        if(first != null && first.startsWith("http")) {
            protocol = first ;
            host = st.nextToken();
            fullHost = protocol + ":" + host;
        } else {
            host = first;
            fullHost = host;
        }

        String port = "";
        String token = "";
        String next = st.nextToken();
        if(next != null && next.length() > 6) {
            token = next;
        } else {
            port = next;
            token = st.nextToken();
        }
        String walletAddress = st.nextToken();
        String walletPassphrase = st.nextToken();

        log.info("wallet = {}, protocol = {}, host = {}, port = {}, fullHost = {}, token = {}, address = {}, passphrase = {}, next = {}", walletType, protocol, host, port, fullHost, token, walletAddress, walletPassphrase, next);

        Assert.assertEquals("http", protocol);
        Assert.assertEquals("//localhost", host);
        Assert.assertEquals("http://localhost", fullHost);
        Assert.assertEquals("", port);

        final BitcoinExtension bitcoinExtension = new BitcoinExtension();
        final IWallet bitgowallet = bitcoinExtension.createWallet(wallet);
        Assert.assertTrue(bitgowallet instanceof BitgoWallet);
        final BitgoWallet bitgoWallet = (BitgoWallet)bitgowallet;
        Assert.assertNotNull(bitgoWallet);
        Assert.assertEquals("http://localhost/api", bitgoWallet.getUrl());
        Assert.assertEquals("5b20e3a9266bbe80095757489d84a6bb", bitgoWallet.getWalletId());
        log.info("wallet = " + bitgoWallet);
    }

    @Test
    public void bitgoHttpsTokenTest() {
        String wallet = "bitgo:https://localhost:3080:v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21:5b20e3a9266bbe80095757489d84a6bb:Vranec8586";
        StringTokenizer st = new StringTokenizer(wallet,":");
        String walletType = st.nextToken();
        String first = st.nextToken();
        String protocol = "";
        String host = "";
        String fullHost = "";
        if(first != null && first.startsWith("http")) {
            protocol = first ;
            host = st.nextToken();
            fullHost = protocol + ":" + host;
        } else {
            host = first;
            fullHost = host;
        }

        String port = "";
        String token = "";
        String next = st.nextToken();
        if(next != null && next.length() > 6) {
            token = next;
        } else {
            port = next;
            token = st.nextToken();
        }
        String walletAddress = st.nextToken();
        String walletPassphrase = st.nextToken();

        log.info("wallet = {}, protocol = {}, host = {}, port = {}, fullHost = {}, token = {}, address = {}, passphrase = {}, next = {}", walletType, protocol, host, port, fullHost, token, walletAddress, walletPassphrase, next);

        Assert.assertEquals("https", protocol);
        Assert.assertEquals("//localhost", host);
        Assert.assertEquals("https://localhost", fullHost);
        Assert.assertEquals("3080", port);

        final BitcoinExtension bitcoinExtension = new BitcoinExtension();
        final IWallet bitgowallet = bitcoinExtension.createWallet(wallet);
        Assert.assertTrue(bitgowallet instanceof BitgoWallet);
        final BitgoWallet bitgoWallet = (BitgoWallet)bitgowallet;
        Assert.assertNotNull(bitgoWallet);
        Assert.assertEquals("https://localhost:3080/api", bitgoWallet.getUrl());
        Assert.assertEquals("5b20e3a9266bbe80095757489d84a6bb", bitgoWallet.getWalletId());
        log.info("wallet = " + bitgoWallet);
    }

    @Test
    public void bitgoNoProtocolTokenTest() {
        String wallet = "bitgo:localhost:3080:v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21:5b20e3a9266bbe80095757489d84a6bb:Vranec8586";
        StringTokenizer st = new StringTokenizer(wallet,":");
        String walletType = st.nextToken();
        String first = st.nextToken();
        String protocol = "";
        String host = "";
        String fullHost = "";
        if(first != null && first.startsWith("http")) {
            protocol = first ;
            host = st.nextToken();
            fullHost = protocol + ":" + host;
        } else {
            host = first;
            fullHost = host;
        }

        String port = "";
        String token = "";
        String next = st.nextToken();
        if(next != null && next.length() > 6) {
            token = next;
        } else {
            port = next;
            token = st.nextToken();
        }
        String walletAddress = st.nextToken();
        String walletPassphrase = st.nextToken();

        log.info("wallet = {}, protocol = {}, host = {}, port = {}, fullHost = {}, token = {}, address = {}, passphrase = {}, next = {}", walletType, protocol, host, port, fullHost, token, walletAddress, walletPassphrase, next);

        Assert.assertEquals("", protocol);
        Assert.assertEquals("localhost", host);
        Assert.assertEquals("localhost", fullHost);
        Assert.assertEquals("3080", port);

        final BitcoinExtension bitcoinExtension = new BitcoinExtension();
        final IWallet bitgowallet = bitcoinExtension.createWallet(wallet);
        Assert.assertTrue(bitgowallet instanceof BitgoWallet);
        final BitgoWallet bitgoWallet = (BitgoWallet)bitgowallet;
        Assert.assertNotNull(bitgoWallet);
        Assert.assertEquals("http://localhost:3080/api", bitgoWallet.getUrl());
        Assert.assertEquals("5b20e3a9266bbe80095757489d84a6bb", bitgoWallet.getWalletId());
        log.info("wallet = " + bitgoWallet);
    }

    @Test
    public void bitgoWalletTest() {
        String address = "bitgo:http://localhost:3080:v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21:5b20e3a9266bbe80095757489d84a6bb:Vranec8586";
        final BitcoinExtension bitcoinExtension = new BitcoinExtension();
        final IWallet wallet = bitcoinExtension.createWallet(address);
        Assert.assertTrue(wallet instanceof BitgoWallet);
        final BitgoWallet bitgoWallet = (BitgoWallet)wallet;
        Assert.assertNotNull(bitgoWallet);
        Assert.assertNotNull(bitgoWallet);
        Assert.assertEquals("http://localhost:3080/api", bitgoWallet.getUrl());
        Assert.assertEquals("5b20e3a9266bbe80095757489d84a6bb", bitgoWallet.getWalletId());
        log.info("wallet = " + bitgoWallet);
    }

    @Test
    public void bitgoErrorTest() {
        String body = "{\"error\":\"invalid address for network\",\"name\":\"Invalid\",\"requestId\":\"cjjectv6y2zlgill815ppvp4g\",\"message\":\"invalid address for network\"}";
        int index1 = body.indexOf("error") + 8;
        int index2 = body.indexOf(",") - 1;
        String value = body.substring(index1, index2);
        Assert.assertNotNull(value);
        Assert.assertEquals("invalid address for network", value);

        body = "{\"error\":\"sub-dust-threshold amount for LWcx7VHK3hKDV5KaiE2EQLPpGt9GEVwzdM: 10000\",\"name\":\"Invalid\",\"requestId\":\"cjjebbtly2ew1gmldyr9h2dnk\",\"message\":\"sub-dust-threshold amount for LWcx7VHK3hKDV5KaiE2EQLPpGt9GEVwzdM: 10000\"}";
        index1 = body.indexOf("error") + 8;
        index2 = body.indexOf(",") - 1;
        value = body.substring(index1, index2);
        Assert.assertNotNull(value);
        Assert.assertEquals("sub-dust-threshold amount for LWcx7VHK3hKDV5KaiE2EQLPpGt9GEVwzdM: 10000", value);

        body = "{\"error\":\"needs unlock\",\"needsOTP\":true,\"needsUnlock\":true,\"name\":\"Response\",\"requestId\":\"cjjebd0lq2ke0g7l73czlu75j\",\"message\":\"needs unlock\"}";
        index1 = body.indexOf("error") + 8;
        index2 = body.indexOf(",") - 1;
        value = body.substring(index1, index2);
        Assert.assertNotNull(value);
        Assert.assertEquals("needs unlock", value);
    }
}
