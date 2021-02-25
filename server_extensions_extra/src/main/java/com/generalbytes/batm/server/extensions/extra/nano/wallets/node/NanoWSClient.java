/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.nano.wallets.node;

import com.generalbytes.batm.server.extensions.extra.nano.NanoCurrencySpecification;
import com.generalbytes.batm.server.extensions.payment.IPaymentOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.oczadly.karl.jnano.model.HexData;
import uk.oczadly.karl.jnano.model.NanoAccount;
import uk.oczadly.karl.jnano.model.NanoAmount;
import uk.oczadly.karl.jnano.model.block.StateBlock;
import uk.oczadly.karl.jnano.model.block.StateBlockSubType;
import uk.oczadly.karl.jnano.websocket.NanoWebSocketClient;
import uk.oczadly.karl.jnano.websocket.TopicListener;
import uk.oczadly.karl.jnano.websocket.WsObserver;
import uk.oczadly.karl.jnano.websocket.topic.TopicConfirmation;
import uk.oczadly.karl.jnano.websocket.topic.message.MessageContext;
import uk.oczadly.karl.jnano.websocket.topic.message.TopicMessageConfirmation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Karl Oczadly
 */
public class NanoWSClient {

    private static final long RECONNECT_MS = 250;

    private static final Logger log = LoggerFactory.getLogger(NanoWSClient.class);

    private final NanoCurrencySpecification currencySpec;
    private final URI uri;
    private volatile NanoWebSocketClient client;
    private volatile Thread reconnectThread;
    private final Object connLock = new Object();

    private final ExecutorService listenerExecutor = Executors.newCachedThreadPool();
    private final Map<NanoAccount, DepositListener> blockListeners = new ConcurrentHashMap<>();

    public NanoWSClient(NanoCurrencySpecification currencySpec, URI uri) {
        this.currencySpec = currencySpec;
        this.uri = uri;
        initConnection();
    }


    public void requestPaymentNotifications(Collection<IPaymentOutput> addresses, DepositListener listener) {
        Set<NanoAccount> outputs = parseOutputs(addresses);

        // Register listeners
        for (NanoAccount account : outputs) {
            if (blockListeners.put(account, listener) != null)
                log.warn("Deposit address {} is already in listener map.", account);
        }

        // Update topic filter
        if (isConnected()) {
            client.getTopics().topicConfirmedBlocks().update(
                    new TopicConfirmation.UpdateArgs().addAccountsFilter(new ArrayList<>(outputs)));
        }
    }

    public void endPaymentNotifications(Collection<IPaymentOutput> addresses) {
        Set<NanoAccount> accounts = parseOutputs(addresses);

        // Remove listeners
        accounts.forEach(blockListeners::remove);
        // Remove from topic filter
        if (isConnected()) {
            client.getTopics().topicConfirmedBlocks().update(
                    new TopicConfirmation.UpdateArgs().removeAccountsFilter(new ArrayList<>(accounts)));
        }
    }

    protected Set<NanoAccount> parseOutputs(Collection<IPaymentOutput> addresses) {
        return addresses.stream()
                .map(IPaymentOutput::getAddress)
                .map(currencySpec::parseAddress)
                .collect(Collectors.toSet());
    }

    protected void initTopics() throws InterruptedException {
        client.getTopics().topicConfirmedBlocks().registerListener(new ConfirmedBlockHandler());
        client.getTopics().topicConfirmedBlocks().subscribeBlocking(
                new TopicConfirmation.SubArgs().filterAccounts(new ArrayList<>(blockListeners.keySet())));
    }

    private void initConnection() {
        synchronized (connLock) {
            if (!isConnected() && (reconnectThread == null || !reconnectThread.isAlive())) {
                reconnectThread = new Thread(new ReconnectThreadTask(), "Nano-WS-Reconnection-Thread");
                reconnectThread.setDaemon(true);
                reconnectThread.start();
            }
        }
    }

    public boolean isConnected() {
        return client != null && client.isOpen();
    }


    public interface DepositListener {
        void onDeposit(HexData hash, NanoAmount amount);
    }

    class ConfirmedBlockHandler implements TopicListener<TopicMessageConfirmation> {
        @Override
        public void onMessage(TopicMessageConfirmation message, MessageContext context) {
            log.debug("New block confirmation from websocket ({})", message.getHash());
            if (message.getBlock() instanceof StateBlock) {
                StateBlock sb = (StateBlock)message.getBlock();
                if (sb.getSubType() == StateBlockSubType.SEND) {
                    NanoAccount destAccount = sb.getLink().asAccount();
                    DepositListener listener = blockListeners.get(destAccount);
                    if (listener != null) {
                        log.debug("Notifying DepositListener of new block for account {}", destAccount);
                        listenerExecutor.submit(() -> listener.onDeposit(message.getHash(), message.getAmount()));
                    } else {
                        log.debug("No DepositListener registered.");
                    }
                }
            }
        }
    }

    class SocketStateObserver implements WsObserver {
        @Override
        public void onOpen(int httpStatus) {
            try {
                initTopics();
            } catch (Exception e) {
                log.warn("Failed to initialize websocket topic filters.", e);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            log.warn("Nano node websocket disconnected. Attempting to reconnect in {} ms.", RECONNECT_MS);
            initConnection(); // Launch reconnection thread
        }

        @Override
        public void onSocketError(Exception ex) {
            log.debug("Exception in WebSocket connection", ex);
        }

        @Override
        public void onHandlerError(Exception ex) {
            log.error("Exception in WebSocket handler", ex);
        }
    }

    class ReconnectThreadTask implements Runnable {
        @Override
        public void run() {
            try {
                boolean connected;
                do {
                    Thread.sleep(RECONNECT_MS);
                    connected = doConnect();
                } while (!connected);
            } catch (InterruptedException e) {
                log.warn("Reconnection thread interrupted. Websocket will not reconnect.");
            }
        }

        private boolean doConnect() {
            if (!isConnected()) {
                synchronized (connLock) {
                    if (!isConnected()) {
                        log.info("Attempting to connect to node websocket.");
                        client = new NanoWebSocketClient(uri);
                        client.setObserver(new SocketStateObserver());
                        try {
                            return client.connect();
                        } catch (InterruptedException e) {
                            log.error("Websocket connection attempt interrupted.", e);
                        }
                    }
                }
            }
            return false;
        }
    }

}
