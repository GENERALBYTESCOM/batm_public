package com.generalbytes.batm.server.extensions.extra.solana;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.programs.AssociatedTokenProgram;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.programs.TokenProgram;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.TokenResultObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Solana wallet backed by a local Solana RPC node.
 * Handles both SOL (native) and USDCSOL (SPL token) depending on configured currency.
 *
 * Config strings:
 *   solanarpc:http://HOST:8899:BASE58_64BYTE_SECRET_KEY
 *   usdcsolrpc:http://HOST:8899:BASE58_64BYTE_SECRET_KEY
 *
 * The secret key must be the 64-byte Solana secret key (private || public) encoded as Base58,
 * which is the same format produced by SolanaWalletGenerator.
 */
public class SolanaRpcWallet implements IWallet, IQueryableWallet, IGeneratesNewDepositCryptoAddress {

    private static final Logger log = LoggerFactory.getLogger(SolanaRpcWallet.class);

    private static final long LAMPORTS_PER_SOL = 1_000_000_000L;
    private static final int USDC_SOL_DECIMALS = 6;
    // USDC mint address on Solana mainnet
    private static final PublicKey USDC_MINT = new PublicKey("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");

    private final String cryptoCurrency;
    private final RpcClient rpcClient;
    private final Account account;

    public SolanaRpcWallet(String rpcUrl, String base58SecretKey, String cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
        this.rpcClient = new RpcClient(rpcUrl);
        byte[] secretKeyBytes = Base58.decode(base58SecretKey);
        this.account = new Account(secretKeyBytes);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> currencies = new HashSet<>();
        currencies.add(cryptoCurrency);
        return currencies;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return cryptoCurrency;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("SolanaRpcWallet error: unknown cryptocurrency: {}", cryptoCurrency);
            return null;
        }
        return account.getPublicKey().toBase58();
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        return getCryptoAddress(cryptoCurrency);
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("SolanaRpcWallet error: unknown cryptocurrency: {}", cryptoCurrency);
            return null;
        }
        try {
            if (CryptoCurrency.SOL.getCode().equals(cryptoCurrency)) {
                return getSolBalance(account.getPublicKey());
            } else if (CryptoCurrency.USDCSOL.getCode().equals(cryptoCurrency)) {
                return getUsdcBalance(account.getPublicKey());
            }
        } catch (Exception e) {
            log.error("Error reading {} balance.", cryptoCurrency, e);
        }
        return null;
    }

    @Override
    public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
        try {
            PublicKey pubKey = new PublicKey(address);
            BigDecimal balance;
            if (CryptoCurrency.SOL.getCode().equals(cryptoCurrency)) {
                balance = getSolBalance(pubKey);
            } else {
                balance = getUsdcBalance(pubKey);
            }
            if (balance == null) {
                return ReceivedAmount.ZERO;
            }
            int confirmations = balance.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0;
            return new ReceivedAmount(balance, confirmations);
        } catch (Exception e) {
            log.error("Error reading received amount for address {}.", address, e);
        }
        return ReceivedAmount.ZERO;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("SolanaRpcWallet error: unknown cryptocurrency: {}", cryptoCurrency);
            return null;
        }
        try {
            if (CryptoCurrency.SOL.getCode().equals(cryptoCurrency)) {
                return sendSol(destinationAddress, amount);
            } else if (CryptoCurrency.USDCSOL.getCode().equals(cryptoCurrency)) {
                return sendUsdc(destinationAddress, amount);
            }
        } catch (Exception e) {
            log.error("Error sending {} {} to {} (description: {})", amount, cryptoCurrency, destinationAddress, description, e);
        }
        return null;
    }

    private BigDecimal getSolBalance(PublicKey pubKey) throws RpcException {
        long lamports = rpcClient.getApi().getBalance(pubKey);
        return BigDecimal.valueOf(lamports).divide(BigDecimal.valueOf(LAMPORTS_PER_SOL));
    }

    private BigDecimal getUsdcBalance(PublicKey ownerPubKey) {
        try {
            List<TokenResultObjects.TokenAccount> accounts =
                rpcClient.getApi().getTokenAccountsByOwner(ownerPubKey, USDC_MINT);
            if (accounts == null || accounts.isEmpty()) {
                return BigDecimal.ZERO;
            }
            double uiAmount = accounts.get(0).getAccount().getData().getParsed().getInfo().getTokenAmount().getUiAmount();
            return BigDecimal.valueOf(uiAmount);
        } catch (Exception e) {
            log.error("Error reading USDC-SOL balance for {}.", ownerPubKey.toBase58(), e);
            return BigDecimal.ZERO;
        }
    }

    private String sendSol(String destinationAddress, BigDecimal amount) throws RpcException {
        PublicKey destination = new PublicKey(destinationAddress);
        long lamports = amount.multiply(BigDecimal.valueOf(LAMPORTS_PER_SOL)).longValue();

        log.info("SolanaRpcWallet - sending {} SOL ({} lamports) from {} to {}", amount, lamports, account.getPublicKey().toBase58(), destinationAddress);

        Transaction tx = new Transaction();
        tx.addInstruction(SystemProgram.transfer(account.getPublicKey(), destination, lamports));
        String signature = rpcClient.getApi().sendTransaction(tx, List.of(account));

        log.info("SolanaRpcWallet - SOL transfer signature: {}", signature);
        return signature;
    }

    private String sendUsdc(String destinationAddress, BigDecimal amount) throws Exception {
        PublicKey destinationOwner = new PublicKey(destinationAddress);
        PublicKey sourceAta = AssociatedTokenProgram.findAssociatedTokenAddress(account.getPublicKey(), USDC_MINT);
        PublicKey destinationAta = AssociatedTokenProgram.findAssociatedTokenAddress(destinationOwner, USDC_MINT);

        long tokenUnits = amount.multiply(BigDecimal.TEN.pow(USDC_SOL_DECIMALS)).longValue();

        log.info("SolanaRpcWallet - sending {} USDCSOL ({} units) from {} (ATA: {}) to {} (ATA: {})",
            amount, tokenUnits, account.getPublicKey().toBase58(), sourceAta.toBase58(),
            destinationAddress, destinationAta.toBase58());

        Transaction tx = new Transaction();

        // Create the destination ATA if it doesn't exist yet (costs a small SOL rent fee)
        if (!ataExists(destinationAta)) {
            log.info("SolanaRpcWallet - destination ATA does not exist, creating: {}", destinationAta.toBase58());
            tx.addInstruction(AssociatedTokenProgram.createAssociatedTokenAccountInstruction(
                account.getPublicKey(), destinationAta, destinationOwner, USDC_MINT));
        }

        tx.addInstruction(TokenProgram.transfer(sourceAta, destinationAta, BigInteger.valueOf(tokenUnits), account.getPublicKey()));

        String signature = rpcClient.getApi().sendTransaction(tx, List.of(account));
        log.info("SolanaRpcWallet - USDCSOL transfer signature: {}", signature);
        return signature;
    }

    private boolean ataExists(PublicKey ataAddress) {
        try {
            long balance = rpcClient.getApi().getBalance(ataAddress);
            return balance > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
