package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.v2;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Class CoinmarketcapV2APITest tests method of interface ICoinmarketcapV2API.
 */
public class CoinmarketcapV2APITest {

    private static long currentUnix = System.currentTimeMillis();

    private static final long CACHE_EXPIRY_TIME_DEFAULT = 600;

    private static ICoinmarketcapV2API api;

    @BeforeClass
    public static  void setup() {
        api = RestProxyFactory.createProxy(ICoinmarketcapV2API.class, "https://api.coinmarketcap.com");
    }

    /**
     * Method timeTest() tests if enauf time has expired and the listings should be updated.
     *
     * @throws InterruptedException
     */
    @Test
    public void timeTest() throws InterruptedException {
        Assert.assertTrue(true);
        Thread.sleep(3000);
        long recentUnix = System.currentTimeMillis();
        long diff = recentUnix - currentUnix;
        long seconds = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS);

        final Map<String, Integer> coinIDs = new HashMap<String, Integer>();
        long cacheExpiryTime = 2; //2 seconds
        if(seconds > cacheExpiryTime) {
            final Map<String, Object> listings = api.getListings();
            if (listings != null && !listings.isEmpty()) {
                final List<Object> dataList = (List<Object>) listings.get("data");
                for (Object dataobject : dataList) {
                    Map<String, Object> map = (Map<String, Object>) dataobject;
                    final Integer id = (Integer) map.get("id");
                    final String symbol = (String) map.get("symbol");
                    if (!coinIDs.containsKey(symbol) && !coinIDs.containsValue(id)) {
                        coinIDs.put(symbol, id);
                    }
                }
            }
        }

