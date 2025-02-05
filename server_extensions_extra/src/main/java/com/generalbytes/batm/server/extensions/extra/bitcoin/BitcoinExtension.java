/*************************************************************************************
 * Copyright (C) 2014-2025 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.IPaymentProcessor;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseApiFactory;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseApiWrapper;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseApiWrapperLegacy;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseV2ApiWrapperLegacy;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance.BinanceComExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance.BinanceUsExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.BitbuyExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitfinex.BitfinexExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitflyer.BitFlyerExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.BitpandaProExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitstamp.BitstampExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.BittrexExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.CoinbaseExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.ICoinbaseAPILegacy;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbasepro.CoinbaseProExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coingi.CoingiExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.CoinZixExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.DVChainExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.enigma.EnigmaExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.hitbtc.HitbtcExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.poloniex.PoloniexExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.StillmanDigitalExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.bitcoinpay.BitcoinPayPP;
import com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.coinofsale.CoinOfSalePP;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bitkub.BitKubRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bity.BityRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.coingecko.CoinGeckoRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.coinpaprika.CoinPaprikaRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.mrcoin.MrCoinRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.satangpro.SatangProRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitcoind.BATMBitcoindRPCWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitcoind.BATMBitcoindRPCWalletWithUniqueAddresses;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitcore.BitcoreWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.BitgoWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.BitgoWalletWithUniqueAddresses;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.CoinbaseV2RateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.CoinbaseWalletV2;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.CoinbaseWalletV2WithUniqueAddresses;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.ICoinbaseV2APILegacy;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.CryptXWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.CryptXWithUniqueAddresses;
import com.generalbytes.batm.server.extensions.extra.ethereum.UsdcDefinition;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;
import com.google.common.collect.ImmutableSet;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import static com.generalbytes.batm.common.currencies.CryptoCurrency.USDT;
import static com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitflyer.BitFlyerExchange.BITFLYER_COM_BASE_URL;
import static com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitflyer.BitFlyerExchange.BITFLYER_JP_BASE_URL;
import static com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.ICryptXAPI.PRIORITY_CUSTOM;
import static com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.ICryptXAPI.PRIORITY_HIGH;
import static com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.ICryptXAPI.PRIORITY_LOW;
import static com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.ICryptXAPI.PRIORITY_MEDIUM;

public class BitcoinExtension extends AbstractExtension {
    private IExtensionContext ctx;

    @Override
    public void init(IExtensionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public String getName() {
        return "BATM Bitcoin extra extension";
    }

    @Override
    public IExchange createExchange(String paramString) {
        try {
        if ((paramString != null) && (!paramString.trim().isEmpty())) {
            StringTokenizer paramTokenizer = new StringTokenizer(paramString, ":");
            String prefix = paramTokenizer.nextToken();
            if ("bitfinex".equalsIgnoreCase(prefix)) {
                String apiKey = paramTokenizer.nextToken();
                String apiSecret = paramTokenizer.nextToken();
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (paramTokenizer.hasMoreTokens()) {
                    preferredFiatCurrency = paramTokenizer.nextToken().toUpperCase();
                }
                return new BitfinexExchange(apiKey, apiSecret, preferredFiatCurrency);
            } else if ("bitstamp".equalsIgnoreCase(prefix)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                String something = paramTokenizer.nextToken();
                String userId;
                if (something.equalsIgnoreCase(FiatCurrency.USD.getCode())
                    || something.equalsIgnoreCase(FiatCurrency.EUR.getCode())
                    || something.equalsIgnoreCase(FiatCurrency.GBP.getCode())) {
                    preferredFiatCurrency = something.toUpperCase();
                    userId = paramTokenizer.nextToken();
                } else {
                    userId = something;
                }

                String apiKey = paramTokenizer.nextToken();
                String secretKey = paramTokenizer.nextToken();
                return new BitstampExchange(preferredFiatCurrency, userId, apiKey, secretKey);
            } else if ("bittrex".equalsIgnoreCase(prefix)) {
                String apiKey = paramTokenizer.nextToken();
                String apiSecret = paramTokenizer.nextToken();
                return new BittrexExchange(apiKey, apiSecret, FiatCurrency.USD.getCode());
            } else if ("hitbtc".equalsIgnoreCase(prefix)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                String apiKey = paramTokenizer.nextToken();
                String apiSecret = paramTokenizer.nextToken();
                return new HitbtcExchange(apiKey, apiSecret, preferredFiatCurrency);
            } else if ("coinbaseexchange".equalsIgnoreCase(prefix)) {
                String apiKey = paramTokenizer.nextToken().trim();
                String secretKey = paramTokenizer.nextToken().trim();

                String accountName = null;
                String preferedFiatCurrency = null;
                String paymentMethodName = null;

                if(paramTokenizer.hasMoreTokens()) {
                    accountName = paramTokenizer.nextToken().trim();
                }

                if(paramTokenizer.hasMoreTokens()) {
                    preferedFiatCurrency = paramTokenizer.nextToken().toUpperCase().trim();
                }

                if(paramTokenizer.hasMoreTokens()) {
                    paymentMethodName = paramTokenizer.nextToken().trim();
                }
                ICoinbaseAPILegacy api = CoinbaseApiFactory.createCoinbaseApiLegacy();
                CoinbaseApiWrapper apiWrapper = new CoinbaseApiWrapperLegacy(api, apiKey, secretKey);
                return new CoinbaseExchange(apiWrapper, accountName, preferedFiatCurrency, paymentMethodName);
            } else if ("coinbasepro".equalsIgnoreCase(prefix)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                String key = paramTokenizer.nextToken();
                String secret = paramTokenizer.nextToken();
                String passphrase = paramTokenizer.nextToken();
                if (paramTokenizer.hasMoreTokens()) {
                    preferredFiatCurrency = paramTokenizer.nextToken().toUpperCase();
                }
                return new CoinbaseProExchange(key, secret, passphrase, preferredFiatCurrency, false);
            } else if ("coingi".equalsIgnoreCase(prefix)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                String key = paramTokenizer.nextToken();
                String privateKey = paramTokenizer.nextToken();
                if (paramTokenizer.hasMoreTokens()) {
                    preferredFiatCurrency = paramTokenizer.nextToken().toUpperCase();
                }
                return new CoingiExchange(key, privateKey, preferredFiatCurrency);
            } else if ("dvchain".equalsIgnoreCase(prefix)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                String apiSecret = paramTokenizer.nextToken();
                return new DVChainExchange(apiSecret, preferredFiatCurrency);
            } else if ("bitflyer".equalsIgnoreCase(prefix)) {
                String preferredFiatCurrency = paramTokenizer.nextToken().toUpperCase();
                String key = paramTokenizer.nextToken();
                String secret = paramTokenizer.nextToken();
                return new BitFlyerExchange(preferredFiatCurrency, key, secret, BITFLYER_JP_BASE_URL);
            } else if ("bitflyer.com".equalsIgnoreCase(prefix)) {
                String preferredFiatCurrency = paramTokenizer.nextToken().toUpperCase();
                String key = paramTokenizer.nextToken();
                String secret = paramTokenizer.nextToken();

                return new BitFlyerExchange(preferredFiatCurrency, key, secret, BITFLYER_COM_BASE_URL);
            } else if ("enigma".equalsIgnoreCase(prefix)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                String username = paramTokenizer.nextToken();
                String password = paramTokenizer.nextToken();
                if (paramTokenizer.hasMoreTokens()) {
                    preferredFiatCurrency = paramTokenizer.nextToken().toUpperCase();
                }
                return new EnigmaExchange(username, password, preferredFiatCurrency);

            } else if ("binancecom".equalsIgnoreCase(prefix)) {
                String preferredFiatCurrency = FiatCurrency.EUR.getCode();
                String apikey = paramTokenizer.nextToken();
                String secretKey = paramTokenizer.nextToken();
                if (paramTokenizer.hasMoreTokens()) {
                    preferredFiatCurrency = paramTokenizer.nextToken().toUpperCase();
                }
                return new BinanceComExchange(apikey, secretKey, preferredFiatCurrency);

            } else if ("binanceus".equalsIgnoreCase(prefix)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                String apikey = paramTokenizer.nextToken();
                String secretKey = paramTokenizer.nextToken();
                if (paramTokenizer.hasMoreTokens()) {
                    preferredFiatCurrency = paramTokenizer.nextToken().toUpperCase();
                }
                return new BinanceUsExchange(apikey, secretKey, preferredFiatCurrency);

            } else if ("bitpandapro".equalsIgnoreCase(prefix)) {
                String preferredFiatCurrency = FiatCurrency.EUR.getCode();
                String apikey = paramTokenizer.nextToken();
                if (paramTokenizer.hasMoreTokens()) {
                    preferredFiatCurrency = paramTokenizer.nextToken().toUpperCase();
                }
                return BitpandaProExchange.asExchange(apikey, preferredFiatCurrency);

            } else if ("poloniex".equalsIgnoreCase(prefix)) {
                String preferredFiatCurrency = USDT.getCode();
                String key = paramTokenizer.nextToken();
                String secret = paramTokenizer.nextToken();
                if (paramTokenizer.hasMoreTokens()) {
                    preferredFiatCurrency = paramTokenizer.nextToken().toUpperCase();
                }
                return new PoloniexExchange(key, secret, preferredFiatCurrency);
            } else if ("coinzix".equalsIgnoreCase(prefix)) {
                String token = paramTokenizer.nextToken();
                String secret = paramTokenizer.nextToken();
                return new CoinZixExchange(token, secret);
            } else if ("bitbuy".equalsIgnoreCase(prefix)) {
                String apiKey = paramTokenizer.nextToken();
                String apiSecret = paramTokenizer.nextToken();
                return new BitbuyExchange(apiKey, apiSecret);
            } else if ("stillmandigital".equalsIgnoreCase(prefix)) {
                String apiKey = paramTokenizer.nextToken();
                String apiSecret = paramTokenizer.nextToken();
                boolean useSandbox = paramTokenizer.hasMoreTokens() && paramTokenizer.nextToken().equals("sandbox");
                return new StillmanDigitalExchange(apiKey, apiSecret, useSandbox);
            }
        }
        } catch (Exception e) {
            ExtensionsUtil.logExtensionParamsException("createExchange", getClass().getSimpleName(), paramString, e);
        }
        return null;
    }

    @Override
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin) {
        if (paymentProcessorLogin != null && !paymentProcessorLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(paymentProcessorLogin, ":");
            String processorType = st.nextToken();
            if ("bitcoinpay".equalsIgnoreCase(processorType)) { // bitcoinpay:msciu823jes
                if (st.hasMoreTokens()) {
                    String apiKey = st.nextToken();
                    return new BitcoinPayPP(apiKey);
                }
            } else if ("coinofsale".equalsIgnoreCase(processorType)) { // coinofsale:token:pin
                String token = st.nextToken();
                String pin = st.nextToken();
                return new CoinOfSalePP(token, pin);
            }
        }
        return null;
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        try{
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();
            if ("bitcoind".equalsIgnoreCase(walletType) || "bitcoindnoforward".equalsIgnoreCase(walletType)) {
                // "bitcoind:protocol:user:password:ip:port:label"

                String protocol = st.nextToken();
                String username = st.nextToken();
                String password = st.nextToken();
                String hostname = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                String label = "";
                if (st.hasMoreTokens()) {
                    label = st.nextToken();
                }

                InetSocketAddress tunnelAddress = ctx.getTunnelManager().connectIfNeeded(walletLogin, tunnelPassword, InetSocketAddress.createUnresolved(hostname, port));
                hostname = tunnelAddress.getHostString();
                port = tunnelAddress.getPort();

                if (protocol != null && username != null && password != null && hostname != null && label != null) {
                    String rpcURL = protocol + "://" + username + ":" + password + "@" + hostname + ":" + port;
                    if ("bitcoindnoforward".equalsIgnoreCase(walletType)) {
                        return new BATMBitcoindRPCWalletWithUniqueAddresses(rpcURL);
                    }
                    return new BATMBitcoindRPCWallet(rpcURL, label);
                }
            } else if ("bitcore".equalsIgnoreCase(walletType)) { // bitcore:apiKey:proxyUrl
                String apiKey = st.nextToken();
                // the next token is a URL, so we can't use : as a delimiter
                // instead use \n and then remove the leading :
                String proxyUrl = st.nextToken("\n").replaceFirst(":", "");
                return new BitcoreWallet(apiKey, proxyUrl);
            } else if ("bitgo".equalsIgnoreCase(walletType) || "bitgonoforward".equalsIgnoreCase(walletType)) {
                // BitGo API Specification: https://developers.bitgo.com/api/express.wallet.sendcoins
                //
                // bitgo:host:port:token:wallet_address:wallet_passphrase:num_blocks:fee_rate:max_fee_rate
                // but host is optionally including the "http://" and port is optional,
                // num_blocks is an optional integer greater than 2 and it's used to calculate mining fee,
                // fee_rate is an optional integer defined fee rate,
                // max_fee_rate is an optional integer defined maximum fee rate.
                // bitgo:http://localhost:80:token:wallet_address:wallet_passphrase
                // bitgo:http://localhost:token:wallet_address:wallet_passphrase
                // bitgo:localhost:token:wallet_address:wallet_passphrase
                // bitgo:localhost:80:token:wallet_address:wallet_passphrase
                // bitgo:localhost:80:token:wallet_address:wallet_passphrase:num_blocks

                String first = st.nextToken();
                String scheme;
                String host;
                if (first.startsWith("http")) {
                    scheme = first;
                    host = st.nextToken().replaceAll("/", "");
                } else {
                    scheme = "http";
                    host = first;
                }

                int port;
                String token;
                String next = st.nextToken();
                if (next.length() > 6) {
                    port = scheme.equals("https") ? 443 : 80;
                    token = next;
                } else {
                    port = Integer.parseInt(next);
                    token = st.nextToken();
                }
                String walletId = st.nextToken();
                String walletPassphrase = st.nextToken();

                InetSocketAddress tunnelAddress = ctx.getTunnelManager().connectIfNeeded(walletLogin, tunnelPassword, InetSocketAddress.createUnresolved(host, port));
                host = tunnelAddress.getHostString();
                port = tunnelAddress.getPort();

                int numBlocks = 2;
                if (st.hasMoreTokens()) {
                    int number = Integer.parseInt(st.nextToken());
                    if (number > 2) {
                        numBlocks = number;
                    }
                }

                Integer feeRate = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : null;
                Integer maxFeeRate = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : null;
                if (feeRate != null && (maxFeeRate == null || feeRate > maxFeeRate)) {
                    maxFeeRate = feeRate;
                }

                if ("bitgonoforward".equalsIgnoreCase(walletType)) {
                    return new BitgoWalletWithUniqueAddresses(scheme, host, port, token, walletId, walletPassphrase, numBlocks, feeRate, maxFeeRate);
                }

                return new BitgoWallet(scheme, host, port, token, walletId, walletPassphrase, numBlocks, feeRate, maxFeeRate);

            } else if ("coinbasewallet2".equalsIgnoreCase(walletType)
                || "coinbasewallet2noforward".equalsIgnoreCase(walletType)) {
                String apiKey = st.nextToken();
                String secretKey = st.nextToken();

                String accountName = null;
                if (st.hasMoreTokens()) {
                    accountName = st.nextToken();
                    if (accountName.trim().isEmpty()) {
                        accountName = null;
                    }
                }
                ICoinbaseV2APILegacy api = CoinbaseApiFactory.createCoinbaseV2ApiLegacy();
                CoinbaseV2ApiWrapperLegacy apiWrapper = new CoinbaseV2ApiWrapperLegacy(api, apiKey, secretKey);
                if ("coinbasewallet2noforward".equalsIgnoreCase(walletType)) {
                    return new CoinbaseWalletV2WithUniqueAddresses(apiWrapper, accountName);
                }
                return new CoinbaseWalletV2(apiWrapper, accountName);
            } else if ("cryptx".equalsIgnoreCase(walletType) || "cryptxnoforward".equalsIgnoreCase(walletType)) {

                String first = st.nextToken();
                String scheme;
                String host;
                if (first.startsWith("http")) {
                    scheme = first;
                    host = st.nextToken().replaceAll("/", "");
                } else {
                    scheme = "http";
                    host = first;
                }

                int port;
                String token;
                String next = st.nextToken();
                if (next.length() > 6) {
                    port = scheme.equals("https") ? 443 : 80;
                    token = next;
                } else {
                    port = Integer.parseInt(next);
                    token = st.nextToken();
                }
                String walletId = st.nextToken();

                String password = null;
	            String priority = null;

	            if (st.hasMoreTokens()) {
	                String nextToken = st.nextToken();
	                if (!nextToken.equals(PRIORITY_LOW) && !nextToken.equals(PRIORITY_MEDIUM) &&
                            !nextToken.equals(PRIORITY_HIGH) && !nextToken.equals(PRIORITY_CUSTOM)) {
	                    password = nextToken;
	                    if (password.isEmpty()) password = null;

	                    if (st.hasMoreTokens()) {
	                        priority = st.nextToken();
	                        if (priority.isEmpty()) priority = null;
                        }

                    } else {
	                    priority = nextToken;
                    }
	            }

	            String customFeePrice = null;
	            if (priority != null && priority.equals(PRIORITY_CUSTOM) && st.hasMoreTokens()) {
		            priority = null;
		            customFeePrice = st.nextToken();
		            if (customFeePrice.isEmpty()) {
			            customFeePrice = null;
		            }
	            }

	            String customGasLimit = null;
	            if (st.hasMoreTokens()){
		            customGasLimit = st.nextToken();
		            if (customGasLimit.isEmpty()) {
			            customGasLimit = null;
		            }
	            }

                if ("cryptxnoforward".equalsIgnoreCase(walletType)) {
                    return new CryptXWithUniqueAddresses(scheme, host, port, token, walletId, priority, customFeePrice, customGasLimit, password);
                }
                return new CryptXWallet(scheme, host, port, token, walletId, priority, customFeePrice, customGasLimit, password);
            }
        }
        } catch (Exception e) {
            ExtensionsUtil.logExtensionParamsException("createWallet", getClass().getSimpleName(), walletLogin, e);
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.BNB.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new BinanceCoinAddressValidator();
        }
        return null; // no BTC address validator in open source version so far (It is present in
                     // built-in extension)
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        return null; // no BTC paper wallet generator in open source version so far (It is present in
                     // built-in extension)
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        try {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String rsType = st.nextToken();

            if ("btcfix".equalsIgnoreCase(rsType)) {
                BigDecimal rate = BigDecimal.ZERO;
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate, preferredFiatCurrency);
            } else if ("fixprice".equalsIgnoreCase(rsType)) {
                BigDecimal rate = BigDecimal.ZERO;
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate, preferredFiatCurrency);
            } else if ("bitfinex".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new BitfinexExchange(preferredFiatCurrency);
            } else if ("bitstamp".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new BitstampExchange(preferredFiatCurrency);
            } else if ("bittrex".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = st.hasMoreTokens() ? st.nextToken().toUpperCase() : FiatCurrency.USD.getCode();
                return new BittrexExchange(preferredFiatCurrency);
            } else if ("bity".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.CHF.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new BityRateSource(preferredFiatCurrency);
            } else if ("mrcoin".equalsIgnoreCase(rsType)) {
                return new MrCoinRateSource();
            }else if ("coinbasers".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                // Rate Source doesn't need credentials
                ICoinbaseV2APILegacy api = CoinbaseApiFactory.createCoinbaseV2ApiLegacy();
                CoinbaseV2ApiWrapperLegacy apiWrapper = new CoinbaseV2ApiWrapperLegacy(api, null, null);
                return new CoinbaseV2RateSource(preferredFiatCurrency, apiWrapper);
            } else if ("coinbasepro".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new CoinbaseProExchange(preferredFiatCurrency);
            } else if ("coingi".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new CoingiExchange(preferredFiatCurrency);
            } else if ("coingecko".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = st.hasMoreTokens() ? st.nextToken().toUpperCase() : FiatCurrency.USD.getCode();
                return new CoinGeckoRateSource(preferredFiatCurrency);
            } else if ("coinpaprika".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = st.hasMoreTokens() ? st.nextToken().toUpperCase() : FiatCurrency.USD.getCode();
                return new CoinPaprikaRateSource(preferredFiatCurrency);
            }else if ("bitflyer".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.JPY.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new BitFlyerExchange(preferredFiatCurrency, BITFLYER_JP_BASE_URL);
            }else if ("bitflyer.com".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new BitFlyerExchange(preferredFiatCurrency, BITFLYER_COM_BASE_URL);
            } else if ("enigma".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new EnigmaExchange(preferredFiatCurrency);
            } else if ("bitkub".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.THB.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new BitKubRateSource(preferredFiatCurrency);

            } else if ("satangpro".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.THB.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new SatangProRateSource(preferredFiatCurrency);
            } else if ("binancecom".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.EUR.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new BinanceComExchange(preferredFiatCurrency);
            } else if ("binanceus".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new BinanceUsExchange(preferredFiatCurrency);
            } else if ("bitpandapro".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.EUR.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return BitpandaProExchange.asRateSource(preferredFiatCurrency);
            } else if ("poloniex".equals(rsType)) {
                String preferredFiatCurrency = USDT.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new PoloniexExchange(preferredFiatCurrency);
            } else if ("coinzix".equals(rsType)) {
                return new CoinZixExchange();
            } else if ("bitbuy".equalsIgnoreCase(rsType)) {
                String apiKey = st.nextToken();
                String apiSecret = st.nextToken();
                return new BitbuyExchange(apiKey, apiSecret);
            } else if ("stillmandigital".equalsIgnoreCase(rsType)) {
                String apiKey = st.nextToken();
                String apiSecret = st.nextToken();
                boolean useSandbox = st.hasMoreTokens() && st.nextToken().equals("sandbox");
                return new StillmanDigitalExchange(apiKey, apiSecret, useSandbox);
            }
        }
        } catch (Exception e) {
            ExtensionsUtil.logExtensionParamsException("createRateSource", getClass().getSimpleName(), sourceLogin, e);
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.BCH.getCode());
        result.add(CryptoCurrency.EGLD.getCode());
        result.add(CryptoCurrency.USDTTRON.getCode());
        result.add(CryptoCurrency.BNB.getCode());
        result.add(CryptoCurrency.USDC.getCode());
        return result;
    }

    @Override
    public Set<String> getSupportedWatchListsNames() {
        return null;
    }

    @Override
    public IWatchList getWatchList(String name) {
        return null;
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        return ImmutableSet.of(new UsdcDefinition());
    }
}
