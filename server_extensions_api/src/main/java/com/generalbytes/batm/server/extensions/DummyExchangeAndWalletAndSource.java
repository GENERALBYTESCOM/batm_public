package com.generalbytes.batm.server.extensions;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class DummyExchangeAndWalletAndSource implements IExchange, IWallet, IRateSource {

    private static final BigDecimal EXCHANGE_RATE = new BigDecimal(2000);
    private static final BigDecimal WALLET_BALANCE = new BigDecimal(10);
    private static final BigDecimal EXCHANGE_BALANCE = new BigDecimal(1000);
    private static final String ETH_WALLET_ADDRESS = "0xB009BE55782FD3aDE5fc00624FaBdbba3094F6D2";
    private static final String DASH_WALLET_ADDRESS = "XrAwEffseCKgQPQhYqXuscBaoUnHqkKxQz"; //safe
    private static final String BTC_WALLET_ADDRESS = "18nB5x3zxF26MuA89yNcnkS9qs33KNwLFu";
    private static final String XMR_WALLET_ADDRESS = "dc3c48b1577d25eb4ce56b266bcf7aab6b27c28a0ba305d8dfebff52e6f6f757";
    private static final String LTC_WALLET_ADDRESS = "LZRi2YvS3cR4Pc3hQkAxqYLKRXEjjxZdd5"; //safe
    private static final String TX_SELL_ID = "tx_sell_id";
    private static final String TXT_ID = "txt_id";
    private String fiatCurrency;
    private String cryptoCurrency;
    private String walletAddress;

    private static final Logger log = LoggerFactory.getLogger("batm_public.server_extensions_api.DummyExchangeAndWalletAndSource");

    public DummyExchangeAndWalletAndSource(String fiatCurrency, String cryptoCurrency, String walletAddress) throws IllegalArgumentException {
        if (fiatCurrency == null || cryptoCurrency == null) {
            throw new NullPointerException("Fiat and crypto currency has to be specified.");
        }

        if (cryptoCurrency.equals(CryptoCurrency.BTC.getCode())
            || cryptoCurrency.equals(CryptoCurrency.ETH.getCode())
            || cryptoCurrency.equals(CryptoCurrency.DASH.getCode())
            || cryptoCurrency.equals(CryptoCurrency.XMR.getCode())
            || cryptoCurrency.equals(CryptoCurrency.LTC.getCode())
            || cryptoCurrency.equals(CryptoCurrency.TRTL.getCode())) {
            if (walletAddress != null) {
                throw new IllegalArgumentException("Built-in wallet is used for BTC, LTC, ETH, DASH, XMR, TRTL crypto currencies.");
            }
        } else {
            if (walletAddress == null || "".equals(walletAddress)) {
                throw new IllegalArgumentException("Wallet address has to be specified.");
            }
            this.walletAddress = walletAddress;
        }

        this.cryptoCurrency = cryptoCurrency;
        this.fiatCurrency = fiatCurrency;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(cryptoCurrency);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(fiatCurrency);
        return result;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return fiatCurrency;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        return WALLET_BALANCE;
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        if (this.fiatCurrency.equalsIgnoreCase(fiatCurrency)) {
            return EXCHANGE_BALANCE;
        }else{
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (cryptoCurrency.equalsIgnoreCase(this.cryptoCurrency) && fiatCurrencyToUse.equalsIgnoreCase(this.fiatCurrency)) {
            log.info(String.format("S1%s-DummyExchangeWallet: purchasing coins S2%s", this.cryptoCurrency, amount));
            return "true";
        } else {
            log.error(String.format("S1%s-DummyExchangeWallet: S2%s unsupported currency", this.cryptoCurrency, cryptoCurrency));
            return null;
        }
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return TX_SELL_ID;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        log.info(String.format("S1%s-DummyExchangeWallet: sending coins to S2%s S3%s", this.cryptoCurrency, destinationAddress, amount));
        return TXT_ID;
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        return getAddress(cryptoCurrency);
    }

    private String getAddress(String cryptoCurrency) {
        if (this.cryptoCurrency.equals(cryptoCurrency)) {
            if (CryptoCurrency.BTC.getCode().equals(cryptoCurrency)) {
                return BTC_WALLET_ADDRESS;
            } else if (CryptoCurrency.ETH.getCode().equals(cryptoCurrency)) {
                return ETH_WALLET_ADDRESS;
            } else if (CryptoCurrency.LTC.getCode().equals(cryptoCurrency)) {
                return LTC_WALLET_ADDRESS;
            } else if (CryptoCurrency.DASH.getCode().equals(cryptoCurrency)) {
                return DASH_WALLET_ADDRESS;
            } else if (CryptoCurrency.XMR.getCode().equals(cryptoCurrency)) {
                return XMR_WALLET_ADDRESS;
            } else {
                return walletAddress;
            }
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        log.debug(String.format("S1%s-DummyExchangeWallet: exchange rate is S2%s", this.cryptoCurrency, EXCHANGE_RATE));
        return EXCHANGE_RATE;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        return getAddress(cryptoCurrency);

    }

    @Override
    public String getPreferredCryptoCurrency() {
        return cryptoCurrency;
    }
}
