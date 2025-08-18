package com.generalbytes.batm.server.extensions.travelrule;


import com.generalbytes.batm.server.extensions.IExtensionContext;
import lombok.AllArgsConstructor;

import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * A facade that provides a simplified interface for travel rule related operations from {@code IExtensionContext}.
 */
@AllArgsConstructor
public class TravelRuleExtensionContext {

    private final IExtensionContext context;

    public ITravelRuleTransferData findTravelRuleTransferByAddress(String address) {
        return context.findTravelRuleTransferByAddress(address);
    }

    public KeyStore loadKeyStoreFromConfigDirectory(String keyStoreType, Path filePath, String passphrase) {
        return context.loadKeyStoreFromConfigDirectory(keyStoreType, filePath, passphrase);
    }

    public X509Certificate loadX509CertificateFromConfigDirectory(Path filePath) {
        return context.loadX509CertificateFromConfigDirectory(filePath);
    }

    public boolean configFileExists(String fileNameInConfigDirectory) {
        return context.configFileExists(fileNameInConfigDirectory);
    }

    public String getConfigProperty(String fileNameInConfigDirectory, String key, String defaultValue) {
        return context.getConfigProperty(fileNameInConfigDirectory, key, defaultValue);
    }

}
