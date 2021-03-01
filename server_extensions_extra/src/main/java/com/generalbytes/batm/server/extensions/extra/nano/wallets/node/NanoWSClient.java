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

/**
 * @author Karl Oczadly
 */
public class NanoWSClient {

    private static final long RECONNECT_MS = 250;
    private static final long TOPIC_UPDATE_TIMEOUT_MS = 5000;

    private static final Logger log = LoggerFactory.getLogger(NanoWSClient.class);

    private final NanoCurrencySpecification currencySpec;
    private final URI uri;
    private volatile NanoWebSocketClient client;
    private volatile Thread reconnectThread;
    private final Object connLock = new Object();
    private volatile boolean initializedSuccessfully; // False if couldn't subscribe to topics

    private final ExecutorService listenerExecutor = Executors.newCachedThreadPool();
    private final Map<NanoAccount, DepositListener> blockListeners = new ConcurrentHashMap<>();

    public NanoWSClient(NanoCurrencySpecification currencySpec, URI uri) {
        this.currencySpec = currencySpec;
        this.uri = uri;
        initConnection();
    }


    public boolean requestPaymentNotifications(Collection<IPaymentOutput> addresses, DepositListener listener) {
        Set<NanoAccount> outputs = currencySpec.parsePaymentOutputs(addresses);

        // Register listeners
        for (NanoAccount account : outputs) {
            if (blockListeners.put(account, listener) != null)
                log.warn("Deposit address {} is already in listener map.", account);
        }

        // Update topic filter
        if (isOpen()) {
            try {
                boolean updated = client.getTopics().topicConfirmedBlocks().updateBlocking(TOPIC_UPDATE_TIMEOUT_MS,
                        new TopicConfirmation.UpdateArgs().addAccountsFilter(new ArrayList<>(outputs)));

                if (updated) {
                    log.debug("Updated confirmation topic filter.");
                } else {
                    log.warn("Didn't receive ACK when subscribing to confirmations topic.");
                    return false;
                }
            } catch (Exception e) {
                log.warn("Couldn't add accounts to topic filter.", e);
                return false;
            }
        }
        return true; // Will attempt to subscribe when WS opens
    }

    public void endPaymentNotifications(Collection<IPaymentOutput> addresses) {
        Set<NanoAccount> accounts = currencySpec.parsePaymentOutputs(addresses);

        // Remove listeners
        accounts.forEach(blockListeners::remove);
        // Remove from topic filter
        if (isOpen()) {
            try {
                client.getTopics().topicConfirmedBlocks().update(new TopicConfirmation.UpdateArgs()
                    .removeAccountsFilter(new ArrayList<>(accounts)));
            } catch (Exception e) {
                log.warn("Couldn't remove accounts from topic filter.", e);
            }
        }
    }

    protected void initTopics() throws InterruptedException {
        // Register handler
        client.getTopics().topicConfirmedBlocks().registerListener(new ConfirmedBlockHandler());

        // Subscribe to block confirmations
        boolean subscribed;
        do {
            subscribed = client.getTopics().topicConfirmedBlocks().subscribeBlocking(TOPIC_UPDATE_TIMEOUT_MS,
                    new TopicConfirmation.SubArgs().filterAccounts(new ArrayList<>(blockListeners.keySet())));
            if (!subscribed)
                log.warn("Didn't receive ACK when subscribing to confirmations topic. Retrying...");
        } while (!subscribed);
        log.debug("Subscribed to topic.");
    }

    private void initConnection() {
        synchronized (connLock) {
            if (!isOpen() && (reconnectThread == null || !reconnectThread.isAlive())) {
                initializedSuccessfully = false;
                reconnectThread = new Thread(new ReconnectThreadTask(), "Nano-WS-Reconnection-Thread");
                reconnectThread.setDaemon(true);
                reconnectThread.start();
            }
        }
    }

    /** True if open and initialized successfully. */
    public boolean isActive() {
        return isOpen() && initializedSuccessfully;
    }

    /** True if websocket has an open connection. */
    private boolean isOpen() {
        return client != null && client.isOpen();
    }


    public interface DepositListener {
        void onDeposit(HexData hash);
    }

    class ConfirmedBlockHandler implements TopicListener<TopicMessageConfirmation> {
        @Override
        public void onMessage(TopicMessageConfirmation message, MessageContext context) {
            log.debug("New block confirmation from websocket ({})", message.getHash());
            if (message.getBlock() instanceof StateBlock) {
                StateBlock sb = (StateBlock)message.getBlock();

                // Find target account
                NanoAccount account = null;
                if (sb.getSubType() == StateBlockSubType.SEND) {
                    account = sb.getLink().asAccount(); // Recipient (link)
                } else if (sb.getSubType() == StateBlockSubType.RECEIVE) {
                    account = sb.getAccount(); // Block owner
                }

                if (account != null) {
                    // Notify listeners
                    DepositListener listener = blockListeners.get(account);
                    if (listener != null) {
                        log.debug("Notifying DepositListener of new block {}", message.getHash());
                        listenerExecutor.submit(() -> listener.onDeposit(message.getHash()));
                    } else {
                        log.warn("No DepositListener registered for account {} but received notification.", account);
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
                initializedSuccessfully = true;
            } catch (Exception e) {
                log.warn("Failed to initialize websocket topic filters.", e);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            log.warn("Nano node websocket disconnected. Attempting to reconnect in {} ms.", RECONNECT_MS);
            initializedSuccessfully = false;
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
            if (!isOpen()) {
                synchronized (connLock) {
                    if (!isOpen()) {
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
