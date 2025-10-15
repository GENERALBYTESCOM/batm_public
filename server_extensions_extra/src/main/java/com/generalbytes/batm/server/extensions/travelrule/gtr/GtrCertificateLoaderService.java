package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * Service responsible for loading Global Travel Rule (GTR) certificates used in {@link GtrApiFactory}.
 */
@AllArgsConstructor
public class GtrCertificateLoaderService {

    private final TravelRuleExtensionContext extensionContext;

    /**
     * Loads GTR key store with client certificates and creates {@link KeyManagerFactory}.
     *
     * @param certificatePath       Relative GTR client certificate path in config directory (typically {@code /batm/config})
     *                              in {@code .p12} format. The path must contain a file extension.
     * @param certificatePassphrase Passphrase for access to key store. Can be {code null} if passphrase is not used.
     * @return {@link KeyManagerFactory} for initialization {@link SSLContext} in {@link GtrApiFactory}.
     * @throws Exception If error was occurred while loading key store.
     */
    public KeyManagerFactory loadGtrKeyStore(String certificatePath, String certificatePassphrase) throws Exception {
        KeyStore keyStore = getKeyStore(certificatePath, certificatePassphrase);
        return initKeyManagerFactory(keyStore, certificatePassphrase);
    }

    private KeyStore getKeyStore(String certificatePath, String certificatePassphrase) {
        KeyStore keyStore = extensionContext.loadKeyStoreFromConfigDirectory("PKCS12", Path.of(certificatePath), certificatePassphrase);
        if (keyStore == null) {
            throw new TravelRuleProviderException("key store of type 'PKCS12' with relative path '" + certificatePath + "' failed to load");
        }

        return keyStore;
    }

    private KeyManagerFactory initKeyManagerFactory(KeyStore keyStore, String certificatePassphrase) throws Exception {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        char[] passphraseAsCharArray = getPassphraseAsCharArray(certificatePassphrase);
        keyManagerFactory.init(keyStore, passphraseAsCharArray);

        return keyManagerFactory;
    }

    private char[] getPassphraseAsCharArray(String passphrase) {
        if (StringUtils.isEmpty(passphrase)) {
            return null;
        }

        return passphrase.toCharArray();
    }

    /**
     * Loads GTR trust store with GTR server certificate and creates {@link TrustManagerFactory}.
     *
     * @param certificatePath Relative GTR server certificate path in config directory (typically {@code /batm/config})
     *                        in {@code .pem} format. The path must contain a file extension.
     * @return {@link TrustManagerFactory} for initialization {@link SSLContext} in {@link GtrApiFactory}.
     * @throws Exception If error was occurred while loading trust store.
     */
    public TrustManagerFactory loadGtrTrustStore(String certificatePath) throws Exception {
        X509Certificate x509Certificate = getX509Certificate(certificatePath);
        KeyStore trustStore = loadTrustStoreWithCertificate(x509Certificate);
        return initTrustManagerFactory(trustStore);
    }

    private X509Certificate getX509Certificate(String certificatePath) {
        X509Certificate x509Certificate = extensionContext.loadX509CertificateFromConfigDirectory(Path.of(certificatePath));
        if (x509Certificate == null) {
            throw new TravelRuleProviderException("certificate of type 'pem' with relative path '" + certificatePath + "' failed to load");
        }

        return x509Certificate;
    }

    private KeyStore loadTrustStoreWithCertificate(X509Certificate x509Certificate) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        trustStore.setCertificateEntry("gtr_server_trust_certificate", x509Certificate);

        return trustStore;
    }

    private TrustManagerFactory initTrustManagerFactory(KeyStore trustStore) throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        return trustManagerFactory;
    }

}