        Assert.assertFalse(coinIDs.isEmpty());
    }

    /**
     * Method timeNotExpiredTest() tests if enauf time has expired and the listings should be updated.
     * In this case not enauf time will be provided
     *
     * @throws InterruptedException
     */
    @Test
    public void timeNotExpiredTest() throws InterruptedException {
        Assert.assertTrue(true);
        Thread.sleep(1000);
        long recentUnix = System.currentTimeMillis();
        long diff = recentUnix - currentUnix;
        long seconds = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS);

        final Map<String, Integer> coinIDs = new HashMap<String, Integer>();
        long cacheExpiryTime = 2; //2 seconds
        if(seconds > CACHE_EXPIRY_TIME_DEFAULT) {
            final Map<String, Object> listings = api.getListings();
            if (listings != null && !listings.isEmpty()) {
                final List<Object> dataList = (List<Object>) listings.get("data");
                for (Object dataobject : dataList) {
                    Map<String, Object> map = (Map<String, Object>) dataobject;
                    final Integer id = (Integer) map.get("id");
                    final String symbol = (String) map.get("symbol");
                    if (!coinIDs.containsKey(symbol) && !coinIDs.containsValue(id)) {
                        coinIDs.put(symbol, id);
                    }
                }
            }
        }

        Assert.assertTrue(coinIDs.isEmpty());
    }

    /**
     * Method timeNotExpiredButCoinIdIsEmptyTest() tests if enauf time has expired and the listings should be updated.
     * In this case not enauf time will be provided
     * @throws InterruptedException
     */
    @Test
    public void timeNotExpiredButCoinIdIsEmptyTest() throws InterruptedException {
        Assert.assertTrue(true);
        Thread.sleep(1000);
        long recentUnix = System.currentTimeMillis();
        long diff = recentUnix - currentUnix;
        long seconds = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS);

        final Map<String, Integer> coinIDs = new HashMap<String, Integer>();
        if(coinIDs.isEmpty() || seconds > CACHE_EXPIRY_TIME_DEFAULT) {
            final Map<String, Object> listings = api.getListings();
            if (listings != null && !listings.isEmpty()) {
                final List<Object> dataList = (List<Object>) listings.get("data");
                for (Object dataobject : dataList) {
                    Map<String, Object> map = (Map<String, Object>) dataobject;
                    final Integer id = (Integer) map.get("id");
                    final String symbol = (String) map.get("symbol");
                    if (!coinIDs.containsKey(symbol) && !coinIDs.containsValue(id)) {
                        coinIDs.put(symbol, id);
                    }
                }
            }
        }

        Assert.assertFalse(coinIDs.isEmpty());
    }

    /**
     * Method getGlobalTest() tests if method getGlobal() of the api has been correctly called.
     */
    @Test
    public void getGlobalTest() {
        final Map<String, Object> result = api.getGlobal();
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.get("data"));
        Assert.assertTrue(result.get("data") instanceof Map);

        final Map<String, Integer> dataMap = (Map<String, Integer>) result.get("data");
        Assert.assertFalse(dataMap.isEmpty());
        Assert.assertEquals(5, dataMap.size());
        Assert.assertNotNull(dataMap.get("active_cryptocurrencies"));

        final Integer activeCrypto = dataMap.get("active_cryptocurrencies");
        Assert.assertTrue(activeCrypto > 1000);
    }

    /**
     * Method getAllTickersTest() checks if method getAllTickers() of the api is called correctly and
     * that data is accepted accordingly.
     */
    @Test
    public void getAllTickersTest() {
        final Map<String, Object> result = api.getAllTickers();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());

        final Object object = result.get("data");
        Assert.assertTrue(object instanceof Map);

        final Map<String, Map<String, Object>> dataMap = (Map<String, Map<String, Object>>) object;
        Assert.assertFalse(dataMap.isEmpty());

        final Map<String, Object> btcMap = dataMap.get("1");
        Assert.assertFalse(btcMap.isEmpty());

        final Integer id = (Integer)btcMap.get("id");
        final String symbol = (String)btcMap.get("symbol");

        Assert.assertNotNull(id);
        Assert.assertNotNull(symbol);
        Assert.assertEquals(1, id.intValue());
        Assert.assertEquals("BTC", symbol);
    }

    /**
     * Method getTickerOneStringParameterTest() checks if method getTicker() of the api, with one parameter - id
     * of type string is correctly called and that data is received.
     */
    @Test
    public void getTickerOneStringParameterTest() {
        final Map<String, Map<String, Object>> result = api.getTicker("1");
        Assert.assertNotNull(result);

        final Map<String, Object> data = result.get("data");
        Assert.assertNotNull(data);

        final Integer id = (Integer) data.get("id");
        Assert.assertNotNull(id);
        Assert.assertEquals(1, id.intValue());

        final Map<String, Object> metadata = result.get("metadata");
        Assert.assertNotNull(metadata);

        final Integer timestamp = (Integer) metadata.get("timestamp");
        Assert.assertNotNull(timestamp);
    }

    /**
     * Method getTickerOneStringParameterTest() checks if method getTicker() of the api, with one parameter - id
     * of type string is correctly called and that data is received.
     */
    @Test
    public void getTickerTwoStringParametersTest() {
        final Map<String, Map<String, Object>> result = api.getTicker("1", "EUR");
        Assert.assertNotNull(result);

        final Map<String, Object> data = result.get("data");
        Assert.assertNotNull(data);

        final Integer id = (Integer) data.get("id");
        Assert.assertNotNull(id);
        Assert.assertEquals(1, id.intValue());

        final Map<String, Object> metadata = result.get("metadata");
        Assert.assertNotNull(metadata);

        final Integer timestamp = (Integer) metadata.get("timestamp");
        Assert.assertNotNull(timestamp);
    }

    /**
     * Method getTickerOneIntegerParameterTest() checks if method getTicker() of the api, with one parameter - id
     * of type string is correctly called and that data is received.
     */
    @Test
    public void getTickerOneIntegerParameterTest() {
        final Map<String, Map<String, Object>> result = api.getTicker(1);
        Assert.assertNotNull(result);

        final Map<String, Object> data = result.get("data");
        Assert.assertNotNull(data);

        final Integer id = (Integer) data.get("id");
        Assert.assertNotNull(id);
        Assert.assertEquals(1, id.intValue());

        final Map<String, Object> metadata = result.get("metadata");
        Assert.assertNotNull(metadata);

        final Integer timestamp = (Integer) metadata.get("timestamp");
        Assert.assertNotNull(timestamp);
    }

    /**
     * Method getTickerOneIntegerOneStringParametersTest() checks if method getTicker() of the api, with one parameter - id
     * of type string is correctly called and that data is received.
     */
    @Test
    public void getTickerOneIntegerOneStringParametersTest() {
        final Map<String, Map<String, Object>> result = api.getTicker(1, "EUR");
        Assert.assertNotNull(result);

        final Map<String, Object> data = result.get("data");
        Assert.assertNotNull(data);

        final Integer id = (Integer) data.get("id");
        Assert.assertNotNull(id);
        Assert.assertEquals(1, id.intValue());

        final Map<String, Object> metadata = result.get("metadata");
        Assert.assertNotNull(metadata);

        final Integer timestamp = (Integer) metadata.get("timestamp");
        Assert.assertNotNull(timestamp);
    }

    /**
     * Method getTickerTest() checks if method getTicker() of the api, with two parameters - id of type string,
     * and fiat of type String is correctly called and that data and quotes has been received.
     */
    @Test
    public void getQuotesTest() {
        String fiat = "EUR";

        final Map<String, Map<String, Object>> result = api.getTicker("1", fiat);
        Assert.assertNotNull(result);

        final Map<String, Object> data = result.get("data");
        Assert.assertNotNull(data);

        final Map<String, Object> quotes = (Map<String, Object>) data.get("quotes");
        Assert.assertNotNull(quotes);

        final Map<String, Object> quote = (Map<String, Object>) quotes.get(fiat);
        Assert.assertNotNull(quote);

        final double price = (double) quote.get("price");
        Assert.assertTrue(price != 0);

        final BigDecimal priceBD = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Assert.assertNotNull(priceBD);
        Assert.assertTrue(priceBD.doubleValue() != 0);
    }

    /**
     * Method getListingsTest() calls method getListings() from the api and checks if correct data -
     * listings for all crypto currencies has been received.
     */
    @Test
    public void getListingsTest() {
        final Map<String, Object> result = api.getListings();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());

        final Object object = result.get("data");
        Assert.assertTrue(object instanceof List);

        final List<Object> dataList = (List<Object>)object;
        Assert.assertNotNull(dataList);
        Assert.assertFalse(dataList.isEmpty());
        Assert.assertTrue(dataList.size() > 100);

        final Object dataobject = dataList.get(0);
        Assert.assertNotNull(object);
        Assert.assertTrue(dataobject instanceof Map);

        final Map<String, Object> map = (Map<String, Object>)dataobject;
        Assert.assertNotNull(map);

        final Integer id = (Integer) map.get("id");
        Assert.assertNotNull(id);
        Assert.assertEquals(1, id.intValue());

        final String symbol = (String) map.get("symbol");
        Assert.assertNotNull(symbol);
        Assert.assertEquals("BTC", symbol);

        final String name = (String) map.get("name");
        Assert.assertNotNull(name);
        Assert.assertEquals("Bitcoin", name);


        final Object metadataObject = result.get("metadata");
        Assert.assertTrue(metadataObject instanceof Map);

        final Map<String, Object> metadata = (Map<String, Object>)metadataObject;
        Assert.assertNotNull(metadata);

        final Integer timestamp = (Integer) metadata.get("timestamp");
        Assert.assertNotNull(timestamp);
    }

    /**
     * Method createCoinIdsTest() tests creation of coinId, which are are received by calling method getListings()
     * of the api. The listings contain id for each coin, and this ids are used to create map of crypto currency symbol
     * and its respective id.
     */
    @Test
    public void createCoinIdsTest() {
        final Map<String, Integer> coinIDs = new HashMap<String, Integer>();
        final Map<String, Object> listings = api.getListings();
        final List<Object> dataList = (List<Object>) listings.get("data");
        Assert.assertNotNull(dataList);
        Assert.assertFalse(dataList.isEmpty());
        Assert.assertTrue(dataList.size() > 100);

        for(Object dataobject : dataList) {
            Map<String, Object> map = (Map<String, Object>)dataobject;
            final Integer id = (Integer) map.get("id");
            final String symbol = (String) map.get("symbol");
            if(!coinIDs.containsKey(symbol) && !coinIDs.containsValue(id)) {
                coinIDs.put(symbol, id);
            }
        }

        Assert.assertFalse(coinIDs.isEmpty());
    }

    /**
     * Method getCurrencyIdTest() retrieves currency id for particular crypto currency symbol.
     * The id is taken by fetching method getListings() of the api.
     */
    @Test
    public void getCurrencyIdTest() {
        String currency = "ETH";

        final Map<String, Integer> coinIDs = new HashMap<String, Integer>();
        final Map<String, Object> listings = api.getListings();
        final List<Object> dataList = (List<Object>) listings.get("data");
        Assert.assertNotNull(dataList);
        Assert.assertFalse(dataList.isEmpty());
        Assert.assertTrue(dataList.size() > 100);

        for(Object dataobject : dataList) {
            Map<String, Object> map = (Map<String, Object>)dataobject;
            final Integer id = (Integer) map.get("id");
            final String symbol = (String) map.get("symbol");
            if(!coinIDs.containsKey(symbol) && !coinIDs.containsValue(id)) {
                coinIDs.put(symbol, id);
            }
        }

        Assert.assertFalse(coinIDs.isEmpty());

        final Integer id = coinIDs.get(currency);
        System.out.println("ETH id = " + id.toString());
        Assert.assertNotNull(id);
        Assert.assertEquals(1027, id.intValue());
    }

    /**
     * Method getQuoteByCurrencySymbolAndFiatCurrencyTest() checks the quotes for particular crypto currency
     * and for particular fiat currency and makes sure that data is provided.
     */
    @Test
    public void getQuoteByCurrencySymbolAndFiatCurrencyTest() {
        String currency = "ETH";
        String fiat = "EUR";

        final Map<String, Integer> coinIDs = new HashMap<String, Integer>();
        final Map<String, Object> listings = api.getListings();
        final List<Object> dataList = (List<Object>) listings.get("data");
        Assert.assertNotNull(dataList);
        Assert.assertFalse(dataList.isEmpty());
        Assert.assertTrue(dataList.size() > 100);

        for(Object dataobject : dataList) {
            Map<String, Object> map = (Map<String, Object>)dataobject;
            final Integer id = (Integer) map.get("id");
            final String symbol = (String) map.get("symbol");
            if(!coinIDs.containsKey(symbol) && !coinIDs.containsValue(id)) {
                coinIDs.put(symbol, id);
            }
        }

        Assert.assertFalse(coinIDs.isEmpty());

        final Integer id = coinIDs.get(currency);
        Assert.assertNotNull(id);

        final Map<String, Map<String, Object>> result = api.getTicker(id, fiat);
        Assert.assertNotNull(result);

        final Map<String, Object> data = result.get("data");
        Assert.assertNotNull(data);

        final Map<String, Object> quotes = (Map<String, Object>) data.get("quotes");
        Assert.assertNotNull(quotes);
        Assert.assertEquals(2, quotes.size());

        final Map<String, Object> quote = (Map<String, Object>) quotes.get(fiat);
        Assert.assertNotNull(quote);

        final double price = (double) quote.get("price");
        Assert.assertTrue(price != 0);

        final BigDecimal priceBD = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Assert.assertNotNull(priceBD);
        Assert.assertTrue(priceBD.doubleValue() != 0);

        System.out.println("Price of " + currency + " = " + priceBD.doubleValue() + " " + fiat);
    }
}
