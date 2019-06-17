package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWalletAdvanced;
import com.generalbytes.batm.server.extensions.IWalletInformation;
import com.generalbytes.batm.server.extensions.extra.lightning.ILightningWalletInformation;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.Invoice;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.SentInfo;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.ClientConfigUtil;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public class EclairWallet implements IWalletAdvanced {

    private static final Logger log = LoggerFactory.getLogger(EclairWallet.class);
    public static final int API_WAIT_MAX = 20000;
    public static final int API_WAIT_INTERVAL = 500;

    private final EclairAPI api;

    public EclairWallet(String scheme, String host, int port, String password) {
        String url = new HttpUrl.Builder().scheme(scheme).host(host).port(port).build().toString();
        final ClientConfig config = new ClientConfig();
        ClientConfigUtil.addBasicAuthCredentials(config, "", password);
        api = RestProxyFactory.createProxy(EclairAPI.class, url, config);
    }

    /**
     *
     * @param destinationAddress
     * @param amount
     * @param cryptoCurrency
     * @param description
     * @return paymentHash
     */
    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!CryptoCurrency.LBTC.getCode().equalsIgnoreCase(cryptoCurrency)) {
            log.error("{} not supported", cryptoCurrency);
            return null;
        }

        try {
            Invoice invoice = api.parseInvoice(destinationAddress);

            log.info("Invoice: {}", invoice);

            if (invoice.amount != null) {
                log.info("Invoices with amount not supported");
                return null;
            }

            List<SentInfo> sentInfo = api.getSentInfoByPaymentHash(invoice.paymentHash);

            if (sentInfo.stream().filter(i -> i.status == SentInfo.Status.SUCCEEDED).findAny().isPresent()) {
                log.info("Invoice already paid");
                return null;
            }

            BigInteger amountMsat = amount.movePointRight(8 + 3).toBigInteger();

            String id = api.payInvoice(destinationAddress, amountMsat);

            for (int millis = 0; millis < API_WAIT_MAX; millis += API_WAIT_INTERVAL) {
                sleep(API_WAIT_INTERVAL);

                SentInfo sentInfo2 = api.getSentInfoById(id).get(0);

                switch (sentInfo2.status) {
                    case FAILED:
                        log.error("Payment failed: id={}, paymentHash={}", sentInfo2.id, sentInfo2.paymentHash);
                        return null;
                    case SUCCEEDED:
                        return sentInfo2.paymentHash;
                }
            }

        } catch (ErrorResponseException e) {
            log.error("Error: {}", e.error);
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody(), e);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, BigDecimal fee, String cryptoCurrency, String description) {
        return null; // TODO
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        return null; // FIXME
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return null; // TODO remove from interface?
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return null; // TODO remove from interface?
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!CryptoCurrency.LBTC.getCode().equalsIgnoreCase(cryptoCurrency)) {
            log.error("{} not supported", cryptoCurrency);
            return null;
        }

        try {
            return new BigDecimal(api.getChannels().stream()
                .map(ch -> ch.data.commitments.localCommit.spec.toLocalMsat)
                .reduce(BigInteger.ZERO, BigInteger::add)); // TODO or max?

        } catch (ErrorResponseException e) {
            log.error("Error: {}", e.error);
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody(), e);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.trace("", e);
        }
    }


    @Override
    public IWalletInformation getWalletInformation() {
        return new ILightningWalletInformation() {
            @Override
            public String getPubKey() {
                return null; // TODO
            }
        };
    }
}
