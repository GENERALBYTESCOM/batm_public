/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.common;

import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;

import com.generalbytes.batm.server.extensions.payment.IBlockchainWatcher;
import com.generalbytes.batm.server.extensions.payment.IBlockchainWatcherAddressListener;
import com.generalbytes.batm.server.extensions.payment.IBlockchainWatcherTransactionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RPCBlockchainWatcher implements IBlockchainWatcher{

    private static final Logger log = LoggerFactory.getLogger("batm.master.RPCBlockchainWatcher");

    private static final long WATCH_PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(10);
    private static final long PERIOD_BETWEEN_CALLS_MILLIS = TimeUnit.SECONDS.toMillis(2);

    private final List<TransactionWatchRecord> trecords = new LinkedList<>();
    private final List<AddressWatchRecord> arecords = new LinkedList<>();
    private Thread workerThread = null;
    private RPCClient rpcClient;

    public RPCBlockchainWatcher(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public void addTransaction(String cryptoCurrency, String txId, IBlockchainWatcherTransactionListener l) {
        synchronized (trecords) {
            BitcoindRpcClient.Transaction transaction = null;
            for (int i=0;i<60;i++) {
                try {
                    transaction = rpcClient.getTransaction(txId);
                    log.debug("Transaction " + txId + " recognized by wallet.");
                } catch (BitcoinRPCException e) {
                    if (i == 59) {
                        log.error("Error", e);
                    }else{
                        log.warn("Transaction " + txId + " is not recognized by wallet.");
                    }
                    try {
                        Thread.sleep(1000); //wait one second - sometimes it takes few seconds for wallet to find its transaction
                    } catch (InterruptedException e1) {
                        log.error("", e1);
                    }
                }
                if (transaction != null) {
                    break;
                }
            }
            if (transaction != null) {
                TransactionWatchRecord t = new TransactionWatchRecord(cryptoCurrency, txId, l, transaction.confirmations());
                trecords.add(t);
            }else{
                log.error("Error: For some reason transaction " + txId + " was not recognized by wallet.");
            }
        }
    }

    public void removeTransaction(String transactionHash) {
        synchronized (trecords) {
            for (int i = 0; i < trecords.size(); i++) {
                TransactionWatchRecord record = trecords.get(i);
                if (record.getTransactionHash().equals(transactionHash)) {
                    trecords.remove(record);
                    record.getListener().removedFromWatch(record.getCryptoCurrency(), record.getTransactionHash());
                    return;
                }
            }
        }
    }

    @Override
    public void removeTransactions(IBlockchainWatcherTransactionListener listener) {
        synchronized (trecords) {
            for (int i = 0; i < trecords.size(); ) {
                TransactionWatchRecord record = trecords.get(i);
                if (record.getListener() == listener) {
                    trecords.remove(record);
                    record.getListener().removedFromWatch(record.getCryptoCurrency(), record.getTransactionHash());
                } else {
                    i++;
                }
            }
        }
    }

    public synchronized void start() {
        if (workerThread != null) {
            workerThread.interrupt();
            workerThread = null;
        }
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        });
        workerThread.setName("RPCBlockchainWatcher");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    private void doWork() {
        long lastBlockChainHeight = -1;

        log.debug("doWork - Starting RPCBlockchainWatcher...");
        try {
            long currentBlockChainHeight;
            while (workerThread != null) {
                //Check transactions
                if (trecords.size() > 0) {
                    currentBlockChainHeight = rpcClient.getBlockCount();
                    if (currentBlockChainHeight != lastBlockChainHeight && currentBlockChainHeight != 0) {
                        lastBlockChainHeight = currentBlockChainHeight;
                        log.debug("doWork - Last block mined: " + currentBlockChainHeight);
                        List<TransactionWatchRecord> res;
                        synchronized (trecords) {
                            res = new LinkedList<>(this.trecords);
                        }
                        if (!res.isEmpty()) {
                            List<TransactionWatchRecord> res2 = new LinkedList<>();
                            for (TransactionWatchRecord record : res) {
                                // prioritize transactions with small number of confirmations
                                if (record.getLastNumberOfConfirmations() < 3) {
                                    checkTransactionRecord(record, currentBlockChainHeight);
                                    Thread.sleep(PERIOD_BETWEEN_CALLS_MILLIS);
                                } else {
                                    res2.add(record);
                                }
                            }
                            for (TransactionWatchRecord record : res2) {
                                checkTransactionRecord(record, currentBlockChainHeight);
                                Thread.sleep(PERIOD_BETWEEN_CALLS_MILLIS);
                            }
                        }
                    }
                }
                //Check wallets
                if (arecords.size() > 0) {
                    List<RPCClient.ReceivedAddress> receivedAddresses = rpcClient.listReceivedByAddress2(0);
                    for (int i = 0; i < receivedAddresses.size(); i++) {
                        RPCClient.ReceivedAddress ra = receivedAddresses.get(i);
                        for (AddressWatchRecord arecord : arecords) {
                            if (arecord.getAddress().equals(ra.address())) {
                                //address is watched
                                List<String> newTxIds = new ArrayList<>();
                                if (arecord.getLastTransactionIds() == null || arecord.getLastTransactionIds().isEmpty()) {
                                    newTxIds.addAll(ra.txids());
                                }else{
                                    //Lets compare what we already know of and what is new.
                                    newTxIds.addAll(removeKnownTxIds(ra.txids(), arecord.getLastTransactionIds()));
                                }
                                arecord.getLastTransactionIds().addAll(ra.txids());
                                for (String newTxId : newTxIds) {
                                    IBlockchainWatcherAddressListener listener = arecord.getListener();
                                    if (listener != null) {
                                        BitcoindRpcClient.Transaction transaction = rpcClient.getTransaction(newTxId);
                                        listener.newTransactionSeen(arecord.getCryptoCurrency(), arecord.getAddress(), newTxId, transaction.confirmations());
                                    }
                                }
                            }
                        }
                    }
                }
                Thread.sleep(WATCH_PERIOD_MILLIS);
            }
        } catch (InterruptedException e) {
            log.error("Error", e);
        } catch (BitcoinRPCException e) {
            log.error("Error", e);
        }
    }

    private List<String> removeKnownTxIds(List<String> whereToSearch, List<String> whatToSearch) {
        List<String> result = new ArrayList<>(whereToSearch);
        result.removeAll(whatToSearch);
        return result;
    }

    private void checkTransactionRecord(TransactionWatchRecord record, long currentBlockChainHeight) throws BitcoinRPCException {
        String txHash = record.getTransactionHash();
        if (record.getListener() != null) {
            record.getListener().newBlockMined(record.getCryptoCurrency(), txHash, currentBlockChainHeight);
        }
        BitcoindRpcClient.Transaction t = rpcClient.getTransaction(txHash);
        long transactionHeight = -1;
        if (t != null) {
            String blockHash = t.blockHash();
            if (blockHash != null) {
                transactionHeight = rpcClient.getBlock(blockHash).height();
            }
        }
        boolean numberOfConfirmationsChanged = false;
        if (transactionHeight > 0) {
            //transaction is in block
            int numberOfConfirmations = 1 + (int)(currentBlockChainHeight - transactionHeight);
            if (numberOfConfirmations > record.getLastNumberOfConfirmations()) {
                record.setLastNumberOfConfirmations(numberOfConfirmations);
                log.debug("checkTransactionRecord - Number of confirmations for tx " + txHash + " is now " + numberOfConfirmations);
                if (record.getListener() != null) {
                    record.getListener().numberOfConfirmationsChanged(record.getCryptoCurrency(), txHash, numberOfConfirmations);
                    numberOfConfirmationsChanged = true;
                }
            }
        }
        if (!numberOfConfirmationsChanged) {
            log.debug("checkTransactionRecord - Number of confirmations for tx " + txHash + " is not changed. transactionHeight = " + transactionHeight + ", currentBlockChainHeight = " + currentBlockChainHeight);
        }
    }

    @SuppressWarnings("all")
    public void stop() {
        log.debug("stop - Stopping RPCBlockchainWatcher...");
        if (workerThread != null) {
            workerThread.interrupt();
            workerThread = null;
        }
    }

    @Override
    public void addAddress(String cryptoCurrency, String address, IBlockchainWatcherAddressListener listener) {
        synchronized (arecords) {
            arecords.add(new AddressWatchRecord(cryptoCurrency, address,listener));
        }
    }

    @Override
    public void removeAddress(String cryptoCurrency, String address) {
        synchronized (arecords) {
            for (int i = 0; i < arecords.size(); i++) {
                AddressWatchRecord record = arecords.get(i);
                if (record.getAddress().equals(address)) {
                    arecords.remove(i);
                    return;
                }
            }
        }
    }

    @Override
    public void removeAddresses(IBlockchainWatcherAddressListener listener) {
        ArrayList<AddressWatchRecord> listOfToBeRemoved = new ArrayList<>();
        synchronized (arecords) {
            for (int i = 0; i < arecords.size(); i++) {
                AddressWatchRecord record = arecords.get(i);
                if (record.getListener() == listener) {
                    listOfToBeRemoved.add(record);
                }
            }
            for (AddressWatchRecord addrToBeRemoved : listOfToBeRemoved) {
                arecords.remove(addrToBeRemoved);
            }
        }
    }
}
