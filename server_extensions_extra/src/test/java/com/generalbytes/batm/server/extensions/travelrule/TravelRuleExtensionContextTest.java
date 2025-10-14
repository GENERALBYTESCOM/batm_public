package com.generalbytes.batm.server.extensions.travelrule;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void testConvertCryptoToBaseUnit() {
        BigDecimal amount = BigDecimal.valueOf(21L);
        String cryptocurrency = "BTC";

        when(extensionContext.convertCryptoToBaseUnit(amount, cryptocurrency)).thenReturn(2_100_000_000L);

        long result = travelRuleExtensionContext.convertCryptoToBaseUnit(amount, cryptocurrency);

        assertEquals(2_100_000_000L, result);
    }

    @Test
    void testConvertCryptoFromBaseUnit() {
        long amount = 2_100_000_000L;
        String cryptocurrency = "BTC";

        when(extensionContext.convertCryptoFromBaseUnit(amount, cryptocurrency)).thenReturn(BigDecimal.valueOf(21L));

        BigDecimal result = travelRuleExtensionContext.convertCryptoFromBaseUnit(amount, cryptocurrency);

        assertEquals(BigDecimal.valueOf(21L), result);
    }

    @Test
    void findTravelRuleTransferByPublicId() {
        ITravelRuleTransferData transferData = mock(ITravelRuleTransferData.class);

        when(extensionContext.findTravelRuleTransferByPublicId("transfer_public_id")).thenReturn(transferData);

        ITravelRuleTransferData result = travelRuleExtensionContext.findTravelRuleTransferByPublicId("transfer_public_id");

        assertSame(transferData, result);
    }

}