package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;

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
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;
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
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.Account;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.Balance;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.CreateDepositAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.CreateOrder;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.CryptoWithdraw;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.DepositAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.MarketTick;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.Order;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.OrderBookEntry;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.OrderBookSnapshot;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.OrderState;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.Side;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.WithdrawCrypto;
import com.generalbytes.batm.server.extensions.util.net.CompatSSLSocketFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.util.concurrent.RateLimiter;

import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

/**
 * Exchange adapter for <a href="https://exchange.bitpanda.com">Bitpanda Pro</a>.
 */
public final class BitpandaProExchange implements IExchangeAdvanced, IRateSourceAdvanced {
    private static final Logger LOG = LoggerFactory.getLogger("batm.master.BitpandaProExchange");

    private static final URI LIVE_URL = URI.create("https://api.exchange.bitpanda.com");

    public static IRateSourceAdvanced asRateSource(String preferredFiatCurrency) {
        return new BitpandaProExchange(LIVE_URL, null, preferredFiatCurrency);
    }

    public static IExchangeAdvanced asExchange(String apikey, String preferredFiatCurrency) {
        Objects.requireNonNull(apikey, "apikey");
        return new BitpandaProExchange(LIVE_URL, apikey, preferredFiatCurrency);
    }

    // all CRYPTO_FIAT combinations are supported
    private static final ImmutableSet<String> FIAT_CURRENCIES = ImmutableSet.of(
        FiatCurrency.EUR.getCode(),
        FiatCurrency.CHF.getCode()
    );
    private static final ImmutableSet<String> CRYPTO_CURRENCIES = ImmutableSet.of(
        CryptoCurrency.BTC.getCode(),
        CryptoCurrency.ETH.getCode(),
        CryptoCurrency.XRP.getCode()
    );
    // from https://api.exchange.bitpanda.com/public/v1/instruments
    private static final Map<String, Integer> INSTRUMENT_AMOUNT_PRECISION = ImmutableMap.<String, Integer>builder()
        .put("BTC_EUR", 5)
        .put("ETH_EUR", 4)
        .put("XRP_EUR", 0)
        .put("BTC_CHF", 5)
        .put("ETH_CHF", 4)
        .put("XRP_CHF", 0)
        .build()
    ;


    private final IBitpandaProAPI api;
    // allow 2 requests per second -> 120 requests/minute, safely below api rate limits
    private final RateLimiter rateLimiter = RateLimiter.create(2);

    private final Cache<String, BigDecimal> rateCache = CacheBuilder
        .newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build();

    private final String preferredFiatCurrency;

