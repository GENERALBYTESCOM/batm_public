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
package com.generalbytes.batm.server.extensions.extra.tron;

import com.generalbytes.batm.server.extensions.IWallet;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.trident.abi.FunctionReturnDecoder;
import org.tron.trident.abi.TypeReference;
import org.tron.trident.abi.datatypes.Address;
import org.tron.trident.abi.datatypes.Bool;
import org.tron.trident.abi.datatypes.Function;
import org.tron.trident.abi.datatypes.generated.Uint256;
import org.tron.trident.core.ApiWrapper;
import org.tron.trident.core.exceptions.IllegalException;
import org.tron.trident.crypto.SECP256K1;
import org.tron.trident.crypto.tuwenitypes.Bytes32;
import org.tron.trident.proto.Chain;
import org.tron.trident.proto.Response;
import org.tron.trident.utils.Base58Check;
import org.tron.trident.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class TRC20Wallet implements IWallet {
    private static final Logger log = LoggerFactory.getLogger(TRC20Wallet.class);
    private static final long FEE_LIMIT = 10_000_000; // Maximum TRX consumption, measured in SUN (1 TRX = 1,000,000 SUN).
    private static final long BLOCK_TIME_SECONDS = 3;

    private final String tronProApiKey;
    private final String hexPrivateKey;
    private final String tokenSymbol;
    private final int tokenDecimalPlaces;
    private final String contractAddress;
    private final Set<String> cryptoCurrencies;
    private final String walletAddress;

    public TRC20Wallet(String tronProApiKey, String hexPrivateKey, String tokenSymbol, int tokenDecimalPlaces, String contractAddress) {
        this.tronProApiKey = Objects.requireNonNull(tronProApiKey, "tronProApiKey must not be null");
        this.hexPrivateKey = Objects.requireNonNull(hexPrivateKey, "hexPrivateKey must not be null");
        this.tokenSymbol = Objects.requireNonNull(tokenSymbol, "tokenSymbol must not be null");
        this.tokenDecimalPlaces = tokenDecimalPlaces;
        this.contractAddress = Objects.requireNonNull(contractAddress, "contractAddress must not be null");

        cryptoCurrencies = Collections.singleton(tokenSymbol);
        walletAddress = getAddress(hexPrivateKey);
    }

    private class AutoClosingApiWrapper implements AutoCloseable {
        private final ApiWrapper wrapper = ApiWrapper.ofMainnet(hexPrivateKey, tronProApiKey);

        public ApiWrapper get() {
            return wrapper;
        }

        @Override
        public void close() {
            wrapper.close();
        }
    }

    // The private key address derivation is compatible (tested) with tronlink wallet
    // To support mnemonic we would need to update bitrafael and add tron coin_type 195 (derive path: m/44'/195'/${ index }'/0/0)
    private String getAddress(CharSequence hexPrivateKey) {
        SECP256K1.PublicKey publicKey = SECP256K1.KeyPair.create(SECP256K1.PrivateKey.create(Bytes32.fromHexString(hexPrivateKey))).getPublicKey();
        Keccak.Digest256 digest = new Keccak.Digest256();
        digest.update(publicKey.getEncoded(), 0, 64);
        byte[] raw = digest.digest();
        byte[] rawAddr = new byte[21];
        rawAddr[0] = 0x41;
        System.arraycopy(raw, 12, rawAddr, 1, 20);
        return Base58Check.bytesToBase58(rawAddr);
    }


    @Override
    public Set<String> getCryptoCurrencies() {
        return cryptoCurrencies;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return tokenSymbol;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!tokenSymbol.equals(cryptoCurrency)) {
            log.error("{} TRC20 wallet - unknown cryptocurrency '{}'", tokenSymbol, cryptoCurrency);
            return null;
        }
        return walletAddress;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!tokenSymbol.equals(cryptoCurrency)) {
            log.error("{} TRC20 wallet - unknown cryptocurrency '{}'", tokenSymbol, cryptoCurrency);
            return null;
        }

        try (AutoClosingApiWrapper wrapper = new AutoClosingApiWrapper()) {
            Function balanceOf = new Function("balanceOf",
                Collections.singletonList(new Address(walletAddress)),
                Collections.singletonList(new TypeReference<Uint256>() {}));

            Response.TransactionExtention txnExt = wrapper.get().constantCall(walletAddress, contractAddress, balanceOf);
            String result = Numeric.toHexString(txnExt.getConstantResult(0).toByteArray());
            BigInteger decodedResult = (BigInteger) FunctionReturnDecoder.decode(result, balanceOf.getOutputParameters()).get(0).getValue();

            return new BigDecimal(decodedResult).movePointLeft(tokenDecimalPlaces).stripTrailingZeros();
        } catch (RuntimeException e) {
            log.error("Error obtaining balance.", e);
            return null;
        }
    }


    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!tokenSymbol.equals(cryptoCurrency)) {
            log.error("{} TRC20 wallet - unknown cryptocurrency '{}'", tokenSymbol, cryptoCurrency);
            return null;
        }

        BigDecimal cryptoBalance = getCryptoBalance(cryptoCurrency);
        if (cryptoBalance == null || cryptoBalance.compareTo(amount) < 0) {
            log.error("ERC20 wallet error: Not enough tokens. Balance is: " + cryptoBalance + " " + cryptoCurrency + ". Trying to send: " + amount + " " + cryptoCurrency);
            return null;
        }

        log.info("TRC20 sending {} {} from {} to {}", amount, cryptoCurrency, walletAddress, destinationAddress);
        try (AutoClosingApiWrapper wrapper = new AutoClosingApiWrapper()) {
            Function transfer = new Function("transfer",
                Arrays.asList(new Address(destinationAddress),
                    new Uint256(amount.movePointRight(tokenDecimalPlaces).toBigInteger())),
                Collections.singletonList(new TypeReference<Bool>() {
                }));

            Chain.Transaction transaction = wrapper.get().triggerCall(walletAddress, contractAddress, transfer)
                .setFeeLimit(FEE_LIMIT)
                .setMemo(description) // this is visible in blockchain explorer
                .build();
            Chain.Transaction signedTxn = wrapper.get().signTransaction(transaction);
            String txID = wrapper.get().broadcastTransaction(signedTxn);
            log.info("TRC20 transaction broadcast: {} ", txID);

            return processResult(txID, wrapper.get());

        } catch (RuntimeException e) {
            log.error("Error sending coins", e);
            return null;
        }
    }

    private String processResult(String txID, ApiWrapper apiWrapper) {
        try {
            Response.TransactionInfo txInfo = getTransactionInfo(txID, apiWrapper);
            Chain.Transaction.Result.contractResult result = txInfo.getReceipt().getResult();

            if (result != Chain.Transaction.Result.contractResult.SUCCESS) {
                log.error("Transaction failed: {}\n{}", result, txInfo);
                return null;
            }
        } catch (IllegalException e) {
            log.warn("Unknown transaction result, returning as successful");
            return txID;
        }

        log.info("Transaction successful: {}", txID);
        return txID;
    }

    /**
     * tries to get transaction info from blockchain and retries if it is not yet available
     * @throws IllegalException when all re-tries failed
     */
    private Response.TransactionInfo getTransactionInfo(String txID, ApiWrapper apiWrapper) throws IllegalException {
        int count = 0;
        int maxTries = 3;
        while (true) {
            try {
                sleepBlockTime(); // tron blocks are fast. No need to query before the next block
                return apiWrapper.getTransactionInfoById(txID);
            } catch (IllegalException e) {
                if (++count == maxTries) {
                    throw e;
                }
                // intentionally not logging exception's message because it can be confusing here.
                // It is thrown when the transaction is not yet in blockchain
                log.warn("Transaction info not ready. Retrying");
            }
        }
    }

    private void sleepBlockTime() {
        try {
            TimeUnit.SECONDS.sleep(BLOCK_TIME_SECONDS);
        } catch (InterruptedException e) {
            //ignore
        }
    }
}
