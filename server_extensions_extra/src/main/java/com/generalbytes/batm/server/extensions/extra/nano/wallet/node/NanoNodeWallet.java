package com.generalbytes.batm.server.extensions.extra.nano.wallet.node;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.nano.NanoExtensionContext;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoRpcClient;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoWsClient;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * @author Karl Oczadly
 */
public class NanoNodeWallet implements INanoRpcWallet, IGeneratesNewDepositCryptoAddress, IWallet, IQueryableWallet {

    private static final Logger log = LoggerFactory.getLogger(NanoNodeWallet.class);

    private final NanoExtensionContext context;
    private final NanoRpcClient rpcClient;
    private final NanoWsClient wsClient;
    private final String walletId, hotWalletAccount;

    public NanoNodeWallet(NanoExtensionContext context, NanoRpcClient rpcClient, NanoWsClient wsClient,
                           String walletId, String hotWalletAccount) {
        this.context = context;
        this.rpcClient = rpcClient;
        this.wsClient = wsClient;
        this.walletId = walletId;
        this.hotWalletAccount = hotWalletAccount;
    }


    @Override
    public NanoRpcClient getRpcClient() {
        return rpcClient;
    }

    @Override
    public NanoWsClient getWsClient() {
        return wsClient;
    }

    @Override
    public BigInteger sendAllFromWallet(String depositAddress, String destination) {
        try {
            BigInteger balance = rpcClient.getBalance(depositAddress).unconfBalance;
            if (!balance.equals(BigInteger.ZERO)) {
                String hash = rpcClient.sendFromWallet(walletId, depositAddress, destination,
                    balance, UUID.randomUUID().toString());
                log.info("Sent {} to {}, hash: {}", balance, destination, hash);
                return balance;
            }
        } catch (IOException | NanoRpcClient.RpcException e) {
            log.error("Couldn't send deposit wallet funds.", e);
        }
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger moveFundsToHotWallet(String depositAddress) {
        return sendAllFromWallet(depositAddress, hotWalletAccount);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        if (!context.getCurrencyCode().equalsIgnoreCase(cryptoCurrency)) {
            log.warn("Unrecognized currency code {}", cryptoCurrency);
            return null;
        }
        try {
            for (int i = 0; i < 5; i++) {
                String account = rpcClient.newWalletAccount(walletId);
                // Ensure account isn't in a used state
                if (!account.equalsIgnoreCase(hotWalletAccount) && !rpcClient.doesAccountExist(account)) {
                    return account;
                } else {
                    log.warn("Deposit address {} already in use, trying another...", account);
                }
            }
            log.error("Couldn't find an unused deposit address.");
            return null;
        } catch (NanoRpcClient.RpcException | IOException e) {
            log.error("Couldn't create new deposit address.", e);
            return null;
        }
    }

    @Override
    public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
        address = context.getUtil().parseAddress(address);
        try {
            /*
             * TODO: Only including pocketed balance for now. This could be changed in the future if fork resolution
             *       issues are resolved, and would speed up deposit confirmation times.
             */
            NanoRpcClient.AccountBalance balance = rpcClient.getBalance(address);
            if (balance.confBalance.compareTo(BigInteger.ZERO) > 0)
                return new ReceivedAmount(context.getUtil().amountFromRaw(balance.confBalance), Integer.MAX_VALUE);
            // No balance; return unconfirmed and pending blocks with confirmation 0
            BigInteger unconfTotal = balance.unconfBalance.add(balance.unconfPending);
            return new ReceivedAmount(context.getUtil().amountFromRaw(unconfTotal), 0);
        } catch (NanoRpcClient.RpcException | IOException e) {
            log.error("Couldn't retrieve balance for account {}.", address, e);
            return null;
        }
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!context.getCurrencyCode().equalsIgnoreCase(cryptoCurrency)) {
            log.warn("Unrecognized currency code {}", cryptoCurrency);
            return null;
        }
        return hotWalletAccount;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return Collections.singleton(context.getCurrencyCode());
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return context.getCurrencyCode();
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        try {
            return context.getUtil().amountFromRaw(
                    rpcClient.getBalance(hotWalletAccount).confBalance);
        } catch (NanoRpcClient.RpcException | IOException e) {
            log.error("Couldn't retrieve balance of account {}.", hotWalletAccount, e);
            return null;
        }
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!context.getCurrencyCode().equalsIgnoreCase(cryptoCurrency)) {
            log.warn("Unrecognized currency code {}", cryptoCurrency);
            return null;
        }

        destinationAddress = context.getUtil().parseAddress(destinationAddress);
        BigInteger amountRaw = context.getUtil().amountToRaw(amount);
        log.info("Sending {} Nano from hot wallet to {}...", amount, destinationAddress);
        try {
            String hash = rpcClient.sendFromWallet(walletId, hotWalletAccount, destinationAddress,
                    amountRaw, description);
            log.info("Sent {} Nano from hot wallet to {}, hash = {}", amount, destinationAddress, hash);
            return hash;
        } catch (NanoRpcClient.RpcException | IOException e) {
            log.error("Failed to send {} Nano from hot wallet {}.", amount, hotWalletAccount, e);
            return null;
        }
    }


    public static NanoNodeWallet create(NanoExtensionContext context, StringTokenizer args) throws Exception {
        /*
         * ORDER OF CONFIGURATION TOKENS:
         *  0  Node IP/host
         *  1  RPC protocol (http/https)
         *  2  RPC port
         *  3  Hot wallet ID
         *  4  Hot wallet account
         *  5  Websocket protocol (ws/wss)
         *  6  Websocket port
         */
        String nodeHost = args.nextToken();
        if (nodeHost.equals("[")) {
            // IPv6 local address
            nodeHost = "[::1]";
            args.nextToken(); // Skip
        }
        String rpcProtocol = args.nextToken().toLowerCase();
        int rpcPort = Integer.parseInt(args.nextToken());
        URL rpcUrl = new URL(rpcProtocol, nodeHost, rpcPort, "");

        String walletId = args.nextToken().toUpperCase();
        String walletAccount = context.getUtil().parseAddress(args.nextToken());

        URI wsUri = null;
        if (args.hasMoreElements()) {
            String wsProtocol = args.nextToken().toLowerCase();
            int wsPort = Integer.parseInt(args.nextToken());
            wsUri = new URI(wsProtocol, "", nodeHost, wsPort, "", "", "");
        }

        log.info("Using nano_node wallet: RPC: {}, WS: {}, Wallet ID: {}, Hot-wallet: {}",
                rpcUrl, wsUri != null ? wsUri : "[not used]", walletId, walletAccount);

        return new NanoNodeWallet(context,
                new NanoRpcClient(rpcUrl),
                wsUri != null ? new NanoWsClient(wsUri) : null,
                walletId, walletAccount);
    }

}
