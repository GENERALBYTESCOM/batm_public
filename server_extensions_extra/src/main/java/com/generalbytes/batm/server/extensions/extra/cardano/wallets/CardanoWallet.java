package com.generalbytes.batm.server.extensions.extra.cardano.wallets;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.Amount;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.Balance;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.CreateTransactionRequest;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.Transaction;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.Wallet;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.Payment;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.WalletAddress;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CardanoWallet implements IWallet {
    private static final Logger log = LoggerFactory.getLogger(CardanoWallet.class);
    private static final String CRYPTO_CURRENCY = CryptoCurrency.ADA.getCode();
    private static final int ADA_TO_LOVELACE_SCALE = 6; //Used to convert BigDecimal amount to int amount and back

    private final CardanoWalletApi api;
    private final String walletId;
    private final String passphrase;

    public CardanoWallet(String protocol, String host, int port, String walletId, String passphrase) {
        String url = new HttpUrl.Builder().scheme(protocol).host(host).port(port).build().toString();
        api = RestProxyFactory.createProxy(CardanoWalletApi.class, url);
        this.walletId = walletId;
        this.passphrase = passphrase;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Cardano wallet error: unknown cryptocurrency: {}", cryptoCurrency);
            return null;
        }
        try {
            List<WalletAddress> walletAddresses = api.getWalletAddresses(walletId);
            return walletAddresses.isEmpty() ? null : walletAddresses.get(0).getId();
        } catch (HttpStatusIOException hse) {
            log.debug("getCryptoAddress - error HTTP code: {}, HTTP content: {}", hse.getHttpStatusCode(), hse.getHttpBody());
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CRYPTO_CURRENCY);
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CRYPTO_CURRENCY;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Cardano wallet error: unknown cryptocurrency: {}", cryptoCurrency);
            return null;
        }
        try {
            Wallet wallet = api.getWallet(walletId);
            Balance balance = wallet.getBalance();
            Long quantity = balance.getAvailable().getQuantity();
            return BigDecimal.valueOf(quantity).movePointLeft(ADA_TO_LOVELACE_SCALE);
        } catch (HttpStatusIOException hse) {
            log.debug("getCryptoBalance - error HTTP code: {}, HTTP content: {}", hse.getHttpStatusCode(), hse.getHttpBody());
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            log.info("sendCoins - Cardano sending coins to: {}, amount: {}", destinationAddress, amount);
            Transaction transaction = api.createTransaction(walletId, createTransactionRequest(destinationAddress, amount));
            log.debug("result = {}", transaction);
            return transaction.getId();
        } catch (HttpStatusIOException hse) {
            log.debug("sendCoins - error HTTP code: {}, HTTP content: {}", hse.getHttpStatusCode(), hse.getHttpBody());
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    private CreateTransactionRequest createTransactionRequest(String destinationAddress, BigDecimal quantity) {
        quantity = quantity.setScale(ADA_TO_LOVELACE_SCALE, RoundingMode.FLOOR);
        long quantityInLovelace = quantity.movePointRight(ADA_TO_LOVELACE_SCALE).longValue();

        Amount amount = new Amount();
        amount.setQuantity(quantityInLovelace);
        amount.setUnit("lovelace");

        Payment payment = new Payment();
        payment.setAddress(destinationAddress);
        payment.setAmount(amount);

        CreateTransactionRequest requestBody = new CreateTransactionRequest();
        requestBody.setPassphrase(passphrase);
        requestBody.setPayments(Collections.singletonList(payment));

        return requestBody;
    }
}
