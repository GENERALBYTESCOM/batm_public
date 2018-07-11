package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrax;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.IBittrexAPI;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto.BtCurrenciesDto;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto.BtResponseTickerDto;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto.BtResponseCurrenciesDto;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto.BtTickerDto;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.BitgoAPITest;
import com.generalbytes.batm.server.extensions.extra.worldcoin.sources.cd.CompatSSLSocketFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BittrexAPITest {

    private static final Logger log = LoggerFactory.getLogger(BitgoAPITest.class);
    private static final Integer readTimeout = 90 * 1000; //90 seconds
    private static IBittrexAPI api;

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
        api = RestProxyFactory.createProxy(IBittrexAPI.class, "https://bittrex.com/api");
    }

    @Test
    @Ignore
    public void getCurrenciesTest() {
        final BtResponseCurrenciesDto response = api.getCurrencies();
        Assert.assertNotNull(response);

        final Boolean isSuccess = response.getSuccess();
        Assert.assertNotNull(isSuccess);
        Assert.assertTrue(isSuccess.booleanValue());

        final String message = response.getMessage();
        Assert.assertNotNull(message);
        Assert.assertTrue(message.isEmpty());

        final List<BtCurrenciesDto> result = response.getResult();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());

        final BtCurrenciesDto dto = result.get(0);
        Assert.assertNotNull(dto);

        Assert.assertEquals("BTC", dto.getCurrency());
        Assert.assertEquals("Bitcoin", dto.getCurrencyLong());
        Assert.assertEquals("BITCOIN", dto.getCoinType());
    }

    @Test
    public void getTickerBTResponseTest() {
        final BtResponseTickerDto response = api.getTicker("USD-BTC");
        Assert.assertNotNull(response);

        final Boolean isSuccess = response.getSuccess();
        Assert.assertNotNull(isSuccess);
        Assert.assertTrue(isSuccess.booleanValue());

        final String message = response.getMessage();
        Assert.assertNotNull(message);
        Assert.assertTrue(message.isEmpty());

        final BtTickerDto ticker = response.getResult();
        Assert.assertNotNull(ticker);
        Assert.assertNotNull(ticker.getAsk());
        Assert.assertNotNull(ticker.getBid());
        Assert.assertNotNull(ticker.getLast());
        System.out.println(ticker);
    }

    @Test
    @Ignore
    public void getDepositAddressTest() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String apikey = "ff82c6f42b494d5fa12d7fdfb2cb0634";
        String secret = "f04d0b581b4d424fb13c95f1d03f66a9";
        String nonce = String.valueOf(System.currentTimeMillis());
        String currency = "BTC";
        String url = "https://bittrex.com/api/v1.1/account/getdepositaddress?apikey=ff82c6f42b494d5fa12d7fdfb2cb0634&currency=BTC&nonce="+nonce;
        String apisign = HmacSha512.HMACSHA512(url, secret);
        Map<String, Object> response = api.getDepositAddress(apisign, apikey, currency, nonce);
        Assert.assertNotNull(response);
        System.out.println(response);
    }

    @Test
    @Ignore
    public void getBalance() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String apikey = "ff82c6f42b494d5fa12d7fdfb2cb0634";
        String secret = "f04d0b581b4d424fb13c95f1d03f66a9";
        String nonce = String.valueOf(System.currentTimeMillis());
        String currency = "USD";
        String apisign = "https://bittrex.com/api/v1.1/account/getbalance?apikey=ff82c6f42b494d5fa12d7fdfb2cb0634&currency=USD&nonce="+nonce;
        String hash = HmacSha512.HMACSHA512(apisign, secret);
        Map<String, Object> response = api.getBalance(hash, apikey, currency, nonce);
        Assert.assertNotNull(response);
    }

    @Test
    @Ignore
    public void withdrawTest() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, MalformedURLException {
        String apikey = "ff82c6f42b494d5fa12d7fdfb2cb0634";
        String secret = "f04d0b581b4d424fb13c95f1d03f66a9";
        String nonce = "123456";
        String currency = "BTC";
        int quantity = 100000;
        String address = "1ARpsabbU9tABWkeVTxxpTHjLvD5vr9EAT";

        String uri = "https://bittrex.com/api/v1.1/account/withdraw?apikey=ff82c6f42b494d5fa12d7fdfb2cb0634&currency=BTC&quantity="+quantity+"&address="+address+"&nonce="+nonce;
        URL url = new URL(uri);
        System.out.println("url path = " + uri);
        String apisign = HmacSha512.HMACSHA512(uri, secret);
        System.out.println(apisign);

        Map<String, Object> response = api.withdraw(apisign, apikey, currency, quantity, address, nonce);
        Assert.assertNotNull(response);
    }

    @Test
    public void buyLimitTest() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, MalformedURLException {
        String apikey = "ff82c6f42b494d5fa12d7fdfb2cb0634";
        String secret = "f04d0b581b4d424fb13c95f1d03f66a9";
        String nonce = "123456";
        String currency = "BTC";
        double quantity = 100000.0;
        double rate = 1.0;
        String address = "1ARpsabbU9tABWkeVTxxpTHjLvD5vr9EAT";
        String market = "BTC-LTC";
        String uri = "https://bittrex.com/api/v1.1/market/buylimit?apikey="+apikey+"&market="+market+"&quantity="+quantity+"&rate="+rate+"&nonce="+nonce;
        URL url = new URL(uri);
        System.out.println("url path = " + uri);
        String apisign = HmacSha512.HMACSHA512(uri, secret);
        System.out.println(apisign);

        Map<String, Object> response = api.buyLimit(apisign, apikey, market, quantity, rate, nonce);
        Assert.assertNotNull(response);
    }

    @Test
    public void sellLimitTest() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, MalformedURLException {
        String apikey = "ff82c6f42b494d5fa12d7fdfb2cb0634";
        String secret = "f04d0b581b4d424fb13c95f1d03f66a9";
        String nonce = "123456";
        String currency = "BTC";
        double quantity = 100000.0;
        double rate = 1.0;
        String address = "1ARpsabbU9tABWkeVTxxpTHjLvD5vr9EAT";
        String market = "BTC-LTC";
        String uri = "https://bittrex.com/api/v1.1/market/selllimit?apikey="+apikey+"&market="+market+"&quantity="+quantity+"&rate="+rate+"&nonce="+nonce;
        URL url = new URL(uri);
        System.out.println("url path = " + uri);
        String apisign = HmacSha512.HMACSHA512(uri, secret);
        System.out.println(apisign);

        Map<String, Object> response = api.sellLimit(apisign, apikey, market, quantity, rate, nonce);
        Assert.assertNotNull(response);
    }
}
