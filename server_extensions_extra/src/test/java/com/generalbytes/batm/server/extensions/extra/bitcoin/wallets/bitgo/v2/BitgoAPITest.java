package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCoinRequest;
import com.generalbytes.batm.server.extensions.util.net.CompatSSLSocketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import javax.ws.rs.HeaderParam;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class BitgoAPITest {

    private static final Logger log = LoggerFactory.getLogger(BitgoAPITest.class);

    private static IBitgoAPI api;

    private static final Integer readTimeout = 90 * 1000; //90 seconds

    private static void setLoggerLevel(String name, String level) {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            List<ch.qos.logback.classic.Logger> loggers = loggerContext.getLoggerList();

            for (ch.qos.logback.classic.Logger logger : loggers) {
                if (logger.getName().startsWith(name)) {
                    logger.setLevel(Level.toLevel(level));
                }
            }
        } catch (Throwable e) {
            log.error("batm.master.ServerUtil - setLoggerLevel");
            log.error("setLoggerLevel", e);
        }
    }

    @BeforeEach
    void setUp() {
        setLoggerLevel("batm", "trace");
        setLoggerLevel("si.mazi.rescu","trace");

        ClientConfig config = new ClientConfig();
        config.setHttpReadTimeout(readTimeout);
        config.addDefaultParam(HeaderParam.class, "Authorization", "Bearer v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21");

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
        api = RestProxyFactory.createProxy(IBitgoAPI.class, "https://test.bitgo.com/", config);
    }

    @Test
    @Disabled
    void getWalletsTest() throws IOException {

        final Map<String, Object> result = api.getWallets("tbtc");
        assertNotNull(result);

        final Object coin = result.get("coin");
        assertNotNull(coin);
        assertTrue(coin instanceof String);
        final String stringCoin = (String) coin;
        assertEquals("tbtc", stringCoin);

        final Object wallets = result.get("wallets");
        assertNotNull(wallets);
        assertTrue(wallets instanceof ArrayList);
        final ArrayList list = (ArrayList)wallets;
        assertFalse(list.isEmpty());
        final Object element = list.get(0);
        assertNotNull(element);
        assertTrue(element instanceof LinkedHashMap);
        final LinkedHashMap map = (LinkedHashMap)element;
        assertNotNull(map);

        final String id = (String) map.get("id");
        assertNotNull(id);
        assertEquals("5b20e3a9266bbe80095757489d84a6bb", id);

        final Object users = map.get("users");
        assertNotNull(users);
        assertTrue(users instanceof ArrayList);

        final ArrayList userList = (ArrayList)users;
        assertFalse(userList.isEmpty());
        final Object user = userList.get(0);
        assertNotNull(user);
        assertTrue(user instanceof LinkedHashMap);
        LinkedHashMap userMap = (LinkedHashMap)user;
        String username = (String) userMap.get("user");
        assertNotNull(username);
        assertEquals("5b20e31ebd6c12af0983fcb383e19a21", username);

        final String cryptoCoin = (String) map.get("coin");
        assertNotNull(cryptoCoin);
        assertEquals("tbtc", cryptoCoin);
        String label = (String) map.get("label");
        assertEquals("TBTCWallet", label);

        log.debug("Wallet label = {}", label);
    }

    @Test
    @Disabled
    void getBalancesTest() throws IOException {
        final String accessToken = "Bearer v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        final Map<String, Object> result = api.getTotalBalances("tbtc");
        assertNotNull(result);
        assertTrue(result instanceof LinkedHashMap);

        final LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)result;
        assertFalse(map.isEmpty());

        final Integer balance = (Integer) map.get("balance");
        assertNotNull(balance);
        assertTrue(balance instanceof Integer);

        final String balanceString = (String) map.get("balanceString");
        assertNotNull(balanceString);
        assertTrue(balanceString instanceof String);

        final Integer confirmedBalance = (Integer) map.get("confirmedBalance");
        assertNotNull(confirmedBalance);

        final String confirmedBalanceString = (String) map.get("confirmedBalanceString");
        assertNotNull(confirmedBalanceString);

        final Integer spendableBalance = (Integer) map.get("spendableBalance");
        assertNotNull(spendableBalance);

        final String spendableBalanceString = (String) map.get("spendableBalanceString");
        assertNotNull(spendableBalanceString);
    }

    @Test
    @Disabled
    void getWalletByLabelTest() throws IOException {
        final String accessToken = "Bearer v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        final String walletLabel = "TBTCWallet";
        final Map<String, Object> result = api.getWallets("tbtc");
        assertNotNull(result);
        List wallets = (List) result.get("wallets");
        assertNotNull(wallets);
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

        assertTrue(walletFound);
    }

    @Disabled
    @Test
    void getWalletTest() throws IOException {
        final String accessToken = "Bearer v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        final String walletId = "5b20e3a9266bbe80095757489d84a6bb";
        final Map<String, Object> result = api.getWalletById("tbtc", walletId);
        assertNotNull(result);
        assertTrue(result instanceof LinkedHashMap);

        final String id = (String) result.get("id");
        assertEquals("5b20e3a9266bbe80095757489d84a6bb", id);

        final String coin = (String) result.get("coin");
        assertNotNull(coin);
        assertEquals("tbtc", coin);

        final String label = (String) result.get("label");
        assertEquals("TBTCWallet", label);
    }

    @Disabled
    @Test
    void getWalletByAIdTest() throws IOException {
        final String accessToken = "Bearer v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        String address = "5b20e3a9266bbe80095757489d84a6bb";

        final Map<String, Object> result = api.getWalletById("tbtc", address);
        assertNotNull(result);
        assertTrue(result instanceof LinkedHashMap);

        String id = (String) result.get("id");
        assertEquals("5b20e3a9266bbe80095757489d84a6bb", id);

        String coin = (String) result.get("coin");
        assertNotNull(coin);
        assertEquals("tbtc", coin);

        final String label = (String) result.get("label");
        assertEquals("TBTCWallet", label);

        final Map<String, Object> receiveAddressMap = (Map<String, Object>) result.get("receiveAddress");
        assertNotNull(receiveAddressMap);
        assertFalse(receiveAddressMap.isEmpty());

        id = (String) receiveAddressMap.get("id");
        assertNotNull(id);
        assertTrue(id.startsWith("5b"));

        String walletId = (String) receiveAddressMap.get("wallet");
        assertNotNull(walletId);
        assertEquals("5b20e3a9266bbe80095757489d84a6bb", walletId);

        coin = (String)receiveAddressMap.get("coin");
        assertNotNull(coin);
        assertEquals("tbtc", coin);

        address = (String)receiveAddressMap.get("address");
        assertNotNull(address);
        log.info("Address = {}", address);
    }

    @Test
    @Disabled("Local instance of bitgo-express test environment is required to run")
    void sendCoinsTest() {
        try {
            IBitgoAPI localapi = RestProxyFactory.createProxy(IBitgoAPI.class, "http://localhost:3080/");
            final String coin = "tbtc";
            final String id = "5b20e3a9266bbe80095757489d84a6bb";
            final String address = "2N5q4MwNSUxbAtaidhRgkiDrbwVR4yCZDhi";
            final String amount = "10000";
            final String walletPassphrase = "JSZSuGNlHfgqPHjrp0eO";

            final BitGoCoinRequest request = new BitGoCoinRequest(address, amount, walletPassphrase, "TXID", 2);
            String accessToken = "Bearer v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
            String contentType = "application/json";
            Map<String, Object> result = localapi.sendCoins(coin, id, request);
            assertNotNull(result);

            Object statusO = result.get("status");
            assertTrue(statusO instanceof String);
            String status = (String) statusO;
            assertNotNull(status);
            assertEquals("signed", status);

            Object tx = result.get("tx");
            assertNotNull(tx);

            Object txid = result.get("txid");
            assertNotNull(txid);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

}
