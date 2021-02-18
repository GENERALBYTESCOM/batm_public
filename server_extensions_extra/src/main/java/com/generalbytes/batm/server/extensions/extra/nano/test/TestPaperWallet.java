package com.generalbytes.batm.server.extensions.extra.nano.test;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.paper.NanoPaperWalletGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Tests paper wallet generation
 */
class TestPaperWallet {

    public static void main(String[] args) throws Exception {
        NanoPaperWalletGenerator paperWalletGenerator = new NanoPaperWalletGenerator();
        IPaperWallet paperWallet = paperWalletGenerator.generateWallet(CryptoCurrency.NANO.getCode(), "", "", false);

        byte[] publicKeyQR = paperWallet.getContent();
        ByteArrayInputStream bis = new ByteArrayInputStream(publicKeyQR);
        BufferedImage bImage = ImageIO.read(bis);
        ImageIO.write(bImage, "png", new File("public_key.png"));

        byte[] privateKeyQR = paperWalletGenerator.generateQR(paperWallet.getPrivateKey(), 400);
        ByteArrayInputStream bisPrivate = new ByteArrayInputStream(privateKeyQR);
        BufferedImage bImage1 = ImageIO.read(bisPrivate);
        ImageIO.write(bImage1, "png", new File("private_key.png"));

        System.out.println("images created");
    }

}
