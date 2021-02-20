package com.generalbytes.batm.server.extensions.extra.nano.test;

import com.generalbytes.batm.server.extensions.extra.nano.wallets.paper.NanoPaperWalletGenerator;

import java.awt.*;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tests paper wallet generation
 */
class TestPaperWallet {

    public static void main(String[] args) throws Exception {
        String message = NanoPaperWalletGenerator.createMessage(
            "en", "nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz");

        Path p = Paths.get("nano-test-message.html").toAbsolutePath();
        try (BufferedWriter writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
            writer.write(message);
        }
        Desktop.getDesktop().browse(p.toUri());

        System.out.println("----------------------------");
        System.out.printf("Written file to %s%n", p);
        System.out.println("----------------------------");
    }

}
