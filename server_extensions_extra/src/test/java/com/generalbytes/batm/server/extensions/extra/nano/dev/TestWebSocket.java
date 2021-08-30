package com.generalbytes.batm.server.extensions.extra.nano.dev;

import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoWsClient;
import org.junit.Ignore;

import java.net.URI;

/**
 * THIS CLASS MAY BE IGNORED.
 * Its only purpose is to test and help during development.
 */
@Ignore
public class TestWebSocket {

    public static void main(String[] args) throws Exception {
        NanoWsClient wsClient = new NanoWsClient(new URI("ws://[::1]:7078"));

        Thread.sleep(2500);

        wsClient.addDepositWatcher(
                "nano_3h5r5huudbj3mrmosha84oregs3k9wgi8cwbynbiajjmto1y9sys8yykjg1m",
                () -> System.out.println("onDeposit() CALLED"));

        Thread.currentThread().join();
    }

}
