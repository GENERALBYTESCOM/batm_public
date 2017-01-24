package com.generalbytes.batm.server.extensions.extra.shadowcash.wallets.paperwallet;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.extra.shadowcash.ShadowcashMainNetParams;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author ludx
 */
public class ShadowcashPaperWalletGenerator implements IPaperWalletGenerator {

    private static final String CRYPTOCURRENCY = ICurrencies.SDC;
    private static final String SPACE = " ";
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    public IPaperWallet generateWallet(String cryptoCurrency, String passphrase, String language) {

        if (!cryptoCurrency.equalsIgnoreCase(CRYPTOCURRENCY)
                || Strings.isNullOrEmpty(passphrase)
                || Strings.isNullOrEmpty(language)) {
            return null;
        }

        return generateWallet(passphrase, language);
    }

    private ShadowcashPaperWallet generateWallet(String passphrase, String language) {

        ShadowcashPaperWallet shadowcashPaperWallet = new ShadowcashPaperWallet();
        final DeterministicKeyChain deterministicKeyChain = new DeterministicKeyChain(secureRandom, 256, passphrase, MnemonicCode.BIP39_STANDARDISATION_TIME_SECS);
        DeterministicSeed seed = deterministicKeyChain.getSeed();
        Wallet wallet = Wallet.fromSeed(ShadowcashMainNetParams.get(), seed);
        Address currentReceiveAddress = wallet.currentReceiveAddress();
        ECKey currentReceiveKey = wallet.currentReceiveKey();
        String privateKeyAsWIF = currentReceiveKey.getPrivateKeyAsWiF(ShadowcashMainNetParams.get());
        byte[] zipFile = createPasswordProtectedMnemonicZipFile(Joiner.on(SPACE).join(seed.getMnemonicCode()), passphrase);

        shadowcashPaperWallet.setPrivateKey(privateKeyAsWIF);
        shadowcashPaperWallet.setAddress(currentReceiveAddress.toString());
        shadowcashPaperWallet.setContent(zipFile);
        shadowcashPaperWallet.setMessage(getMessage(language));

        return shadowcashPaperWallet;
    }

    public String getMessage(String language) {
        Locale locale = Locale.forLanguageTag(language);
        ResourceBundle translations = ResourceBundle.getBundle("i18n.shadowcash", locale);
        return translations.getString("email");
    }

    private byte[] createPasswordProtectedMnemonicZipFile(String mnemonic, String passphrase) {

        byte[] zipFileBytes = null;

        try {
            File mnemonicTextFile = new File("mnemonic.txt");
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mnemonicTextFile), "UTF8"));
            out.append(mnemonic).append("\r\n");
            out.flush();
            out.close();

            ZipFile mnemonicZipFile = new ZipFile("mnemonic.zip");

            ArrayList<File> files = new ArrayList<File>();
            files.add(mnemonicTextFile);

            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword(passphrase);
            mnemonicZipFile.addFiles(files, parameters);

            Path path = mnemonicZipFile.getFile().toPath();
            zipFileBytes = Files.readAllBytes(path);

            mnemonicTextFile.delete();
            mnemonicZipFile.getFile().delete();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return zipFileBytes;
    }
}
