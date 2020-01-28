package com.generalbytes.batm.server.extensions.extra.common;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.payment.IBlockchainWatcherTransactionListener;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class RPCBlockchainWatcherTest {
    private static final Logger log = LoggerFactory.getLogger(RPCBlockchainWatcherTest.class);

    @Ignore
    @Test
    public void test() {
        try {
            RPCClient rpcClient = new RPCClient(CryptoCurrency.BCH.getCode(), "...");
            final RPCBlockchainWatcher w = new RPCBlockchainWatcher(rpcClient);
            IBlockchainWatcherTransactionListener tlistener = new IBlockchainWatcherTransactionListener() {
                @Override
                public void removedFromWatch(String cryptoCurrency, String transactionHash) {
                    log.info("Removed from Watch");
                }

                @Override
                public void numberOfConfirmationsChanged(String cryptoCurrency, String transactionHash, int numberOfConfirmations) {
                    log.info("numberOfConfirmationsChanged " + transactionHash + " = " + numberOfConfirmations);
                }

                @Override
                public void newBlockMined(String cryptoCurrency, String transactionHash, long blockHeight) {

                }
            };
            w.addAddress(CryptoCurrency.BCH.getCode(), "qzezfqhxej3nyz3t5pq3vzmhazgkgns5qcvyul5cqj", (cryptoCurrency, address, transactionId, confirmations) -> {
                log.info("New transaction " + transactionId + " seen on address " + address + " confirmations: " + confirmations);
                w.addTransaction(cryptoCurrency, transactionId, tlistener);
            });
            w.start();
            Thread.sleep(50000000);
            w.stop();
        } catch (InterruptedException e) {
            log.error("Error", e);
        } catch (MalformedURLException e) {
            log.error("Error", e);
        }
    }
}