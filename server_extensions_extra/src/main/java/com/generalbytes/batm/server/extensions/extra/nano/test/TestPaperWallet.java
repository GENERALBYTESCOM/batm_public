package com.generalbytes.batm.server.extensions.extra.nano.test;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.TestExtensionContext;
import com.generalbytes.batm.server.extensions.extra.nano.NanoCurrencyUtil;
import com.generalbytes.batm.server.extensions.extra.nano.NanoExtensionContext;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoRpcClient;
import com.generalbytes.batm.server.extensions.extra.nano.wallet.paper.NanoPaperWalletGenerator;

import java.awt.*;
import java.io.BufferedWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * THIS CLASS MAY BE IGNORED.
 * It's only purpose is to test and help during development.
 */
public class TestPaperWallet {

    public static void main(String[] args) throws Exception {
        NanoRpcClient rpcClient = new NanoRpcClient(new URL("http://[::1]:7076"));

        NanoExtensionContext extContext = new NanoExtensionContext(
                CryptoCurrency.NANO, new TestExtensionContext(), NanoCurrencyUtil.NANO);
        extContext.setRpcClient(rpcClient);
        NanoPaperWalletGenerator walletGen = new NanoPaperWalletGenerator(extContext);

        IPaperWallet paperWallet = walletGen.generateWallet(
                CryptoCurrency.NANO.getCurrencyName(), "1234", "en", false);

        System.out.println(paperWallet.getAddress());
        System.out.println(paperWallet.getPrivateKey());
        System.out.println(paperWallet.getMessage());

        Path p = Paths.get(".nano-test-message.html").toAbsolutePath();
        try (BufferedWriter writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
            writer.write(paperWallet.getMessage());
        }
        Desktop.getDesktop().browse(p.toUri());
    }

}
