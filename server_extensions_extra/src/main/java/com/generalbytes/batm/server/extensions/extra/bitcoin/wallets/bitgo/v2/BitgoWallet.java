/*************************************************************************************
 * Copyright (C) 2014-2024 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.Converters;
import com.generalbytes.batm.server.extensions.ICanSendMany;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCoinRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoSendManyRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoSendManyRequest.BitGoRecipient;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.util.net.CompatSSLSocketFactory;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import javax.ws.rs.HeaderParam;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BitgoWallet implements IWallet, ICanSendMany {

    private static final Logger log = LoggerFactory.getLogger(BitgoWallet.class);

    protected final IBitgoAPI api;
    protected String walletId;
    protected String walletPassphrase;
    protected String url;
    protected static final Integer readTimeout = 90 * 1000; //90 seconds
    protected Integer numBlocks;
    protected Integer feeRate;
    protected Integer maxFeeRate;

    protected static final Map<String, String> cryptoCurrencies = new HashMap<String, String>() {
        {
            put(CryptoCurrency.BCH.getCode(), "bch");
            put(CryptoCurrency.BTC.getCode(), "btc");
            put(CryptoCurrency.ETH.getCode(), "eth");
            put(CryptoCurrency.LTC.getCode(), "ltc");
            put(CryptoCurrency.USDT.getCode(), "usdt"); // ERC-20 (eth)
            put(CryptoCurrency.USDTTRON.getCode(), "trx:usdt");
            put(CryptoCurrency.XRP.getCode(), "xrp");
            put(CryptoCurrency.TBTC.getCode(), "tbtc");
        }
    };

    private static final Map<String, Integer> decimals = new HashMap<String, Integer>() {
        {
            put(CryptoCurrency.BTC.getCode(), pow10Exp(Converters.BTC));
            put(CryptoCurrency.LTC.getCode(), pow10Exp(Converters.LTC));
            put(CryptoCurrency.BCH.getCode(), pow10Exp(Converters.BCH));
            put(CryptoCurrency.ETH.getCode(), pow10Exp(Converters.ETH));
            put(CryptoCurrency.XRP.getCode(), pow10Exp(Converters.XRP));
            put(CryptoCurrency.USDT.getCode(), pow10Exp(Converters.USDT));
            put(CryptoCurrency.USDTTRON.getCode(), pow10Exp(Converters.USDTTRON));
            put(CryptoCurrency.TBTC.getCode(), pow10Exp(Converters.TBTC));
            put(CryptoCurrency.TLTC.getCode(), pow10Exp(Converters.TLTC));
            put(CryptoCurrency.TBCH.getCode(), pow10Exp(Converters.TBCH));
        }

        private int pow10Exp(BigDecimal val) {
            // return exp for val=10^exp
            // e.g. for 10^3=1000: precision=4, exp=3
            return val.precision() - 1;
        }
    };

    public BitgoWallet(String scheme, String host, int port, String token, String walletId, String walletPassphrase, Integer numBlocks) {
        this(scheme, host, port, token, walletId, walletPassphrase, numBlocks, null, null);
    }

    public BitgoWallet(String scheme, String host, int port, String token, String walletId, String walletPassphrase, Integer numBlocks, Integer feeRate, Integer maxFeeRate) {
        this.walletId = walletId;
        this.walletPassphrase = walletPassphrase;
        this.url = new HttpUrl.Builder().scheme(scheme).host(host).port(port).build().toString();
        this.numBlocks = numBlocks;
        this.feeRate = feeRate;
        this.maxFeeRate = maxFeeRate;

        ClientConfig config = new ClientConfig();
        config.setHttpReadTimeout(readTimeout);

        config.addDefaultParam(HeaderParam.class, "Authorization", "Bearer " + token);

        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            final CompatSSLSocketFactory socketFactory = new CompatSSLSocketFactory(sslcontext.getSocketFactory());
            config.setSslSocketFactory(socketFactory);
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            log.error("", e);
        }

        api = RestProxyFactory.createProxy(IBitgoAPI.class, this.url, config);
    }

    private String getResultTxId(Map<String, Object> result) {
        Objects.requireNonNull(result, "Returned map is null");
        if (result.get("txid") instanceof String) {
            return (String) result.get("txid");
        }
        log.warn("txid not returned: {}", result);
        return null;
    }

    @Override
    public String sendMany(Collection<Transfer> transfers, String cryptoCurrency, String description) {
        try {
            List<BitGoRecipient> recipients = transfers.stream()
                .map(transfer -> new BitGoRecipient(transfer.getDestinationAddress(), toSatoshis(transfer.getAmount(), cryptoCurrency)))
                .collect(Collectors.toList());
            final BitGoSendManyRequest request = new BitGoSendManyRequest(recipients, walletPassphrase, description, this.numBlocks);
            String bitgoCryptoCurrency = cryptoCurrencies.get(cryptoCurrency);
            return getResultTxId(api.sendMany(bitgoCryptoCurrency, this.walletId, request));
        } catch (HttpStatusIOException hse) {
            log.debug("send many error, HTTP status: {}, body: {}", hse.getHttpStatusCode(), hse.getHttpBody());
        } catch (ErrorResponseException e) {
            log.debug("send many error, HTTP status: {}, error: {}", e.getHttpStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            final BitGoCoinRequest request = new BitGoCoinRequest(destinationAddress, toSatoshis(amount, cryptoCurrency), walletPassphrase, description, this.numBlocks, this.feeRate, this.maxFeeRate);
            String bitgoCryptoCurrency = cryptoCurrencies.get(cryptoCurrency);
            return getResultTxId(api.sendCoins(bitgoCryptoCurrency, this.walletId, request));
        } catch (HttpStatusIOException hse) {
            log.debug("send coins error, HTTP status: {}, body: {}", hse.getHttpStatusCode(), hse.getHttpBody());
        } catch (ErrorResponseException e) {
            log.debug("send coins error, HTTP status: {}, error: {}", e.getHttpStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    protected String toSatoshis(BigDecimal amount, String cryptoCurrency) {
        return amount
            .movePointRight(getDecimals(cryptoCurrency))
            .setScale(0, RoundingMode.FLOOR)
            .toPlainString();
    }

    private Integer getDecimals(String cryptoCurrency) {
        return Objects.requireNonNull(decimals.get(cryptoCurrency), cryptoCurrency + " not supported");
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (cryptoCurrency == null) {
            cryptoCurrency = getPreferredCryptoCurrency();
        }
        String bitgoCryptoCurrency = cryptoCurrencies.get(cryptoCurrency);
        if (bitgoCryptoCurrency == null) {
            return null;
        }
        try {
            final Map<String, Object> response = api.getWalletById(bitgoCryptoCurrency, walletId);
            if (response == null || response.isEmpty()) {
                return null;
            }

            Object receiveAddressObj = response.get("receiveAddress");
            if (!(receiveAddressObj instanceof Map)) {
                return null;
            }

            Map receiveAddressMap = (Map)receiveAddressObj;
            Object addressObj = receiveAddressMap.get("address");
            if (!(addressObj instanceof String)) {
                return null;
            }
            return (String)addressObj;
        } catch (HttpStatusIOException hse) {
            log.debug("getCryptoAddress error, HTTP status: {}, body: {}", hse.getHttpStatusCode(), hse.getHttpBody());
        } catch (ErrorResponseException e) {
            log.debug("getCryptoAddress error, HTTP status: {}, error: {}", e.getHttpStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return cryptoCurrencies.keySet();
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CryptoCurrency.BTC.getCode();
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (cryptoCurrency == null) {
            cryptoCurrency = getPreferredCryptoCurrency();
        }
        String bitgoCryptoCurrency = cryptoCurrencies.get(cryptoCurrency);
        if (bitgoCryptoCurrency == null) {
            return null;
        }

        try {
            final Map<String, Object> response = api.getWalletById(bitgoCryptoCurrency, walletId);
            if (response == null || response.isEmpty()) {
                return null;
            }

            Object balanceObject = response.get("balanceString");
            if(balanceObject == null) {
                // fallback to older balance for backward compatibility only
                balanceObject = response.get("balance");
            }
            if (balanceObject == null) {
                return null;
            }

            BigDecimal satoshis = new BigDecimal(balanceObject.toString());
            return fromSatoshis(cryptoCurrency, satoshis);
        } catch (HttpStatusIOException hse) {
            log.debug("getCryptoBalance error, HTTP status: {}, body: {}", hse.getHttpStatusCode(), hse.getHttpBody());
        } catch (ErrorResponseException e) {
            log.debug("getCryptoBalance error, HTTP status: {}, error: {}", e.getHttpStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("getCryptoBalance error", e);
        }
        return null;
    }

    /**
     * converts balance from the smallest unit to the base unit, e.g. satoshi to bitcoin
     */
    protected BigDecimal fromSatoshis(String cryptoCurrency, BigDecimal satoshis) {
        return satoshis
            .setScale(0, RoundingMode.FLOOR)
            .movePointLeft(getDecimals(cryptoCurrency))
            .stripTrailingZeros();
    }

    public String toString() {
        return String.format("BitgoWalletV2[url = %s, wallet_id = %s]", url, walletId);
    }

    public String getUrl() {
        return url;
    }

    public String getWalletId() {
        return walletId;
    }
}
