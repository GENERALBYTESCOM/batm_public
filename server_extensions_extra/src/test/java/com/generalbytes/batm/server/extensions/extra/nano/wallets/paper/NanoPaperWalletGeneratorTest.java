package com.generalbytes.batm.server.extensions.extra.nano.wallets.paper;

import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.TestExtensionContext;
import com.generalbytes.batm.server.extensions.extra.nano.NanoExtension;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Karl Oczadly
 */
public class NanoPaperWalletGeneratorTest extends TestCase {

    @Test
    public void testMessageFileExists() {
        assertNotNull(NanoPaperWalletGenerator.getMessageResource("en"));
    }

    @Test
    public void testGenerate() {
        NanoPaperWalletGenerator gen = new NanoPaperWalletGenerator(new TestExtensionContext(),
            "nano", "nano");

        IPaperWallet wallet = gen.generateWallet(NanoExtension.CURRENCY_CODE, "pass", "en", true);

        assertEquals("zip", wallet.getFileExtension());
        assertEquals("application/zip", wallet.getContentType());
        assertEquals(64, wallet.getPrivateKey().length());
        assertEquals(65, wallet.getAddress().length());
        assertNotNull(wallet.getMessage());
        assertFalse(wallet.getMessage().isEmpty());
    }

}