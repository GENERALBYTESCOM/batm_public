package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;

import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CmcTickerData;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CmcTickerQuote;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CmcTickerResponse;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.ICoinmarketcapAPI;
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
public class CoinmarketcapAPITest {

    private static long currentUnix = System.currentTimeMillis();

    private static final long CACHE_EXPIRY_TIME_DEFAULT = 600;

    private static ICoinmarketcapAPI api;

    @BeforeClass
    public static  void setup() {
        api = RestProxyFactory.createProxy(ICoinmarketcapAPI.class, "https://api.coinmarketcap.com");
    }

    /**
     * Method timeTest() tests if enauf time has expired and the listings should be updated.
     *
     * @throws InterruptedException
     */
    @Test
    public void timeTest() throws InterruptedException {
        Assert.assertTrue(true);
        long recentUnix = System.currentTimeMillis();
        long diff = recentUnix - currentUnix;
        long seconds = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS) + 3;

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
        long recentUnix = System.currentTimeMillis();
        long diff = recentUnix - currentUnix;
        long seconds = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS) + 1;

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
        long recentUnix = System.currentTimeMillis();
        long diff = recentUnix - currentUnix;
        long seconds = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS) + 1;

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
     * Method getTickerOneIntegerOneStringParametersTest() checks if method getTicker() of the api, with one parameter - id
     * of type string is correctly called and that data is received.
     */
    @Test
    public void getTickerOneIntegerOneStringParametersTest() {
        CmcTickerResponse result = api.getTicker(1, "USD");
        Assert.assertNotNull(result);

        CmcTickerData data = result.getData();
        Assert.assertNotNull(data);
    }

    @Test
    public void getTickerDogecoinTest() {
        Integer dogeCoinId = 74;//coinmarketcap id
        CmcTickerResponse result = api.getTicker(dogeCoinId, "USD");
        Assert.assertNotNull(result);

        CmcTickerData data = result.getData();
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getQuotes());

        //CmcTickerQuote quote = (CmcTickerQuote) data.getQuotes();
        System.out.println(data);
        System.out.println();
    }

    /**
     * Method getTickerOneIntegerOneStringParametersTest() checks if method getTicker() of the api, with one parameter - id
     * of type string is correctly called and that data is received.
     */
    @Test
    public void getTickerOneIntegerOneNullParametersTest() {
        CmcTickerResponse result = api.getTicker(1, null);
        Assert.assertNotNull(result);

        CmcTickerData data = result.getData();
        Assert.assertNotNull(data);
    }

    /**
     * Method getTickerTest() checks if method getTicker() of the api, with two parameters - id of type string,
     * and fiat of type String is correctly called and that data and quotes has been received.
     */
    @Test
    public void getQuotesTest() {
        CmcTickerResponse result = api.getTicker(1, "EUR");
        Assert.assertNotNull(result);

        CmcTickerData data = result.getData();
        Assert.assertNotNull(data);

        final Map<String, CmcTickerQuote> quotes = data.getQuotes();
        Assert.assertNotNull(quotes);

        CmcTickerQuote quote = quotes.get("EUR");
        Assert.assertNotNull(quote);

        BigDecimal price = quote.getPrice();
        Assert.assertNotNull(price);
        Assert.assertTrue(price.doubleValue() != 0);
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

        CmcTickerResponse result = api.getTicker(1, fiat);
        Assert.assertNotNull(result);

        CmcTickerData data = result.getData();
        Assert.assertNotNull(data);

        final Map<String, CmcTickerQuote> quotes = data.getQuotes();
        Assert.assertNotNull(quotes);

        CmcTickerQuote quote = quotes.get("EUR");
        Assert.assertNotNull(quote);

        BigDecimal price = quote.getPrice();
        Assert.assertNotNull(price);
        Assert.assertTrue(price.doubleValue() != 0);
    }
}
