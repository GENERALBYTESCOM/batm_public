/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashSet;
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

    public BitgoWallet(String scheme, String host, int port, String token, String walletId, String walletPassphrase) {
      this(scheme, host, port, token, walletId, walletPassphrase, 2);
    }

    public BitgoWallet(String scheme, String host, int port, String token, String walletId, String walletPassphrase, Integer numBlocks) {
        this.walletId = walletId;
        this.walletPassphrase = walletPassphrase;
        this.url = new HttpUrl.Builder().scheme(scheme).host(host).port(port).build().toString();
        this.numBlocks = numBlocks;

        ClientConfig config = new ClientConfig();
        config.setHttpReadTimeout(readTimeout);

        config.addDefaultParam(HeaderParam.class, "Authorization", "Bearer " + token);

        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            final CompatSSLSocketFactory socketFactory = new CompatSSLSocketFactory(sslcontext.getSocketFactory());
            config.setSslSocketFactory(socketFactory);
            config.setIgnoreHttpErrorCodes(true);
        }catch(KeyManagementException | NoSuchAlgorithmException e) {
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
            return getResultTxId(api.sendMany(cryptoCurrency.toLowerCase(), this.walletId, request));
        } catch (HttpStatusIOException hse) {
            log.debug("send coins error - HttpStatusIOException, error message: {}, HTTP code: {}, HTTP content: {}", hse.getMessage(), hse.getHttpStatusCode(), hse.getHttpBody());
        } catch (ErrorResponseException e) {
            log.debug("send coins error message: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            final BitGoCoinRequest request = new BitGoCoinRequest(destinationAddress, toSatoshis(amount, cryptoCurrency), walletPassphrase, description, this.numBlocks);
            return getResultTxId(api.sendCoins(cryptoCurrency.toLowerCase(), this.walletId, request));
        } catch (HttpStatusIOException hse) {
            log.debug("send coins error - HttpStatusIOException, error message: {}, HTTP code: {}, HTTP content: {}", hse.getMessage(), hse.getHttpStatusCode(), hse.getHttpBody());
        } catch (ErrorResponseException e) {
            log.debug("send coins error message: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    private int toSatoshis(BigDecimal amount, String cryptoCurrency) {
        switch (CryptoCurrency.valueOfCode(cryptoCurrency)) {
            case BTC:
                return amount.multiply(Converters.BTC).intValue();
            case LTC:
                return amount.multiply(Converters.LTC).intValue();
            case BCH:
                return amount.multiply(Converters.BCH).intValue();
            case ETH:
                return amount.multiply(Converters.ETH).intValue();

            case TBTC:
                return amount.multiply(Converters.TBTC).intValue();
            case TLTC:
                return amount.multiply(Converters.TLTC).intValue();
            case TBCH:
                return amount.multiply(Converters.TBCH).intValue();
            default:
                throw new IllegalArgumentException(cryptoCurrency + " not supported");
        }
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if(cryptoCurrency == null) {
            cryptoCurrency = getPreferredCryptoCurrency();
        }
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        cryptoCurrency = cryptoCurrency.toLowerCase();
        try {
            final Map<String, Object> response = api.getWalletById(cryptoCurrency, walletId);
            if(response == null || response.isEmpty()) {
                return null;
            }

            Object receiveAddressObj = response.get("receiveAddress");
            if(receiveAddressObj == null || !(receiveAddressObj instanceof Map)) {
                return null;
            }

            Map receiveAddressMap = (Map)receiveAddressObj;
            Object addressObj = receiveAddressMap.get("address");
            if(addressObj == null || !(addressObj instanceof String)) {
                return null;
            }
            return (String)addressObj;
        } catch (HttpStatusIOException hse) {
            log.debug("getCryptoAddress error: {}", hse.getHttpBody());
        } catch (ErrorResponseException e) {
            log.debug("getCryptoAddress error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        HashSet<String> s = new HashSet<>();
        s.add(CryptoCurrency.BCH.getCode());
        s.add(CryptoCurrency.BTC.getCode());
        s.add(CryptoCurrency.LTC.getCode());
        s.add(CryptoCurrency.ETH.getCode());

        s.add(CryptoCurrency.TBCH.getCode());
        s.add(CryptoCurrency.TBTC.getCode());
        s.add(CryptoCurrency.TRMG.getCode());
        s.add(CryptoCurrency.TLTC.getCode());
        s.add(CryptoCurrency.TXRP.getCode());
        s.add(CryptoCurrency.TETH.getCode());
        return s;
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
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        cryptoCurrency = cryptoCurrency.toLowerCase();
        try {
            final Map<String, Object> response = api.getWalletById(cryptoCurrency, walletId);
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

            BigDecimal balance = new BigDecimal(balanceObject.toString());
            if (CryptoCurrency.BTC.getCode().equals(cryptoCurrency.toUpperCase())) {
                return balance.divide(Converters.BTC);
            } else if (CryptoCurrency.LTC.getCode().equals(cryptoCurrency.toUpperCase())) {
                return balance.divide(Converters.LTC);
            } else if (CryptoCurrency.BCH.getCode().equals(cryptoCurrency.toUpperCase())) {
                return balance.divide(Converters.BCH);
            } else if (CryptoCurrency.ETH.getCode().equals(cryptoCurrency.toUpperCase())) {
                return balance.divide(Converters.ETH);
            } else if (CryptoCurrency.TBTC.getCode().equals(cryptoCurrency.toUpperCase())) {
                return balance.divide(Converters.TBTC);
            } else if (CryptoCurrency.TLTC.getCode().equals(cryptoCurrency.toUpperCase())) {
                return balance.divide(Converters.TLTC);
            } else if (CryptoCurrency.TBCH.getCode().equals(cryptoCurrency.toUpperCase())) {
                return balance.divide(Converters.TBCH);
            }
            log.error("{} not supported", cryptoCurrency);
            return null;
        } catch (HttpStatusIOException hse) {
            log.debug("getCryptoBalance error: {}", hse.getHttpBody());
        } catch (ErrorResponseException e) {
            log.debug("getCryptoBalance error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("getCryptoBalance error", e);
        }
        return null;
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
