package com.generalbytes.batm.server.extensions.extra.nano.dev;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.nano.util.NanoUtil;
import com.generalbytes.batm.server.extensions.extra.nano.NanoExtensionContext;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoRpcClient;
import com.generalbytes.batm.server.extensions.extra.nano.wallet.node.NanoNodeWallet;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.junit.Ignore;

import java.math.BigDecimal;
import java.net.URL;
import java.util.UUID;

/**
 * THIS CLASS MAY BE IGNORED.
 * Its only purpose is to test and help during development.
 */
@Ignore
public class TestNodeWallet {

    public static void main(String[] args) throws Exception {
        NanoRpcClient rpcClient = new NanoRpcClient(new URL("http://[::1]:7076"));
        String walletId = "C6DFB1E6B2AAA97247BA5A434BB2795F5FC4D68EE0FBEDCD21C72027880596C7";
        String walletAccount = "nano_3h5r5huudbj3mrmosha84oregs3k9wgi8cwbynbiajjmto1y9sys8yykjg1m";
        String destAccount = "nano_3zykdut8t1hekoty3nh6fhqnkekdstbd7f15irpobpp8yd36jc38a6ewquwy";

        NanoExtensionContext context = new NanoExtensionContext(CryptoCurrency.NANO, null, NanoUtil.NANO);
        NanoNodeWallet wallet = new NanoNodeWallet(context, rpcClient, null, walletId, walletAccount);

        ReceivedAmount received = wallet.getReceivedAmount(destAccount, context.getCurrencyCode());
        System.out.printf("Received amount of %s = %.8f (%d confirmations)%n",
                destAccount, received.getTotalAmountReceived(), received.getConfirmations());

        System.out.printf("Balance of hot wallet = %.8f%n", wallet.getCryptoBalance(context.getCurrencyCode()));

        String hash = wallet.sendCoins(destAccount, new BigDecimal("0.001"), context.getCurrencyCode(),
                UUID.randomUUID().toString());
        if (hash == null) {
            System.out.println("Failed to send funds.");
        } else {
            System.out.printf("Sent 0.001 Nano to %s, hash = %s%n", destAccount, hash);
        }
    }

}
