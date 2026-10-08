package com.generalbytes.batm.server.extensions.extra.sui;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates a new SUI paper wallet.
 *
 * Key format: 32-byte Ed25519 private key seed, stored as Base64.
 * Address: "0x" + hex(BLAKE2b-256([0x00] || publicKey))
 */
public class SuiWalletGenerator implements IPaperWalletGenerator {

    private static final Logger log = LoggerFactory.getLogger(SuiWalletGenerator.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String FILE_EXTENSION = "zip";
    private static final String FILE_CONTENT_TYPE = "application/zip";
    private static final String DEFAULT_DELIVERY_MESSAGE = "A new %s wallet %s, use your one-time password to open the attachment.";
    private static final String DELIVERY_MESSAGE_FILE_NAME = "template_wallet_%s.txt";

    private final IExtensionContext extensionContext;

    public SuiWalletGenerator(IExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    @Override
    public IPaperWallet generateWallet(String cryptocurrency, String otp, String userLanguage, boolean shouldBeVanity) {
        if (shouldBeVanity) {
            log.warn("Vanity addresses are not supported by the SUI wallet generator. A random wallet will be generated.");
        }

        Ed25519KeyPairGenerator generator = new Ed25519KeyPairGenerator();
        generator.init(new KeyGenerationParameters(SECURE_RANDOM, 256));
        AsymmetricCipherKeyPair keyPair = generator.generateKeyPair();

        Ed25519PrivateKeyParameters privKey = (Ed25519PrivateKeyParameters) keyPair.getPrivate();
        Ed25519PublicKeyParameters pubKey = (Ed25519PublicKeyParameters) keyPair.getPublic();

        byte[] privateKeySeed = privKey.getEncoded();   // 32 bytes
        byte[] publicKeyBytes = pubKey.getEncoded();     // 32 bytes

        String address = SuiWallet.deriveAddress(publicKeyBytes);
        // Private key stored as Base64 — matches SuiWallet constructor input
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKeySeed);

        byte[] content = extensionContext.createPaperWallet7ZIP(privateKeyBase64, address, otp, cryptocurrency);
        String deliveryMessage = getDeliveryMessage(address, cryptocurrency, userLanguage);

        return new IPaperWallet() {
            @Override public byte[] getContent() { return content; }
            @Override public String getAddress() { return address; }
            @Override public String getPrivateKey() { return privateKeyBase64; }
            @Override public String getFileExtension() { return FILE_EXTENSION; }
            @Override public String getContentType() { return FILE_CONTENT_TYPE; }
            @Override public String getMessage() { return deliveryMessage; }
            @Override public String getCryptoCurrency() { return cryptocurrency; }
        };
    }

    private String getDeliveryMessage(String address, String cryptocurrency, String userLanguage) {
        String msgInLang = extensionContext.getConfigFileContent(String.format(DELIVERY_MESSAGE_FILE_NAME, userLanguage));
        if (StringUtils.isNotBlank(msgInLang)) {
            return msgInLang;
        }
        String msgInEnglish = extensionContext.getConfigFileContent(String.format(DELIVERY_MESSAGE_FILE_NAME, "en"));
        if (StringUtils.isNotBlank(msgInEnglish)) {
            return msgInEnglish;
        }
        return String.format(DEFAULT_DELIVERY_MESSAGE, cryptocurrency, address);
    }
}
