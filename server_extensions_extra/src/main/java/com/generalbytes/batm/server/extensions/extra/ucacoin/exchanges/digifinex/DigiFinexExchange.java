package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex;

import static java.lang.String.format;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto.Account;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto.Balance;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto.DepositAddresses;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto.MarketTick;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto.OrderBookSnapshot;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto.Symbol;
import com.generalbytes.batm.server.extensions.util.net.CompatSSLSocketFactory;

import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.account.AccountInfo;

import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HmacPostBodyDigest;
import si.mazi.rescu.RestProxyFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.RateLimiter;

public class DigiFinexExchange implements IExchangeAdvanced, IRateSourceAdvanced {

	private static final Logger LOG = LoggerFactory.getLogger("batm.master.DigiFinexExchange");

    private static final URI LIVE_URL = URI.create("https://openapi.digifinex.com");

    public static IRateSourceAdvanced asRateSource(String preferredFiatCurrency) {
        return new DigiFinexExchange(LIVE_URL, null, null, preferredFiatCurrency);
    }

    public static IExchangeAdvanced asExchange(String apikey, String secret, String preferredFiatCurrency) {
		Objects.requireNonNull(apikey, "apikey");
		Objects.requireNonNull(apikey, "secret");

        return new DigiFinexExchange(LIVE_URL, apikey, secret, preferredFiatCurrency);
    }
	
	 // all CRYPTO_FIAT combinations are supported
	 private static final ImmutableSet<String> FIAT_CURRENCIES = ImmutableSet.of(
        FiatCurrency.USD.getCode()
    );
    private static final ImmutableSet<String> CRYPTO_CURRENCIES = ImmutableSet.of(
        CryptoCurrency.BTC.getCode(),
        CryptoCurrency.UCA.getCode()
    );
    // from https://api.exchange.bitpanda.com/public/v1/instruments
    private static final Map<String, Integer> INSTRUMENT_AMOUNT_PRECISION = ImmutableMap.<String, Integer>builder()
        .put("BTC_UCA", 8)
        .put("UCA_USD", 5)
        .build()
    ;

    private final IDigiFinexAPI api;
    // allow 2 requests per second -> 120 requests/minute, safely below api rate limits
    private final RateLimiter rateLimiter = RateLimiter.create(2);

    private final Cache<String, BigDecimal> rateCache = CacheBuilder
        .newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build();

    private final String preferredFiatCurrency;

	
	private String secret;

    // visible for testing
    DigiFinexExchange(URI baseUri, String apikey, String secret, String preferredFiatCurrency) {
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
            config.addDefaultParam(HeaderParam.class, "df-client", "id=batm;device=atm");

            if (apikey != null) {
                config.addDefaultParam(HeaderParam.class, "ACCESS-KEY", apikey);
			}
			if (secret != null) {
				this.secret = secret;
			}
            api = RestProxyFactory.createProxy(IDigiFinexAPI.class, baseUri.toASCIIString(), config, this::throttleApiRequests);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOG.error("failed to create an api client proxy", e);
            throw new IllegalStateException("failed to create an api client proxy", e);
        }
    }

    private Object throttleApiRequests(InvocationHandler invocationHandler, Object proxy, Method method, Object[] args) throws Throwable {
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
	
	//region IRateSource

	@Override
	public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
		return safely(() -> {
			final String instrument = asInstrument(cryptoCurrency, fiatCurrency);
			return rateCache.get(instrument, () -> fetchExchangeRateLast(instrument));
		}, "getExchangeRateLast", cryptoCurrency, fiatCurrency);
	}

	private BigDecimal fetchExchangeRateLast(String instrument) {
		Symbol symbol = new Symbol();
		symbol.setSymbol(instrument);
		final MarketTick tick = api.marketTickerForInstrument(symbol);
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
        final OrderBookSnapshot snapshot = api.orderBook(instrument, 50);
		final List<List<Float>> asks = snapshot.getAsks();
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
		final OrderBookSnapshot snapshot = api.orderBook(instrument, 50);
		final List<List<Float>> bids = snapshot.getBids();

        return calculateRequiredLimitPrice(amount, bids);
    }

    /**
     * Determine the price to fully fill a given amount of base currency.
     *
     * @param amountToFill how much has to be traded
     * @param orderbook sorted price levels of the target orderbook
     * @return required price to fully fill given amount with a limit order
     */
    private BigDecimal calculateRequiredLimitPrice(BigDecimal amountToFill, List<List<Float>> orderbook) {
        BigDecimal remaining = amountToFill;
        BigDecimal price = null;

        for (List<Float> entry : orderbook) {
			BigDecimal valueRemaining = new BigDecimal(Float.toString(entry.get(0)));
			remaining = remaining.subtract(valueRemaining);
			BigDecimal valuePrice = new BigDecimal(Float.toString(entry.get(1)));
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
        final Account balances = api.balances(
            RequestSigner.createInstance(secret), 
            Long.toString(System.currentTimeMillis()/1000));
        
        if (balances.getBalances() == null) {
            // account has no balances - implicitly empty
            return BigDecimal.ZERO;
        }

        for (Balance current : balances.getBalances()) {
            if (code.equalsIgnoreCase(current.getCurrency())) {
                return current.getFree();
            }
        }

        return BigDecimal.ZERO; // a balance is created on first deposit/trade, until then it is empty implicitly
    }


	@Override
	public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse,
			String description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse,
			String description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDepositAddress(String cryptoCurrency) {
		DepositAddresses depositAddresses = api.getDepositAddresses(
            cryptoCurrency, 
            RequestSigner.createInstance(secret), 
            Long.toString(System.currentTimeMillis()/1000));
            LOG.info(depositAddresses.toString());
		return null;
	}

	@Override
	public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse,
			String description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse,
			String description) {
		// TODO Auto-generated method stub
		return null;
	}

	private Wallet getWallet(AccountInfo accountInfo) {
        Map<String, Wallet> wallets = accountInfo.getWallets();
        if (wallets.containsKey("exchange")) {
            return wallets.get("exchange");
        }
        throw new UnsupportedOperationException("Wallets in account: " + wallets.keySet());
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

    
}