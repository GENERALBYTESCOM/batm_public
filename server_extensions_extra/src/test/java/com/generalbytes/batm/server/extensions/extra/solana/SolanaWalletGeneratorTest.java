package com.generalbytes.batm.server.extensions.extra.solana;

import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolanaWalletGeneratorTest {

    @Mock
    private IExtensionContext extensionContext;
    @InjectMocks
    private SolanaWalletGenerator walletGenerator;

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testGenerateWallet_messageInUserLanguage(boolean shouldBeVanity) throws Exception {
        when(extensionContext.createPaperWallet7ZIP(anyString(), anyString(), eq("12345"), eq("SOL")))
            .thenReturn(new byte[] { 21 });
        when(extensionContext.getConfigFileContent("template_wallet_cs.txt")).thenReturn("delivery_message_cs");

        IPaperWallet solanaPaperWallet = walletGenerator.generateWallet("SOL", "12345", "cs", shouldBeVanity);

        assertCommonSolanaPaperWallet(solanaPaperWallet);
        assertEquals("delivery_message_cs", solanaPaperWallet.getMessage());
        verifyNoMoreInteractions(extensionContext);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testGenerateWallet_messageInEnglish(String messageInUserLanguage) throws Exception {
        when(extensionContext.createPaperWallet7ZIP(anyString(), anyString(), eq("12345"), eq("SOL")))
            .thenReturn(new byte[] { 21 });
        when(extensionContext.getConfigFileContent("template_wallet_cs.txt")).thenReturn(messageInUserLanguage);
        when(extensionContext.getConfigFileContent("template_wallet_en.txt")).thenReturn("delivery_message_en");

        IPaperWallet solanaPaperWallet = walletGenerator.generateWallet("SOL", "12345", "cs", false);

        assertCommonSolanaPaperWallet(solanaPaperWallet);
        assertEquals("delivery_message_en", solanaPaperWallet.getMessage());
        verifyNoMoreInteractions(extensionContext);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testGenerateWallet_defaultMessage(String messageInEnglish) throws Exception {
        when(extensionContext.createPaperWallet7ZIP(anyString(), anyString(), eq("12345"), eq("SOL")))
            .thenReturn(new byte[] { 21 });
        when(extensionContext.getConfigFileContent("template_wallet_cs.txt")).thenReturn(null);
        when(extensionContext.getConfigFileContent("template_wallet_en.txt")).thenReturn(messageInEnglish);

        IPaperWallet solanaPaperWallet = walletGenerator.generateWallet("SOL", "12345", "cs", false);

        assertCommonSolanaPaperWallet(solanaPaperWallet);
        assertTrue(solanaPaperWallet.getMessage()
            .matches("^A new SOL wallet [A-Za-z0-9]+, use your one-time password to open the attachment\\.$"));
        verifyNoMoreInteractions(extensionContext);
    }

    private void assertCommonSolanaPaperWallet(IPaperWallet solanaPaperWallet) throws Exception {
        byte[] decodedAddress = Base58.decode(solanaPaperWallet.getAddress());
        assertEquals(32, decodedAddress.length);

        byte[] decodedSecretKey = Base58.decode(solanaPaperWallet.getPrivateKey());
        assertEquals(64, decodedSecretKey.length);

        byte[] addressFromSecretKey = Arrays.copyOfRange(decodedSecretKey, 32, 64);
        assertArrayEquals(decodedAddress, addressFromSecretKey);

        assertArrayEquals(new byte[] { 21 }, solanaPaperWallet.getContent());
        assertEquals("zip", solanaPaperWallet.getFileExtension());
        assertEquals("application/zip", solanaPaperWallet.getContentType());
        assertEquals("SOL", solanaPaperWallet.getCryptoCurrency());
    }

}