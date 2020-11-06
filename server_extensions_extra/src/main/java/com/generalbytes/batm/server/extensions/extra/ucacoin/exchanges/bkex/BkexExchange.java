package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex;

import static java.lang.String.format;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.ws.rs.HeaderParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.ITask;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.Balance;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.Balances;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.CreateOrder;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.CreatedOrder;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.DepositAddress;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.DepositAddressResponse;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.MarketTick;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.OrderBookSnapshot;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.OrderState;
import com.generalbytes.batm.server.extensions.util.net.CompatSSLSocketFactory;

import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.RateLimiter;

public class BkexExchange implements IExchangeAdvanced, IRateSourceAdvanced {

    private static final Logger LOG = LoggerFactory.getLogger("batm.master.bkexExchange");

    private static final URI LIVE_URL = URI.create("https://api.bkex.io");

    public static IRateSourceAdvanced asRateSource(String preferredFiatCurrency) {
        return new BkexExchange(LIVE_URL, null, null, preferredFiatCurrency);
    }

    public static IExchangeAdvanced asExchange(String apikey, String secret, String preferredFiatCurrency) {
        Objects.requireNonNull(apikey, "apikey");
        Objects.requireNonNull(apikey, "secret");

        return new BkexExchange(LIVE_URL, apikey, secret, preferredFiatCurrency);
    }

    enum TradableLimitFor {
        BUY, SELL
    }

    // all CRYPTO_FIAT combinations are supported
    private static final ImmutableSet<String> FIAT_CURRENCIES = ImmutableSet.of(CryptoCurrency.USDT.getCode());
    private static final ImmutableSet<String> CRYPTO_CURRENCIES = ImmutableSet.of(CryptoCurrency.UCA.getCode(),
            CryptoCurrency.BTC.getCode());

    private final IBkexAPI api;
    // allow 2 requests per second -> 120 requests/minute, safely below api rate
    // limits
    private final RateLimiter rateLimiter = RateLimiter.create(2);

    private final Cache<String, BigDecimal> rateCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    private final String preferredFiatCurrency;

    private String secret;

    // visible for testing
    BkexExchange(URI baseUri, String apikey, String secret, String preferredFiatCurrency) {
        this.preferredFiatCurrency = Objects.requireNonNull(preferredFiatCurrency, "preferred fiat currency");
        LOG.info(this.preferredFiatCurrency);
        LOG.info(FIAT_CURRENCIES.toString());

        

        if (!FIAT_CURRENCIES.contains(preferredFiatCurrency)) {
            LOG.error("cannot set {} as preferred fiat currency (supports {})", preferredFiatCurrency, FIAT_CURRENCIES);
            throw new IllegalStateException("illegal preferred fiat currency");
        }

        try {
            final SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            final CompatSSLSocketFactory socketFactory = new CompatSSLSocketFactory(sslcontext.getSocketFactory());
            final ClientConfig config = new ClientConfig();
            config.setSslSocketFactory(socketFactory);
            config.setIgnoreHttpErrorCodes(false);
            config.addDefaultParam(HeaderParam.class, "df-client", "id=batm;device=atm");

            if (apikey != null) {
                config.addDefaultParam(HeaderParam.class, "X_ACCESS_KEY", apikey);
            }
            if (secret != null) {
                this.secret = secret;
            }
            api = RestProxyFactory.createProxy(IBkexAPI.class, baseUri.toASCIIString(), config,
                    this::throttleApiRequests);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOG.error("failed to create an api client proxy", e);
            throw new IllegalStateException("failed to create an api client proxy", e);
        }
    }

    private Object throttleApiRequests(InvocationHandler invocationHandler, Object proxy, Method method, Object[] args)
            throws Throwable {
        rateLimiter.acquire();
        return invocationHandler.invoke(proxy, method, args);
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
    public String getPreferredFiatCurrency() {
        return this.preferredFiatCurrency;
    }

    // region IRateSource

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        return safely(() -> {
            final String instrument = asSymbol(cryptoCurrency, fiatCurrency);
            return rateCache.get(instrument, () -> fetchExchangeRateLast(instrument));
        }, "getExchangeRateLast", cryptoCurrency, fiatCurrency);
    }

