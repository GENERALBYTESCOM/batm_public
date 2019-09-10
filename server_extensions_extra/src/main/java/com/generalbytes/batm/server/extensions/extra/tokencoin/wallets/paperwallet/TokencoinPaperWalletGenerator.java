/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.tokencoin.wallets.paperwallet;


import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Hashtable;
import java.util.Random;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TokencoinPaperWalletGenerator implements IPaperWalletGenerator {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.TokencoinPaperWalletGenerator");

    private static int imagesize = 400;
    private static final String MESSAGE = "We have attached a QR code with your address. Please use your QR code to add more funds to your account. Your address is ";

    @Override
    public IPaperWallet generateWallet(String cryptoCurrency, String passphrase, String language) {

        return generateWallet();
    }

    private TokencoinPaperWallet generateWallet()  {
        String charactersallowed = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        String newtkn = "TKN-"
                + generateString(charactersallowed,4)
                +"-"+generateString(charactersallowed,4)
                +"-"+generateString(charactersallowed,4)
                +"-"+generateString(charactersallowed,5);

        TokencoinPaperWallet paperwallet = new TokencoinPaperWallet();
        byte[] image = generateQR(newtkn, imagesize);

        paperwallet.setCryptoCurrency(CryptoCurrency.TKN.getCode());
        paperwallet.setMessage(MESSAGE + newtkn);
        paperwallet.setFileExtension("png");
        paperwallet.setAddress(newtkn);
        paperwallet.setContentType("image/png");
        paperwallet.setContent(image);

        return paperwallet;
    }
    public static String generateString(String characters, int length)
    {
        Random rng = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    public byte[] generateQR(String address, int size)  {
        Hashtable hintMap = new Hashtable();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(address,
                    BarcodeFormat.QR_CODE, size, size, hintMap);
            int matrixWidth = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(matrixWidth, matrixWidth,
                    BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, matrixWidth, matrixWidth);
            // Paint and save the image using the ByteMatrix
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < matrixWidth; i++) {
                for (int j = 0; j < matrixWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream stream = new MemoryCacheImageOutputStream(baos);
            ImageIO.write(image, "png", stream);
            stream.close();
            return baos.toByteArray();
        }	catch (WriterException e) {
            log.error("Error", e);
        } catch (IOException e) {
            log.error("Error", e);
        }
        return null;
    }



}
