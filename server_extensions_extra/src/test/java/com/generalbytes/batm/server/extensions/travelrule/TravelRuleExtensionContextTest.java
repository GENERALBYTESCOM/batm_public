package com.generalbytes.batm.server.extensions.travelrule;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TravelRuleExtensionContextTest {

    @Mock
    private IExtensionContext extensionContext;
    @InjectMocks
    private TravelRuleExtensionContext travelRuleExtensionContext;

    @Test
    void testFindTravelRuleTransferByAddress() {
        String address = "address";
        ITravelRuleTransferData expectedResult = mock(ITravelRuleTransferData.class);

        when(extensionContext.findTravelRuleTransferByAddress(address)).thenReturn(expectedResult);

        ITravelRuleTransferData result = travelRuleExtensionContext.findTravelRuleTransferByAddress(address);

        assertSame(expectedResult, result);
    }

    @Test
    void testLoadKeyStoreFromConfigDirectory() {
        String keyStoreType = "keyStoreType";
        Path filePath = Path.of("/");
        String passphrase = "passphrase";
        KeyStore expectedResult = mock(KeyStore.class);

        when(extensionContext.loadKeyStoreFromConfigDirectory(keyStoreType, filePath, passphrase)).thenReturn(expectedResult);

        KeyStore result = travelRuleExtensionContext.loadKeyStoreFromConfigDirectory(keyStoreType, filePath, passphrase);

        assertSame(expectedResult, result);
    }

    @Test
    void testLoadX509CertificateFromConfigDirectory() {
        Path filePath = Path.of("/");
        X509Certificate expectedResult = mock(X509Certificate.class);

        when(extensionContext.loadX509CertificateFromConfigDirectory(filePath)).thenReturn(expectedResult);

        X509Certificate result = travelRuleExtensionContext.loadX509CertificateFromConfigDirectory(filePath);

        assertSame(expectedResult, result);
    }

    @Test
    void testConfigFileExists() {
        String fileNameInConfigDirectory = "fileNameInConfigDirectory";

        when(extensionContext.configFileExists(fileNameInConfigDirectory)).thenReturn(true);

        boolean result = travelRuleExtensionContext.configFileExists(fileNameInConfigDirectory);

        assertTrue(result);
    }

    @Test
    void testGetConfigProperty() {
        String fileNameInConfigDirectory = "fileNameInConfigDirectory";
        String key = "key";
        String defaultValue = "defaultValue";
        String expectedResult = "expectedResult";

        when(extensionContext.getConfigProperty(fileNameInConfigDirectory, key, defaultValue)).thenReturn(expectedResult);

        String result = travelRuleExtensionContext.getConfigProperty(fileNameInConfigDirectory, key, defaultValue);

        assertSame(expectedResult, result);
    }

}