    private BigDecimal fetchExchangeRateLast(String symbol) {
        try {
            final MarketTick ticks = api.marketTickerForInstrument(symbol);
            return ticks.getTicks().get(0).getPrice();
        } catch (Exception e) {
            LOG.info("ERROR");

            LOG.info(e.getCause().toString());
        }
        return null;
    }

    // endregion

    // region IRateSourceAdvanced

    private static final BigDecimal LIQUIDITY_PROBE_VOLUME = BigDecimal.ONE;

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        BigDecimal result = calculateBuyPrice(cryptoCurrency, fiatCurrency, LIQUIDITY_PROBE_VOLUME);
        if (result != null) {
            return result.divide(LIQUIDITY_PROBE_VOLUME, 8, BigDecimal.ROUND_UP);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        BigDecimal result = calculateSellPrice(cryptoCurrency, fiatCurrency, LIQUIDITY_PROBE_VOLUME);
        if (result != null) {
            return result.divide(LIQUIDITY_PROBE_VOLUME, 8, BigDecimal.ROUND_DOWN);
        }
        return null;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return safely(() -> {
            final String instrument = asSymbol(cryptoCurrency, fiatCurrency);
            return calculateRequiredLimitPriceToBuy(instrument, cryptoAmount).multiply(cryptoAmount);
        }, "calculateBuyPrice", cryptoCurrency, fiatCurrency, cryptoAmount);
    }

    private BigDecimal calculateRequiredLimitPriceToBuy(String instrument, BigDecimal amount) {
        
        final OrderBookSnapshot snapshot = api.orderBook(instrument, 50).getSnapshot();
        final List<List<Float>> asks = snapshot.getAsks();
        return  calculateRequiredLimitPrice(amount, asks);
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return safely(() -> {
            final String instrument = asSymbol(cryptoCurrency, fiatCurrency);
            return calculateRequiredLimitPriceToSell(instrument, cryptoAmount).multiply(cryptoAmount);
        }, "calculateSellPrice", cryptoCurrency, fiatCurrency, cryptoAmount);
    }

    private BigDecimal calculateRequiredLimitPriceToSell(String instrument, BigDecimal amount) {

        final OrderBookSnapshot snapshot = api.orderBook(instrument, 50).getSnapshot();
        final List<List<Float>> bids = snapshot.getBids();
        //return null;
        return calculateRequiredLimitPrice(amount, bids);
    }

    /**
     * Determine the price to fully fill a given amount of base currency.
     *
     * @param amountToFill how much has to be traded
     * @param orderbook    sorted price levels of the target orderbook
     * @return required price to fully fill given amount with a limit order
     */
    private BigDecimal calculateRequiredLimitPrice(BigDecimal amountToFill, List<List<Float>> orderbook) {
        BigDecimal remaining = amountToFill;
        BigDecimal price = null;

        for (List<Float> entry : orderbook) {
            BigDecimal valueRemaining = new BigDecimal(Float.toString(entry.get(1)));
            remaining = remaining.subtract(valueRemaining);
            BigDecimal valuePrice = new BigDecimal(Float.toString(entry.get(0)));
            price = valuePrice;

            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
        }

        LOG.trace("calculated limit price={},amount={},price_levels={}", price, amountToFill, orderbook);

        if (remaining.compareTo(BigDecimal.ZERO) > 0 || price == null) {
            throw new IllegalStateException("insufficient liquidity");
        }

        return price;
    }

