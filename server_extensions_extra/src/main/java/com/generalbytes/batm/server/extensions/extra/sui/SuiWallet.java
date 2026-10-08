package com.generalbytes.batm.server.extensions.extra.sui;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

/**
 * SUI wallet backed by a local SUI node via JSON-RPC.
 * Config string: suirpc:http://HOST:9000:BASE64_ED25519_SEED
 *
 * The private key is the 32-byte Ed25519 seed stored as Base64.
 * The SUI address is derived as: "0x" + hex(BLAKE2b-256([0x00] || publicKey))
 */
public class SuiWallet implements IWallet, IQueryableWallet, IGeneratesNewDepositCryptoAddress {

    private static final Logger log = LoggerFactory.getLogger(SuiWallet.class);

    private final SuiRpcClient rpcClient;
    private final byte[] privateKeySeed;   // 32-byte Ed25519 seed
    private final byte[] publicKeyBytes;   // 32-byte Ed25519 public key
    private final String address;

    public SuiWallet(String rpcUrl, String base64PrivateKeySeed) {
        this.rpcClient = new SuiRpcClient(rpcUrl);
        this.privateKeySeed = Base64.getDecoder().decode(base64PrivateKeySeed);
        Ed25519PrivateKeyParameters privKey = new Ed25519PrivateKeyParameters(privateKeySeed, 0);
        Ed25519PublicKeyParameters pubKey = privKey.generatePublicKey();
        this.publicKeyBytes = pubKey.getEncoded();
        this.address = deriveAddress(this.publicKeyBytes);
    }

    /**
     * Derives the SUI address from a public key.
     * Algorithm: BLAKE2b-256([0x00] || pubkey), then hex-encode with "0x" prefix.
     * 0x00 is the Ed25519 scheme flag.
     */
    static String deriveAddress(byte[] publicKeyBytes) {
        byte[] toHash = new byte[1 + 32];
        toHash[0] = 0x00; // Ed25519 scheme flag
        System.arraycopy(publicKeyBytes, 0, toHash, 1, 32);

        var digest = DigestFactory.createBlake2b256();
        digest.update(toHash, 0, toHash.length);
        byte[] hash = new byte[32];
        digest.doFinal(hash, 0);

        return "0x" + Hex.toHexString(hash);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> currencies = new HashSet<>();
        currencies.add(CryptoCurrency.SUI.getCode());
        return currencies;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CryptoCurrency.SUI.getCode();
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("SuiWallet error: unknown cryptocurrency: {}", cryptoCurrency);
            return null;
        }
        return address;
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        return getCryptoAddress(cryptoCurrency);
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("SuiWallet error: unknown cryptocurrency: {}", cryptoCurrency);
            return null;
        }
        try {
            return rpcClient.getSuiBalance(address);
        } catch (Exception e) {
            log.error("Error reading SUI balance for {}.", address, e);
        }
        return null;
    }

    @Override
    public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
        try {
            BigDecimal balance = rpcClient.getSuiBalance(address);
            int confirmations = balance.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0;
            return new ReceivedAmount(balance, confirmations);
        } catch (Exception e) {
            log.error("Error reading received SUI amount for address {}.", address, e);
        }
        return ReceivedAmount.ZERO;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("SuiWallet error: unknown cryptocurrency: {}", cryptoCurrency);
            return null;
        }
        if (destinationAddress == null || destinationAddress.isEmpty()) {
            log.error("SuiWallet error: destination address is null or empty");
            return null;
        }
        try {
            log.info("SuiWallet - sending {} SUI from {} to {}", amount, address, destinationAddress);
            String digest = rpcClient.transferSui(address, destinationAddress, amount, privateKeySeed, publicKeyBytes);
            log.info("SuiWallet - transaction digest: {}", digest);
            return digest;
        } catch (Exception e) {
            log.error("Error sending {} SUI to {} (description: {})", amount, destinationAddress, description, e);
        }
        return null;
    }
}
