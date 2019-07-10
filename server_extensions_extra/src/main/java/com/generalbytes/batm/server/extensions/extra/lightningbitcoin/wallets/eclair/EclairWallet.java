package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ILightningWallet;
import com.generalbytes.batm.server.extensions.IWalletInformation;
import com.generalbytes.batm.server.extensions.extra.lightning.ILightningWalletInformation;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.Channel;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.Info;
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
import java.net.ConnectException;
import java.util.List;
import java.util.Set;

public class EclairWallet implements ILightningWallet {

    private static final Logger log = LoggerFactory.getLogger(EclairWallet.class);
    private static final int API_POLL_MAX = 20000;
    private static final int API_POLL_INTERVAL = 1000;
    private static final long API_CALL_DELAY = 100;

    private final ILightningWalletInformation walletInformation;

    private final String url;
    private final EclairAPI api;

    public EclairWallet(String scheme, String host, int port, String password) {
        url = new HttpUrl.Builder().scheme(scheme).host(host).port(port).build().toString();
        final ClientConfig config = new ClientConfig();
        ClientConfigUtil.addBasicAuthCredentials(config, "", password);
        api = RestProxyFactory.createProxy(EclairAPI.class, url, config);

        walletInformation = callChecked(() -> new EclairLightningWalletInformation(api.getInfo().nodeId));
    }

    /**
     * @param destinationAddress
     * @param amount
     * @param cryptoCurrency
     * @param description
     * @return paymentHash
     */
    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {

        Invoice invoice = callChecked(cryptoCurrency, () -> api.parseInvoice(destinationAddress));

        log.info("Paying {} to invoice {}", amount, invoice);

        if (invoice.amount != null) {
            log.info("Invoices with amount not supported");
            return null;
        }

        List<SentInfo> sentInfo = callChecked(() -> api.getSentInfoByPaymentHash(invoice.paymentHash));

        if (sentInfo.stream().filter(i -> i.status == SentInfo.Status.SUCCEEDED).findAny().isPresent()) {
            log.info("Invoice already paid");
            return null;
        }
        Long amountMsat = bitcoinToMSat(amount);
        String paymentId = callChecked(() -> api.payInvoice(destinationAddress, amountMsat));

        for (int millis = 0; millis < API_POLL_MAX; millis += API_POLL_INTERVAL) {
            sleep(API_POLL_INTERVAL);

            SentInfo sentInfo2 = callChecked(() -> api.getSentInfoById(paymentId).get(0));

            switch (sentInfo2.status) {
                case FAILED:
                    log.error("Payment failed: id={}, paymentHash={}", sentInfo2.id, sentInfo2.paymentHash);
                    return null;
                case SUCCEEDED:
                    return sentInfo2.paymentHash;
            }
        }
        log.error("Payment result unknown; paymentId={}", paymentId);
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, BigDecimal fee, String cryptoCurrency, String description) {
        return sendCoins(destinationAddress, amount, cryptoCurrency, description);
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        return callChecked(cryptoCurrency, () -> {
            Info info = api.getInfo();
            log.info("Node ID: {}", info.nodeId);
            return info.nodeId;
        });
    }

    @Override
    public String getInvoice(BigDecimal cryptoAmount, String cryptoCurrency, Long paymentValidityInSec, String description) {
        return callChecked(cryptoCurrency, () -> api.createInvoice(bitcoinToMSat(cryptoAmount), description, paymentValidityInSec).serialized);
    }


    @Override
    public Set<String> getCryptoCurrencies() {
        return null;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return null;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        return callChecked(cryptoCurrency, () -> {
            List<Channel> channels = api.getChannels();

            channels.stream().map(ch -> ch.channelId + " " + ch.state
                + " Can send msat: " + ch.data.commitments.localCommit.spec.toLocalMsat
                + " Can receive msat: " + ch.data.commitments.localCommit.spec.toRemoteMsat
                + " Capacity sat: " + ch.data.commitments.commitInput.amountSatoshis
            ).forEach(log::info);

            return mSatToBitcoin(channels.stream()
                .mapToLong(ch -> ch.data.commitments.localCommit.spec.toLocalMsat)
                .max().orElse(0l));
        });
    }

    /**
     * @param destinationAddress
     * @param cryptoCurrency
     * @return paymentHash
     */
    public BigDecimal getReceivedAmount(String destinationAddress, String cryptoCurrency) {
        return callChecked(cryptoCurrency, () -> {
            try {
                return mSatToBitcoin(api.getReceivedInfoByInvoice(destinationAddress).amountMsat);
            } catch (ErrorResponseException e) {
                if (e.error.equals("Not found")) {
                    return BigDecimal.ZERO;
                }
                throw e;
            }
        });
    }

    private BigDecimal mSatToBitcoin(Long amountMsat) {
        return new BigDecimal(amountMsat).movePointLeft(8 + 3);
    }

    private Long bitcoinToMSat(BigDecimal amount) {
        return amount.movePointRight(8 + 3).longValue();
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
        return walletInformation;
    }


    /**
     * calls the supplier, logs the errors and returns null in case of exceptions
     *
     * @param supplier
     * @param <T>
     * @return null in case of exceptions
     */
    private <T> T callChecked(EclairApiSupplier<T> supplier) {
        return callChecked(supplier, 3);
    }

    private <T> T callChecked(EclairApiSupplier<T> supplier, int retries) {
        try {
            sleep(API_CALL_DELAY);
            return supplier.get();
        } catch (ErrorResponseException e) {
            log.error("Error response: {}", e.error);

            // workaround https://github.com/ACINQ/eclair/pull/1032
            if (e.error.endsWith("was malformed:\nSubstream Source cannot be materialized more than once") && retries > 0) {
                log.info("retrying {}", retries);
                return callChecked(supplier, retries - 1);
            }

        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody(), e);
        } catch (ConnectException e) {
            log.error("Cannot connect. URL: '{}'", url, e);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    private <T> T callChecked(String cryptoCurrency, EclairApiSupplier<T> supplier) {
        return callChecked(() -> {
            if (!CryptoCurrency.LBTC.getCode().equals(cryptoCurrency)) {
                throw new IllegalArgumentException(cryptoCurrency + " not supported");
            }
            return supplier.get();
        });
    }
}