    // region IExchange
    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        return safely(() -> {
            assertValidCrypto(cryptoCurrency);
            return fetchBalance(cryptoCurrency);
        }, "getCryptoBalance", cryptoCurrency);
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        return safely(() -> {
            assertValidFiat(fiatCurrency);
            return fetchBalance(fiatCurrency);
        }, "getFiatBalance", fiatCurrency);
    }

    /**
     * Bitpanda Pro handles fiat and crypto balances in the same way.
     *
     * @param code of currency
     * @return available balance of given currency
     */
    private BigDecimal fetchBalance(String code) {
        final Balances wallet = api.balances(RequestSigner.createInstance(secret)).getWallet();

        
        if (wallet.getBalances() == null) {
            // account has no balances - implicitly empty
            return BigDecimal.ZERO;
        }

        for (Balance current : wallet.getBalances()) {
            if (code.equalsIgnoreCase(current.getCurrency())) {
                return current.getAvailable();
            }
        }

        return BigDecimal.ZERO; // a balance is created on first deposit/trade, until then it is empty
                                // implicitly
    }

    private String executeSync(CreateOrder payload) {

        try {

            final CreatedOrder order = api.createOrder(
                payload.getDirection(),
                payload.getPrice(), 
                payload.getSource(),
                payload.getSymbol(),
                payload.getType(),
                payload.getVolume(),
                payload, RequestSigner.createInstance(secret));

            LOG.info("limit order created: {}", order.getOrderId());
            return order.getOrderId();
        } catch (Exception e) {

            LOG.info("limit order ERROR: {}", e.toString());
            return e.toString();

            // TODO: handle exception
        }

        /*
         * wait(Duration.ofSeconds(2));
         * 
         * for (int checks = 0; checks < MAX_ORDER_FILL_CHECKS; checks++) { OrderState
         * current; try { String[] orderIds = new String[1]; orderIds[0] =
         * order.getOrderId(); current = api.getOrderStates(orderIds,
         * RequestSigner.createInstance(secret),
         * Long.toString(System.currentTimeMillis()/1000)).getOrderStates().get(0); }
         * catch (final IbkexAPI.ApiError e) {
         * LOG.debug("failed to get order status {}", e.toString()); current = null; }
         * 
         * if (current != null && 2 == current.getStatus()) {
         * LOG.debug("order filled {}", current.toString()); return order.getOrderId();
         * }
         * 
         * LOG.debug("waiting for order to be processed {}", current);
         * wait(Duration.ofSeconds(3)); }
         */

        // throw new IllegalStateException("executed order was not fully filled in
        // time");
    }

    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse,
            String description) {
        return safely(() -> {
            final CreateOrder request = limitOrder("BID", amount, cryptoCurrency, fiatCurrencyToUse);
            return executeSync(request).toString();
        }, "purchaseCoins", amount, cryptoCurrency, fiatCurrencyToUse, description);
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse,
            String description) {
        return safely(() -> {

            final CreateOrder request = limitOrder("ASK", cryptoAmount, cryptoCurrency, fiatCurrencyToUse);
            return executeSync(request).toString();
        }, "sellCoins", cryptoAmount, cryptoCurrency, fiatCurrencyToUse, description);
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        return null;
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        DepositAddressResponse depositAddresses = api.getDepositAddresses(cryptoCurrency,
                RequestSigner.createInstance(secret));
        LOG.info(depositAddresses.toString());
        List<DepositAddress> addresses = depositAddresses.getAddresses();
        if (addresses.size() > 0) {
            return depositAddresses.getAddresses().get(0).getAddress();
        } else {
            return null;
        }
    }

    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse,
            String description) {
        return safely(() -> new OrderExecutionTask(limitOrder("ASK", amount, cryptoCurrency, fiatCurrencyToUse)),
                "createPurchaseCoinsTask", amount, cryptoCurrency, fiatCurrencyToUse, description);
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse,
            String description) {
        return safely(() -> new OrderExecutionTask(limitOrder("BID", amount, cryptoCurrency, fiatCurrencyToUse)),
                "createSellCoinsTask", amount, cryptoCurrency, fiatCurrencyToUse, description);
    }

    private final class OrderExecutionTask implements ITask {

        private final CreateOrder request;
        // task state
        private Instant start;
        private boolean finished;
        private String result;
        private String orderId;

        public OrderExecutionTask(CreateOrder request) {
            this.request = request;
        }

        @Override
        public boolean onCreate() {
            return wrapped(() -> {
                LOG.info("creating limit order using {}", request);

                final Object order = api.createOrder(
                    request.getDirection(),
                    request.getPrice(), 
                    request.getSource(),
                    request.getSymbol(),
                    request.getType(),
                    request.getVolume(),
                    request,
                    RequestSigner.createInstance(secret));
                // this.orderId = order.getOrderId();
                LOG.info("limit order created: {}", order.toString());

                BkexExchange.wait(Duration.ofSeconds(2));

                start = Instant.now(); // track order execution timeout
                return true;
            }, "onCreate");
        }

        @Override public boolean onDoStep() {
            return wrapped(() -> {
                if (orderId == null) {
                    finished = true;
                    throw new IllegalStateException("skip task - order was not created");
                }

                final Duration elapsed = Duration.between(start, Instant.now());
                if (elapsed.compareTo(Duration.ofHours(5)) > 0) {
                    finished = true;
                    throw new IllegalStateException("executed order was not fully filled in time");
                }

                final OrderState current = api.getOrderState(orderId, RequestSigner.createInstance(secret)).getOrderState();
                if (2 == current.getStatus()) {
                    LOG.debug("order filled {}", current);
                    this.result = current.getId();
                    finished = true;
                    return true;
                }

                return result != null;
            }, "OnDoStep");
        }

        @Override
        public boolean isFinished() {
            return finished;
        }

        @Override
        public String getResult() {
            return result;
        }

        @Override
        public boolean isFailed() {
            return finished && result == null;
        }

        @Override
        public void onFinish() {
            LOG.debug("order execution finished -- {}", request);
        }

        @Override
        public long getShortestTimeForNexStepInvocation() {
            return 5 * 1000; //it doesn't make sense to run step sooner than after 5 seconds
        }

        private boolean wrapped(Callable<Boolean> action, String method) {
            final String label = this.getClass().getSimpleName() + "#" + method;
            try {
                final Boolean result = action.call();
                LOG.debug("{} -> {}", label, result);
                return result == null ? false : result;
            } catch (Exception e) {
                LOG.info(e.toString());
                LOG.error("failed to {} ({}) - {}", label, this, e.toString(), e);
                return false;
            }
        }

        @Override public String toString() {
            return "OrderExecutionTask{" +
                "request=" + request +
                ", start=" + start +
                ", finished=" + finished +
                ", orderId=" + orderId +
                ", result='" + result + '\'' +
                '}';
        }
    }

	 /**
     * Execute an action and yield its result or {@code null} if there is any error.
     *
     * @param action to be executed
     * @param <T> type of result
     * @return result or {@code null}
     */
    private <T> T safely(Callable<T> action, String method, Object... params) {
        final String label = method + Arrays.toString(params);
        try {
            final T result = action.call();
            LOG.debug("{} -> {}", label, result);
            return result;
        } catch (IllegalArgumentException e) {
            LOG.debug("cannot {} - illegal input {}", label, e.getMessage());
        } catch (IllegalStateException e) {
            LOG.info("cannot {} - {}", label, e.getMessage());
        } catch (Exception e) {
            LOG.error("failed to {} - {}", label, e.toString(), e);
        }
        return null;
	}
	
	private String asSymbol(String base, String quote) {
        if (quote == "USD") quote = "USDT";
        // all crypto/fiat pairs are supported
        return base + "_" + quote;
	}
	
	private void assertValidCrypto(String code) {
        if (CRYPTO_CURRENCIES.contains(code)) {
            return;
        }

        final String msg = format("%s is not a supported crypto currency (supported=%s)", code, CRYPTO_CURRENCIES);
        throw new IllegalArgumentException(msg);
    }

    private void assertValidFiat(String code) {
        if (FIAT_CURRENCIES.contains(code)) {
            return;
        }

        final String msg = format("%s is not a supported fiat currency (supported=%s)", code, FIAT_CURRENCIES);
        throw new IllegalArgumentException(msg);
    }

    private CreateOrder limitOrder(String side, BigDecimal amount, String base, String quote) {
        final String symbol = asSymbol(base, quote);
        LOG.info(symbol);
        final BigDecimal amountScaled = amount.setScale(8, RoundingMode.FLOOR);
        final BigDecimal price;
        if (side == "ASK") {
            price = calculateRequiredLimitPriceToSell(symbol, amountScaled);
        } else if (side == "BID") {
            price = calculateRequiredLimitPriceToBuy(symbol, amountScaled);
        } else {
            throw new IllegalArgumentException("illegal order side " + side);
        }

        final CreateOrder payload = new CreateOrder();
        payload.setSymbol(symbol);
        payload.setVolume(amountScaled.floatValue());
        payload.setPrice(Float.parseFloat(price.toPlainString()));
        payload.setDirection(side);
        LOG.info("BLA");

        LOG.info(payload.toString());


        return payload;
    }

    private static void wait(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException ignored) {
        }
    }

    
}