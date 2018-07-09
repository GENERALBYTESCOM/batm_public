package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2;

import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCoinRequest;
import com.generalbytes.batm.server.extensions.extra.worldcoin.sources.cd.CompatSSLSocketFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class BitgoAPITest {

    private static final Logger log = LoggerFactory.getLogger(BitgoAPITest.class);

    private static IBitgoAPI api;

    private static final Integer readTimeout = 90 * 1000; //90 seconds

    @BeforeClass
    public static void setup() {
        System.setProperty("org.slf4j.simpleLogger.log.batm", "trace");
        System.setProperty("org.slf4j.simpleLogger.log.si.mazi.rescu","trace");

        ClientConfig config = new ClientConfig();
        config.setHttpReadTimeout(readTimeout);

        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            final CompatSSLSocketFactory socketFactory = new CompatSSLSocketFactory(sslcontext.getSocketFactory());
            config.setSslSocketFactory(socketFactory);
            config.setIgnoreHttpErrorCodes(true);
        }catch(KeyManagementException kme) {
            log.error("", kme);
        } catch (NoSuchAlgorithmException nae) {
            log.error("", nae);
        }
        api = RestProxyFactory.createProxy(IBitgoAPI.class, "https://test.bitgo.com/api", config);
    }

    @Test
    public void getWalletsTest() {
        final String accessToken = "Bearer v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";

        final Map<String, Object> result = api.getWallets(accessToken, "tbtc");
        Assert.assertNotNull(result);

        final Object coin = result.get("coin");
        Assert.assertNotNull(coin);
        Assert.assertTrue(coin instanceof String);
        final String stringCoin = (String) coin;
        Assert.assertEquals("tbtc", stringCoin);

        final Object wallets = result.get("wallets");
        Assert.assertNotNull(wallets);
        Assert.assertTrue(wallets instanceof ArrayList);
        final ArrayList list = (ArrayList)wallets;
        Assert.assertFalse(list.isEmpty());
        final Object element = list.get(0);
        Assert.assertNotNull(element);
        Assert.assertTrue(element instanceof LinkedHashMap);
        final LinkedHashMap map = (LinkedHashMap)element;
        Assert.assertNotNull(map);

        final String id = (String) map.get("id");
        Assert.assertNotNull(id);
        Assert.assertEquals("5b20e3a9266bbe80095757489d84a6bb", id);

        final Object users = map.get("users");
        Assert.assertNotNull(users);
        Assert.assertTrue(users instanceof ArrayList);

        final ArrayList userList = (ArrayList)users;
        Assert.assertFalse(userList.isEmpty());
        final Object user = userList.get(0);
        Assert.assertNotNull(user);
        Assert.assertTrue(user instanceof LinkedHashMap);
        LinkedHashMap userMap = (LinkedHashMap)user;
        String username = (String) userMap.get("user");
        Assert.assertNotNull(username);
        Assert.assertEquals("5b20e31ebd6c12af0983fcb383e19a21", username);

        final String cryptoCoin = (String) map.get("coin");
        Assert.assertNotNull(cryptoCoin);
        Assert.assertEquals("tbtc", cryptoCoin);
        String label = (String) map.get("label");
        Assert.assertEquals("TBTCWallet", label);

        log.debug("Wallet label = {}", label);
    }

    @Test
    public void getBalancesTest() {
        final String accessToken = "Bearer v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        final Map<String, Object> result = api.getTotalBalances(accessToken, "tbtc");
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof LinkedHashMap);

        final LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)result;
        Assert.assertFalse(map.isEmpty());

        final Integer balance = (Integer) map.get("balance");
        Assert.assertNotNull(balance);
        Assert.assertTrue(balance instanceof Integer);

        final String balanceString = (String) map.get("balanceString");
        Assert.assertNotNull(balanceString);
        Assert.assertTrue(balanceString instanceof String);

        final Integer confirmedBalance = (Integer) map.get("confirmedBalance");
        Assert.assertNotNull(confirmedBalance);

        final String confirmedBalanceString = (String) map.get("confirmedBalanceString");
        Assert.assertNotNull(confirmedBalanceString);

        final Integer spendableBalance = (Integer) map.get("spendableBalance");
        Assert.assertNotNull(spendableBalance);

        final String spendableBalanceString = (String) map.get("spendableBalanceString");
        Assert.assertNotNull(spendableBalanceString);
    }

    @Test
    public void getWalletByLabelTest() {
        final String accessToken = "Bearer v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        final String walletLabel = "TBTCWallet";
        final Map<String, Object> result = api.getWallets(accessToken, "tbtc");
        Assert.assertNotNull(result);
        List wallets = (List) result.get("wallets");
        Assert.assertNotNull(wallets);
        boolean walletFound = false;
        for(Object o : wallets) {
            if(!(o instanceof Map)) {
                continue;
            }
            Map m = (Map)o;
            String label = (String) m.get("label");
            if(walletLabel.equalsIgnoreCase(label)) {
                walletFound = true;
            }
        }

        Assert.assertTrue(walletFound);
    }

    @Test
    public void getWalletTest() {
        final String accessToken = "Bearer v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        final String walletId = "5b20e3a9266bbe80095757489d84a6bb";
        final Map<String, Object> result = api.getWalletById(accessToken, "tbtc", walletId);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof LinkedHashMap);

        final String id = (String) result.get("id");
        Assert.assertEquals("5b20e3a9266bbe80095757489d84a6bb", id);

        final String coin = (String) result.get("coin");
        Assert.assertNotNull(coin);
        Assert.assertEquals("tbtc", coin);

        final String label = (String) result.get("label");
        Assert.assertEquals("TBTCWallet", label);
    }

    @Test
    public void getWalletByAIdTest() {
        final String accessToken = "Bearer v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        String address = "5b20e3a9266bbe80095757489d84a6bb";

        final Map<String, Object> result = api.getWalletById(accessToken, "tbtc", address);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof LinkedHashMap);

        String id = (String) result.get("id");
        Assert.assertEquals("5b20e3a9266bbe80095757489d84a6bb", id);

        String coin = (String) result.get("coin");
        Assert.assertNotNull(coin);
        Assert.assertEquals("tbtc", coin);

        final String label = (String) result.get("label");
        Assert.assertEquals("TBTCWallet", label);

        final Map<String, Object> receiveAddressMap = (Map<String, Object>) result.get("receiveAddress");
        Assert.assertNotNull(receiveAddressMap);
        Assert.assertFalse(receiveAddressMap.isEmpty());

        id = (String) receiveAddressMap.get("id");
        Assert.assertNotNull(id);
        Assert.assertTrue(id.startsWith("5b"));

        String walletId = (String) receiveAddressMap.get("wallet");
        Assert.assertNotNull(walletId);
        Assert.assertEquals("5b20e3a9266bbe80095757489d84a6bb", walletId);

        coin = (String)receiveAddressMap.get("coin");
        Assert.assertNotNull(coin);
        Assert.assertEquals("tbtc", coin);

        address = (String)receiveAddressMap.get("address");
        Assert.assertNotNull(address);
        log.info("Address = {}", address);
    }

    @Test
    @Ignore("Local instance of bitgo-express test environment is required to run")
    public void sendCoinsTest() {
        try {
            IBitgoAPI localapi = RestProxyFactory.createProxy(IBitgoAPI.class, "http://localhost:3080/api");
            final String coin = "tbtc";
            final String id = "5b20e3a9266bbe80095757489d84a6bb";
            final String address = "2N5q4MwNSUxbAtaidhRgkiDrbwVR4yCZDhi";
            final Integer amount = 10000;
            final String walletPassphrase = "JSZSuGNlHfgqPHjrp0eO";

            final BitGoCoinRequest request = new BitGoCoinRequest(address, amount, walletPassphrase);
            String accessToken = "Bearer v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
            String contentType = "application/json";
            Map<String, String> result = localapi.sendCoins(accessToken, contentType, coin, id, request);
            Assert.assertNotNull(result);

            String status = result.get("status");
            Assert.assertNotNull(status);
            Assert.assertEquals("signed", status);

            String tx = result.get("tx");
            Assert.assertNotNull(tx);

            String txid = result.get("txid");
            Assert.assertNotNull(txid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
