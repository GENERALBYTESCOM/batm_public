package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.ITask;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.dto.entities.*;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.dto.requests.*;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.dto.responses.*;

import com.generalbytes.batm.server.extensions.util.net.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class CoinZixExchange implements IRateSourceAdvanced, IExchangeAdvanced {

    private static final Logger log = LoggerFactory.getLogger("batm.master.CoinZixExchange");

    public static AskComparator askComparator = new AskComparator();
    public static BidComparator bidComparator = new BidComparator();

    private static final String baseUrl = "https://api.coinzix.com";
    private static final BigDecimal DECIMAL_CONST = BigDecimal.valueOf(Math.pow(10, 8));
    private static final int PRECISION = 8;

    private final RequestService api;

    private String token;
    private String secretKey;
    private String preferedFiatCurrency = FiatCurrency.EUR.getCode();
    private final ObjectMapper mapper;

    private static final Set<String> FIAT_CURRENCIES = new HashSet<>();
    private static final Set<String> CRYPTO_CURRENCIES = new HashSet<>();

    static {
        initConstants();
    }

    private static void initConstants() {
        FIAT_CURRENCIES.add(FiatCurrency.EUR.getCode());

        CRYPTO_CURRENCIES.add(CryptoCurrency.BTC.getCode());
        CRYPTO_CURRENCIES.add(CryptoCurrency.ETH.getCode());
    }

    public CoinZixExchange() {
        api = RestProxyFactory.createProxy(RequestService.class, baseUrl);
        mapper = new ObjectMapper();
    }

    public CoinZixExchange(String token, String secret) {
        this();
        this.token = token;
        this.secretKey = secret;
    }


    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        log.debug("createPurchaseCoinsTask");
        String pair = getCurrencyPair(cryptoCurrency, fiatCurrencyToUse);
        if (pair != null) {
            return new TradeCoinsTask(pair, amount, Constants.SIDE.BUY);
        }
        log.error("createPurchaseCoinsTask");
        return null;
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        log.debug("createSellCoinsTask");
        String pair = getCurrencyPair(cryptoCurrency, fiatCurrencyToUse);
        if (pair != null) {
            return new TradeCoinsTask(pair, amount, Constants.SIDE.SELL);
        }
        log.error("createSellCoinsTask");
        return null;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        try {
            log.debug("getCryptoBalance");
            cryptoCurrency = checkCryptoCurrency(cryptoCurrency);
            if (cryptoCurrency != null) {
                return getBalance(cryptoCurrency);
            }
        } catch (Throwable e) {
            log.error("getCryptoBalance(2)", e);
        }
        return null;
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        try {
            log.debug("getFiatBalance");
            fiatCurrency = checkFiatCurrency(fiatCurrency);
            if (fiatCurrency != null) {
                return getBalance(fiatCurrency);
            }
        } catch (Throwable e) {
            log.error("getFiatBalance(2)", e);
        }
        return null;
    }

    @Deprecated
    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        log.error("purchaseCoins - Deprecated. Use method IExchangeAdvanced.createPurchaseCoinsTask.");
        return null;
    }

    @Deprecated
    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        log.error("sellCoins - Deprecated. Use method IExchangeAdvanced.createSellCoinsTask.");
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            cryptoCurrency = checkCryptoCurrency(cryptoCurrency);
            if (cryptoCurrency != null) {
                WithdrawRequest request = new WithdrawRequest();
                request.iso = cryptoCurrency;
                request.amount = amount.toPlainString();
                request.to_address = destinationAddress;

                String sign = createSign(request);

                RateLimiter.waitForPossibleCall(getClass());
                WithdrawResponse response = api.withdraw(token, sign, request);

                if (response.data != null){
                    return response.data.id;
                }
            }
        }catch (Throwable e) {
            log.error("sendCoins", e);
        }
        return null;
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        try {
            cryptoCurrency = checkCryptoCurrency(cryptoCurrency);
            if (cryptoCurrency != null) {
                GetDepositAddressRequest request = new GetDepositAddressRequest();
                request.iso = cryptoCurrency;
                String sign = createSign(request);
                RateLimiter.waitForPossibleCall(getClass());
                GetDepositAddressResponse response = api.getDepositAddress(token, sign, request);
                if (response.data != null) {
                    return response.data.address;
                }
            }
        }catch (Throwable e) {
            log.error("getDepositAddress", e);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        BigDecimal result = calculateBuyPrice(cryptoCurrency, fiatCurrency, getMeasureCryptoAmount(cryptoCurrency));
        if (result != null) {
            return result.divide(getMeasureCryptoAmount(cryptoCurrency), 2, BigDecimal.ROUND_UP);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        BigDecimal result = calculateSellPrice(cryptoCurrency, fiatCurrency, getMeasureCryptoAmount(cryptoCurrency));
        if (result != null) {
            return result.divide(getMeasureCryptoAmount(cryptoCurrency), 2, BigDecimal.ROUND_DOWN);
        }
        return null;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        try {
            String pair = getCurrencyPair(cryptoCurrency, fiatCurrency);
            if (pair != null) {
                RateLimiter.waitForPossibleCall(getClass());
                OrderBookResponse response = api.getOrderBook(pair);
                if (response.data != null && response.data.sell != null) {
                    List<OrderBookPrice> asks = Arrays.asList(response.data.sell);
                    Collections.sort(asks, askComparator);
                    BigDecimal tradableLimit = getTradablePrice(cryptoAmount, asks);
                    if (tradableLimit != null) {
                        log.debug("Called Coinzix exchange for BUY rate: {}{} = {}", cryptoCurrency, fiatCurrency, tradableLimit);
                        return tradableLimit.multiply(cryptoAmount);
                    }
                }
            }
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        try {
            String pair = getCurrencyPair(cryptoCurrency, fiatCurrency);
            if (pair != null) {
                RateLimiter.waitForPossibleCall(getClass());
                OrderBookResponse response = api.getOrderBook(pair);
                if (response.data != null && response.data.buy != null) {
                    List<OrderBookPrice> bids = Arrays.asList(response.data.buy);
                    Collections.sort(bids, bidComparator);
                    BigDecimal tradableLimit = getTradablePrice(cryptoAmount, bids);
                    if (tradableLimit != null) {
                        log.debug("Called Coinzix exchange for BUY rate: {}{} = {}", cryptoCurrency, fiatCurrency, tradableLimit);
                        return tradableLimit.multiply(cryptoAmount);
                    }
                }
            }
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return null;
    }

    private BigDecimal getMeasureCryptoAmount(String cryptoCurrency) {
        if (CryptoCurrency.BTC.getCode().equals(cryptoCurrency)) {
            return XChangeExchange.BTC_RATE_SOURCE_CRYPTO_AMOUNT;
        }
        return new BigDecimal(5);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return CRYPTO_CURRENCIES;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return FIAT_CURRENCIES;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        try {
            String pair = getCurrencyPair(cryptoCurrency, fiatCurrency);
            if (pair != null) {
                RateLimiter.waitForPossibleCall(getClass());
                TickerResponse response = api.getTicker(pair);
                if (response.data != null){
                    return new BigDecimal(response.data.last);
                }
            }
        }catch (Throwable e) {
            log.error("getExchangeRateLast", e);
        }
        return null;
    }

    @Override
    public String getPreferredFiatCurrency() {
        if (preferedFiatCurrency != null && !preferedFiatCurrency.isEmpty()) {
            return preferedFiatCurrency;
        }
        return FiatCurrency.EUR.getCode();
    }


    private String checkCryptoCurrency(String cryptoCurrency) {
        if (cryptoCurrency == null || !CRYPTO_CURRENCIES.contains(cryptoCurrency)) {
            log.error("checkCryptoCurrency - Crypto currency \"" + cryptoCurrency + "\" is not supported. CoinZix supports " + Arrays.toString(CRYPTO_CURRENCIES.toArray()) + ".");
            return null;
        }
        return cryptoCurrency.toUpperCase();
    }

    private BigDecimal getTradablePrice(BigDecimal cryptoAmount, List<OrderBookPrice> bidsOrAsksSorted) throws IOException {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderBookPrice order : bidsOrAsksSorted) {
            total = total.add(new BigDecimal(Double.toString(order.volume)));
            if (cryptoAmount.compareTo(total) <= 0) {
                log.debug("tradablePrice: {}", order.rate);
                return new BigDecimal(Double.toString(order.rate));
            }
        }
        throw new IOException("tradable price not available");
    }

    private String checkFiatCurrency(String fiatCurrency) {
        Set<String> fiatCurrencies = getFiatCurrencies();
        if (fiatCurrency == null || (fiatCurrencies != null && !fiatCurrencies.contains(fiatCurrency))) {
            String supports = (fiatCurrencies != null) ? Arrays.toString(fiatCurrencies.toArray()) : "N/A";
            log.error("checkFiatCurrency - Fiat currency \"" + fiatCurrency + "\" is not supported. CoinZix supports " + supports + ".");
            return null;
        }
        return fiatCurrency.toUpperCase();
    }

    private BigDecimal getBalance(String currency) throws IOException, TimeoutException {
        BasicRequest request = new BasicRequest();
        String sign = createSign(request);
        RateLimiter.waitForPossibleCall(getClass());
        BalancesResponse balancesResponse = api.getBalances(this.token, sign, request);
        if (balancesResponse.data != null && balancesResponse.data.list != null){
            for (Balance b: balancesResponse.data.list){
                if (b.currency.iso3.equals(currency)){
                    return BigDecimal.valueOf(b.balance_available).divide(DECIMAL_CONST, PRECISION, RoundingMode.DOWN);
                }
            }
        }
        return null;
    }

    private String getCurrencyPair(String cryptoCurrency, String fiatCurrency) {
        cryptoCurrency = checkCryptoCurrency(cryptoCurrency);
        fiatCurrency = checkFiatCurrency(fiatCurrency);
        if (cryptoCurrency != null && fiatCurrency != null) {
            return cryptoCurrency.toUpperCase() + fiatCurrency.toUpperCase();
        }
        return null;
    }

    public static String getSortedFieldsString(JsonNode node){
        String string = "";

        List<String> list = new LinkedList<String>();
        Iterator<String> it = node.fieldNames();
        while (it.hasNext()) {
            list.add(it.next());
        }
        Collections.sort(list);

        for (String f: list){
            JsonNode subNode = node.get(f);
            string += subNode.isObject() ? getSortedFieldsString(subNode) : subNode.asText();
        }

        return string;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String createSign(BasicRequest request){
        JsonNode node = mapper.convertValue(request, JsonNode.class);
        String str = getSortedFieldsString(node);
        String signed;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest((str+secretKey).getBytes(StandardCharsets.UTF_8));
            signed = bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
        return signed;
    }

    class TradeCoinsTask implements ITask {

        private final long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; // 5 hours
        private long createTime;

        private BigDecimal amount;
        private String orderId;
        private String result;
        private boolean finished;
        private final int side;
        private final String logString;
        private final String pair;

        TradeCoinsTask(String pair, BigDecimal amount, int side) {
            this.amount = amount;
            this.side = side;
            this.pair = pair;
            this.logString = side == Constants.SIDE.BUY ? "PurchaseCoinsTask" : "SellCoinsTask";
        }

        @Override
        public boolean onCreate() {
            try {
                NewOrderRequest orderRequest = new NewOrderRequest();
                orderRequest.pair = pair;
                orderRequest.rate = "0";
                orderRequest.type = side;
                orderRequest.type_trade = Constants.ORDER_TYPE.MARKET;
                orderRequest.volume = amount.toPlainString();
                String sign = createSign(orderRequest);
                RateLimiter.waitForPossibleCall(getClass());
                NewOrderResponse orderResponse = api.newOrder(token, sign, orderRequest);

                if (orderResponse.data != null) {
                    orderId = Long.toString(orderResponse.data.id);
                    createTime = System.currentTimeMillis();
                    log.debug(logString+".onCreate - " + ("orderAId = " + orderId));
                }
            } catch (Exception e) {
                log.error(logString+".onCreate", e);
            }
            return (orderId != null);
        }

        @Override
        public boolean onDoStep(){
            if (orderId == null) {
                log.debug(logString+".onDoStep - Giving up on waiting for trade to complete. Because it did not happen.");
                finished = true;
                result = "Skipped";
                return false;
            }

            long checkTillTime = createTime + MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH;
            if (System.currentTimeMillis() > checkTillTime) {
                log.debug(logString+".onDoStep - " + ("Giving up on waiting for trade " + orderId + " to complete."));
                finished = true;
                return false;
            }


            try {
                GetOrderRequest request = new GetOrderRequest();
                request.order_id = orderId;
                String sign = createSign(request);

                RateLimiter.waitForPossibleCall(getClass());
                GetOrderResponse response = api.getOrder(token, sign, request);
                if (response.data != null && response.data.status == Constants.STATUS.DONE) {
                    result = orderId;
                    finished = true;
                } else if (response.data != null && response.data.status == Constants.STATUS.CLOSED) {
                    log.debug(logString+".onDoStep - trade is not fully filled.");
                    finished = true;
                    result = "Skipped";
                    return false;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            return result != null;
        }

        @Override
        public void onFinish() {
            log.debug(logString+".onFinish - Trade task finished.");
        }

        @Override
        public boolean isFinished() {
            return finished;
        }

        @Override
        public Object getResult() {
            return result;
        }

        @Override
        public boolean isFailed() {
            return finished && result == null;
        }

        @Override
        public long getShortestTimeForNexStepInvocation() {
            return 5 * 1000;
        }
    }


}
