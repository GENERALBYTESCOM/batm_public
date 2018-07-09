package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2;

import com.generalbytes.batm.server.extensions.Converters;
import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCoinRequest;
import com.generalbytes.batm.server.extensions.extra.worldcoin.sources.cd.CompatSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BitgoWallet implements IWallet {

    private static final Logger log = LoggerFactory.getLogger(BitgoWallet.class);

    private final IBitgoAPI api;

    private String apiKey;
    private String accessToken;
    private String walletId;
    private String walletPassphrase;
    private String url;
    private static final Integer readTimeout = 90 * 1000; //90 seconds

    public BitgoWallet(String host, String port, String token, String walletId, String walletPassphrase) {
        this.apiKey = token;
        this.accessToken = "Bearer " + token;
        this.walletId = walletId;
        this.walletPassphrase = walletPassphrase;
        this.url = createUrl(host, port);

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

        api = RestProxyFactory.createProxy(IBitgoAPI.class, this.url, config);
    }

    private String createUrl(String host, String port) {
        String url = host;
        if(port != null && !port.equalsIgnoreCase("") && !port.equalsIgnoreCase(" ")) {
            url = host + ":" + port;
        }
        url = url + "/api";
        if(url.startsWith("https://") || url.startsWith("http://")) {
            return url;
        } else {
            url = "http://" + url;
        }
        return url;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        String status = null;
        final BitGoCoinRequest request = new BitGoCoinRequest(destinationAddress, toSatoshis(amount, cryptoCurrency), walletPassphrase);
        try {
            Map<String, String> result = api.sendCoins(accessToken, "application/json", cryptoCurrency.toLowerCase(), this.walletId, request);
            if (result == null) {
                log.debug("send coins result is null");
                return null;
            }

            status = result.get("status");
        } catch (UndeclaredThrowableException ute) {
            HttpStatusIOException hse = (HttpStatusIOException)ute.getUndeclaredThrowable();
            String body = hse.getHttpBody();
            status = "ERROR";
            String errorMessage = ExtensionsUtil.getErrorMessage(body);
            log.debug("send coins error message = [" + errorMessage +"] ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.debug("send coins status = {}", status);
        return status;
    }

    private int toSatoshis(BigDecimal amount, String cryptoCurrency) {
        switch(cryptoCurrency) {
            case Currencies.BTC: return amount.multiply(Converters.BTC).intValue();
            case Currencies.LTC: return amount.multiply(Converters.LTC).intValue();
            case Currencies.BCH: return amount.multiply(Converters.BCH).intValue();

            case Currencies.TBTC: return amount.multiply(Converters.TBTC).intValue();
            case Currencies.TLTC: return amount.multiply(Converters.TLTC).intValue();
            case Currencies.TBCH: return amount.multiply(Converters.TBCH).intValue();
            default: return amount.multiply(new BigDecimal(1)).intValue();
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
            final Map<String, Object> response = api.getWalletById(accessToken, cryptoCurrency, walletId);
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
        }catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        HashSet<String> s = new HashSet<String>();
        s.add(Currencies.BCH);
        s.add(Currencies.BTC);
        s.add(Currencies.LTC);

        s.add(Currencies.TBCH);
        s.add(Currencies.TBTC);
        s.add(Currencies.TRMG);
        s.add(Currencies.TLTC);
        s.add(Currencies.TXRP);
        s.add(Currencies.TETH);
        return s;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return Currencies.BTC;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if(cryptoCurrency == null) {
            cryptoCurrency = getPreferredCryptoCurrency();
        }
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        cryptoCurrency = cryptoCurrency.toLowerCase();
        try {
            final Map<String, Object> response = api.getWalletById(accessToken, cryptoCurrency, walletId);
            if(response == null || response.isEmpty()) {
                return null;
            }

            Object balanceObject = response.get("balance");
            if(balanceObject == null || !(balanceObject instanceof Integer)) {
                return null;
            }

            Integer balance = (Integer)balanceObject;
            switch(cryptoCurrency.toUpperCase()) {
                case Currencies.BTC: return BigDecimal.valueOf(balance.intValue()).divide(Converters.BTC);
                case Currencies.LTC: return BigDecimal.valueOf(balance.intValue()).divide(Converters.LTC);
                case Currencies.BCH: return BigDecimal.valueOf(balance.intValue()).divide(Converters.BCH);

                case Currencies.TBTC: return BigDecimal.valueOf(balance.intValue()).divide(Converters.TBTC);
                case Currencies.TLTC: return BigDecimal.valueOf(balance.intValue()).divide(Converters.TLTC);
                case Currencies.TBCH: return BigDecimal.valueOf(balance.intValue()).divide(Converters.TBCH);
                default: return BigDecimal.valueOf(balance.intValue()).divide(new BigDecimal(1));
            }
        }catch (Exception e) {
            log.error("", e);
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
