package com.generalbytes.batm.server.extensions.extra.nano.wallets.paper;

import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.TestExtensionContext;
import com.generalbytes.batm.server.extensions.extra.nano.NanoExtension;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author Karl Oczadly
 */
public class NanoPaperWalletGeneratorTest {

    @Test
    public void testMessageFileExists() {
        assertNotNull(NanoPaperWalletGenerator.readMessageTemplate("en"));
        assertNull(NanoPaperWalletGenerator.readMessageTemplate("NonExistentLang"));
    }

    @Test
    public void testGenerate() {
        NanoPaperWalletGenerator gen = new NanoPaperWalletGenerator(
                new TestExtensionContext(), NanoExtension.CURRENCY_SPEC);

        IPaperWallet wallet = gen.generateWallet(NanoExtension.CURRENCY_SPEC.getCurrencyCode(), "passw0rd", "en", true);

        assertEquals("zip", wallet.getFileExtension());
        assertEquals("application/zip", wallet.getContentType());
        assertEquals(64, wallet.getPrivateKey().length());
        assertEquals(65, wallet.getAddress().length());
        assertNotNull(wallet.getMessage());
        assertFalse(wallet.getMessage().isEmpty());
    }

}