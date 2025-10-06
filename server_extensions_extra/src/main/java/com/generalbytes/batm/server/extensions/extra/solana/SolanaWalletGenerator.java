package com.generalbytes.batm.server.extensions.extra.solana;

import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;

import java.security.SecureRandom;

/**
 * Generates a new Solana-compatible paper wallet.
 * <p>
 * This implementation creates an Ed25519 key pair and produces a 64-byte Solana-compatible secret key.
 * The resulting address and secret key can be imported into standard Solana wallets.
 * <p>
 * The generated wallet content is packaged as a ZIP archive and primarily used for distribution via email.
 * <p>
 * For more details about Solana accounts and key formats,
 * see the <a href="https://solana.com/docs/core/accounts">official documentation</a>.
 */
@Slf4j
@AllArgsConstructor
public class SolanaWalletGenerator implements IPaperWalletGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int ENTROPY = 256;
    /**
     * File extension used for the generated {@link IPaperWallet#getContent()}.
     * <p>
     * Mainly used when delivering the wallet as an email attachment.
     */
    private static final String FILE_EXTENSION = "zip";
    /**
     * MIME content type for the generated {@link IPaperWallet#getContent()}.
     * <p>
     * Mainly used when delivering the wallet as an email attachment.
     */
    private static final String FILE_CONTENT_TYPE = "application/zip";
    /**
     * Default message template sent with the generated {@link IPaperWallet#getContent()}.
     * <p>
     * The first placeholder ({@code %s}) represents the cryptocurrency name, and the second ({@code %s}) represents the wallet address.
     * <p>
     * Mainly used when delivering the wallet as an email attachment.
     */
    private static final String DEFAULT_DELIVERY_MESSAGE = "A new %s wallet %s, use your one-time password to open the attachment.";
    /**
     * Name of file in config directory (typically {@code /batm/config}) containing the delivery message.
     * <p>
     * The placeholder ({@code %s}) is replaced with the user's language.
     */
    private static final String DELIVERY_MESSAGE_FILE_NAME = "template_wallet_%s.txt";
    private final IExtensionContext extensionContext;

    @Override
    public IPaperWallet generateWallet(String cryptocurrency, String otp, String userLanguage, boolean shouldBeVanity) {
        checkVanityAddressRequest(shouldBeVanity);

        AsymmetricCipherKeyPair keyPair = generateKeyPair();

        Ed25519PrivateKeyParameters privateKey = (Ed25519PrivateKeyParameters) keyPair.getPrivate();
        Ed25519PublicKeyParameters publicKey = (Ed25519PublicKeyParameters) keyPair.getPublic();

        byte[] privateKeyAsBytes = privateKey.getEncoded();
        byte[] publicKeyAsBytes = publicKey.getEncoded();

        byte[] secretKeyAsBytes = createSecretKey(privateKeyAsBytes, publicKeyAsBytes);

        String address = Base58.encode(publicKeyAsBytes);
        String secretKey = Base58.encode(secretKeyAsBytes);

        return createPaperWallet(address, secretKey, cryptocurrency, otp, userLanguage);
    }

    private void checkVanityAddressRequest(boolean shouldBeVanity) {
        if (shouldBeVanity) {
            log.warn("Vanity addresses are not supported by the Solana wallet generator."
                + " A fully random wallet will be generated instead.");
        }
    }

    private AsymmetricCipherKeyPair generateKeyPair() {
        Ed25519KeyPairGenerator generator = new Ed25519KeyPairGenerator();
        generator.init(new KeyGenerationParameters(secureRandom, ENTROPY));

        return generator.generateKeyPair();
    }

    /**
     * Creates a Solana-compatible secret key (64 bytes) by concatenating the 32-byte private key (seed) and the 32-byte public key.
     * <p>
     * In the Solana ecosystem, the secret key is expected to be a 64-byte array structured as {@code privateKey || publicKey}.
     * This format is used by most Solana wallets for key import/export.
     *
     * @param privateKey Private key as bytes.
     * @param publicKey  Public key as bytes.
     * @return Secret key as bytes.
     */
    private byte[] createSecretKey(byte[] privateKey, byte[] publicKey) {
        byte[] secretKey = new byte[64];
        System.arraycopy(privateKey, 0, secretKey, 0, 32);
        System.arraycopy(publicKey, 0, secretKey, 32, 32);

        return secretKey;
    }

    private IPaperWallet createPaperWallet(String address, String secretKey, String cryptocurrency, String otp, String userLanguage) {
        byte[] content = extensionContext.createPaperWallet7ZIP(secretKey, address, otp, cryptocurrency);
        String deliveryMessage = getDeliveryMessage(address, cryptocurrency, userLanguage);

        return new IPaperWallet() {
            @Override
            public byte[] getContent() {
                return content;
            }

            @Override
            public String getAddress() {
                return address;
            }

            @Override
            public String getPrivateKey() {
                return secretKey;
            }

            @Override
            public String getFileExtension() {
                return FILE_EXTENSION;
            }

            @Override
            public String getContentType() {
                return FILE_CONTENT_TYPE;
            }

            @Override
            public String getMessage() {
                return deliveryMessage;
            }

            @Override
            public String getCryptoCurrency() {
                return cryptocurrency;
            }
        };
    }

    private String getDeliveryMessage(String address, String cryptocurrency, String userLanguage) {
        String messageInUserLanguage = extensionContext.getConfigFileContent(String.format(DELIVERY_MESSAGE_FILE_NAME, userLanguage));
        if (StringUtils.isNotBlank(messageInUserLanguage)) {
            return messageInUserLanguage;
        }

        String messageInEnglish = extensionContext.getConfigFileContent(String.format(DELIVERY_MESSAGE_FILE_NAME, "en"));
        if (StringUtils.isNotBlank(messageInEnglish)) {
            return messageInEnglish;
        }

        return String.format(DEFAULT_DELIVERY_MESSAGE, cryptocurrency, address);
    }

}