    // visible for testing
    BitpandaProExchange(URI baseUri, String apikey, String preferredFiatCurrency) {
        this.preferredFiatCurrency = Objects.requireNonNull(preferredFiatCurrency, "preferred fiat currency");

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
            config.addDefaultParam(HeaderParam.class, "bp-client", "id=batm;device=atm");

            if (apikey != null) {
                config.addDefaultParam(HeaderParam.class, "Authorization", asAuthorizationHeader(apikey));
            }

            api = RestProxyFactory.createProxy(IBitpandaProAPI.class, baseUri.toASCIIString(), config, this::throttleApiRequests);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOG.error("failed to create an api client proxy", e);
            throw new IllegalStateException("failed to create an api client proxy", e);
        }
    }

    private Object throttleApiRequests(InvocationHandler invocationHandler, Object proxy, Method method, Object[] args) throws Throwable {
        rateLimiter.acquire();
        return invocationHandler.invoke(proxy, method, args);
    }

    private String asAuthorizationHeader(String apikey) {
        return "Bearer " + apikey;
    }

    //region Shared

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

    //endregion

    //region IRateSource

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        return safely(() -> {
            final String instrument = asInstrument(cryptoCurrency, fiatCurrency);
            return rateCache.get(instrument, () -> fetchExchangeRateLast(instrument));
        }, "getExchangeRateLast", cryptoCurrency, fiatCurrency);
    }

    private BigDecimal fetchExchangeRateLast(String instrument) {
        final MarketTick tick = api.marketTickerForInstrument(instrument);
        return tick.getLastPrice();
    }

    //endregion

    // region IRateSourceAdvanced

    private static final BigDecimal LIQUIDITY_PROBE_VOLUME = BigDecimal.ONE;

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        BigDecimal result = calculateBuyPrice(cryptoCurrency, fiatCurrency, LIQUIDITY_PROBE_VOLUME);
        if (result != null) {
            return result.divide(LIQUIDITY_PROBE_VOLUME, 2, BigDecimal.ROUND_UP);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        BigDecimal result = calculateSellPrice(cryptoCurrency, fiatCurrency, LIQUIDITY_PROBE_VOLUME);
        if (result != null) {
            return result.divide(LIQUIDITY_PROBE_VOLUME, 2, BigDecimal.ROUND_DOWN);
        }
        return null;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return safely(() -> {
            final String instrument = asInstrument(cryptoCurrency, fiatCurrency);
            return calculateRequiredLimitPriceToBuy(instrument, cryptoAmount).multiply(cryptoAmount);
        }, "calculateBuyPrice", cryptoCurrency, fiatCurrency, cryptoAmount);
    }

    private BigDecimal calculateRequiredLimitPriceToBuy(String instrument, BigDecimal amount) {
        final OrderBookSnapshot snapshot = api.orderBook(instrument, 2, 50);
        final SortedSet<OrderBookEntry> asks =
            ImmutableSortedSet.copyOf(comparing(OrderBookEntry::getPrice, naturalOrder()), snapshot.getAsks());
        return calculateRequiredLimitPrice(amount, asks);
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return safely(() -> {
            final String instrument = asInstrument(cryptoCurrency, fiatCurrency);
            return calculateRequiredLimitPriceToSell(instrument, cryptoAmount).multiply(cryptoAmount);
        }, "calculateSellPrice", cryptoCurrency, fiatCurrency, cryptoAmount);
    }

    private BigDecimal calculateRequiredLimitPriceToSell(String instrument, BigDecimal amount) {
        final OrderBookSnapshot snapshot = api.orderBook(instrument, 2, 50);
        final SortedSet<OrderBookEntry> bids =
            ImmutableSortedSet.copyOf(comparing(OrderBookEntry::getPrice, reverseOrder()), snapshot.getBids());
        return calculateRequiredLimitPrice(amount, bids);
    }

    /**
     * Determine the price to fully fill a given amount of base currency.
     *
     * @param amountToFill how much has to be traded
     * @param orderbook sorted price levels of the target orderbook
     * @return required price to fully fill given amount with a limit order
     */
    private BigDecimal calculateRequiredLimitPrice(BigDecimal amountToFill, Collection<OrderBookEntry> orderbook) {
        BigDecimal remaining = amountToFill;
        BigDecimal price = null;

        for (OrderBookEntry entry : orderbook) {
            remaining = remaining.subtract(entry.getAmount());
            price = entry.getPrice();

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

    // endregion

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
        final Account balances = api.balances();

        if (balances.getBalances() == null) {
            // account has no balances - implicitly empty
            return BigDecimal.ZERO;
        }

        for (Balance current : balances.getBalances()) {
            if (code.equalsIgnoreCase(current.getCurrencyCode())) {
                return current.getAvailable();
            }
        }

        return BigDecimal.ZERO; // a balance is created on first deposit/trade, until then it is empty implicitly
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        return safely(() -> {
            assertValidCrypto(cryptoCurrency);

            final DepositAddress existing = api.cryptoDepositAddress(cryptoCurrency);

            if (existing.getEnabled()) {
                return addressFrom(existing);
            }

            if (!existing.getCanCreateMore()) {
                throw new IllegalStateException("no valid deposit address available and cannot create more");
            }

            // no address existing or not active anymore - create a new one
            final CreateDepositAddress payload = new CreateDepositAddress();
            payload.setCurrency(cryptoCurrency);
            final DepositAddress created = api.createCryptoDepositAddress(payload);

            if (created.getEnabled()) {
                return addressFrom(created);
            }

            throw new IllegalStateException("was not able to create a new deposit address");
        }, "getDepositAddress", cryptoCurrency);
    }

    private String addressFrom(DepositAddress address) {
        if (address.getDestinationTag() != null) {
            return address.getAddress() + ":" + address.getDestinationTag();
        }
        return address.getAddress();
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        return safely(() -> {
            assertValidCrypto(cryptoCurrency);

            final WithdrawCrypto.Recipient recipient;
            if (CryptoCurrency.XRP.getCode().equals(cryptoCurrency)) {
                recipient = xrpRecipient(destinationAddress);
            } else {
                recipient = defaultRecipient(destinationAddress);
            }

            final WithdrawCrypto payload = new WithdrawCrypto();
            payload.setAmount(amount);
            payload.setCurrency(cryptoCurrency);
            payload.setRecipient(recipient);

            final CryptoWithdraw withdrawal = api.withdrawCrypto(payload);

            return withdrawal.getTransactionId().toString();
        }, "sendCoins", destinationAddress, amount, cryptoCurrency, description);
    }

    private WithdrawCrypto.Recipient defaultRecipient(String destinationAddress) {
        final WithdrawCrypto.Recipient recipient = new WithdrawCrypto.Recipient();
        recipient.setAddress(destinationAddress);
        return recipient;
    }

    private WithdrawCrypto.Recipient xrpRecipient(String destinationAddress) {
        final WithdrawCrypto.Recipient recipient = new WithdrawCrypto.Recipient();
        final String[] addressParts = destinationAddress.split(":");
        if (addressParts.length == 2) {
            recipient.setAddress(addressParts[0]);
            recipient.setDestinationTag(addressParts[1]);
        } else {
            recipient.setAddress(destinationAddress);
            recipient.setDestinationTag(null);
        }
        return recipient;
    }

    private static final int MAX_ORDER_FILL_CHECKS = 10;

    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return safely(() -> {
            final CreateOrder request = limitOrder(Side.BUY, amount, cryptoCurrency, fiatCurrencyToUse);
            return executeSync(request).toString();
        }, "purchaseCoins", amount, cryptoCurrency, fiatCurrencyToUse, description);
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse,
        String description) {
        return safely(() -> {
            final CreateOrder request = limitOrder(Side.SELL, cryptoAmount, cryptoCurrency, fiatCurrencyToUse);
            return executeSync(request).toString();
        }, "sellCoins", cryptoAmount, cryptoCurrency, fiatCurrencyToUse, description);
    }

    private UUID executeSync(CreateOrder payload) {
        LOG.info("creating limit order using {}", payload);
        final Order order = api.createOrder(payload);
        LOG.info("limit order created: {}", order);
        wait(Duration.ofSeconds(2));

        for (int checks = 0; checks < MAX_ORDER_FILL_CHECKS; checks++) {
            OrderState current;
            try {
                current = api.getOrder(order.getOrderId());
            } catch (final IBitpandaProAPI.ApiError e) {
                LOG.debug("failed to get order status {}", e.toString());
                current = null;
            }

            if (current != null && Order.Status.FILLED_FULLY == current.getOrder().getStatus()) {
                LOG.debug("order filled {}", current.getOrder());
                return order.getOrderId();
            }

            LOG.debug("waiting for order to be processed {}", current);
            wait(Duration.ofSeconds(3));
        }

        throw new IllegalStateException("executed order was not fully filled in time");
    }

    // endregion

    // region IExchangeAdvanced

    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return safely(() -> new OrderExecutionTask(limitOrder(Side.BUY, amount, cryptoCurrency, fiatCurrencyToUse)),
            "createPurchaseCoinsTask", amount, cryptoCurrency, fiatCurrencyToUse, description);
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return safely(() -> new OrderExecutionTask(limitOrder(Side.SELL, amount, cryptoCurrency, fiatCurrencyToUse)),
            "createSellCoinsTask", amount, cryptoCurrency, fiatCurrencyToUse, description);
    }

    private final class OrderExecutionTask implements ITask {

        private final CreateOrder request;
        // task state
        private Instant start;
        private boolean finished;
        private UUID orderId;
        private String result;

        public OrderExecutionTask(CreateOrder request) {
            this.request = request;
            this.request.setClientId(UUID.randomUUID()); // prevents accidental replay
        }

        @Override public boolean onCreate() {
            return wrapped(() -> {
                LOG.info("creating limit order using {}", request);
                final Order order = api.createOrder(request);
                this.orderId = order.getOrderId();
                LOG.info("limit order created: {}", order);

                BitpandaProExchange.wait(Duration.ofSeconds(2));

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

                final OrderState current = api.getOrder(orderId);
                if (Order.Status.FILLED_FULLY == current.getOrder().getStatus()) {
                    LOG.debug("order filled {}", current.getOrder());
                    this.result = current.getOrder().getOrderId().toString();
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

    // endregion

    private CreateOrder limitOrder(Side side, BigDecimal amount, String base, String quote) {
        final String instrument = asInstrument(base, quote);
        final BigDecimal amountScaled = amount.setScale(INSTRUMENT_AMOUNT_PRECISION.get(instrument), RoundingMode.FLOOR);

        final BigDecimal price;
        if (side == Side.SELL) {
            price = calculateRequiredLimitPriceToSell(instrument, amountScaled);
        } else if (side == Side.BUY) {
            price = calculateRequiredLimitPriceToBuy(instrument, amountScaled);
        } else {
            throw new IllegalArgumentException("illegal order side " + side);
        }

        final CreateOrder payload = new CreateOrder();
        payload.setInstrumentCode(instrument);
        payload.setAmount(amountScaled);
        payload.setPrice(price);
        payload.setSide(side);
        payload.setType(Order.Type.LIMIT);

        return payload;
    }

    private String asInstrument(String base, String quote) {
        assertValidCrypto(base);
        assertValidFiat(quote);
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

    private static void wait(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException ignored) {
        }
    }
}
