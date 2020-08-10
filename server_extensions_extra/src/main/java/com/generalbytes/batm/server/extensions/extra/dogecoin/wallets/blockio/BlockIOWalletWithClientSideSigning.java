/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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

package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ICanSendMany;
import com.generalbytes.batm.server.extensions.IWallet;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.QueryParam;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.IBlockIO.PRIORITY_HIGH;
import static com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.IBlockIO.PRIORITY_LOW;
import static com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.IBlockIO.PRIORITY_MEDIUM;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BlockIOWalletWithClientSideSigning implements IWallet, ICanSendMany {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.BlockIOWallet2");
    private ObjectMapper jsonObjectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private String pin;
    private String priority;

    protected IBlockIO api;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    public BlockIOWalletWithClientSideSigning(String apiKey, String pin, String priority) {
        this.pin = pin;
        if (priority == null) {
            this.priority = PRIORITY_LOW;
        } else if (PRIORITY_LOW.equalsIgnoreCase(priority.trim())) {
            this.priority = PRIORITY_LOW;
        } else if (PRIORITY_MEDIUM.equalsIgnoreCase(priority.trim())) {
            this.priority = PRIORITY_MEDIUM;
        } else if (PRIORITY_HIGH.equalsIgnoreCase(priority.trim())) {
            this.priority = PRIORITY_HIGH;
        } else {
            this.priority = PRIORITY_LOW;
        }

        ClientConfig config = new ClientConfig();
        config.addDefaultParam(QueryParam.class, "api_key", apiKey);
        api = RestProxyFactory.createProxy(IBlockIO.class, "https://block.io", config);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.DOGE.getCode());
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CryptoCurrency.BTC.getCode();
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        try {
            BlockIOResponseAddresses response = api.getAddresses();
            if (response != null && response.getData() != null && response.getData().getAddresses() != null && response.getData().getAddresses().length > 0) {
                return response.getData().getAddresses()[0].getAddress();
            }
        } catch (HttpStatusIOException e) {
            log.error("HTTP error in getCryptoAddress: {}", e.getHttpBody());
        } catch (Exception e) {
            log.error("Error", e);
        }

        return null;
    }


    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        try {
            BlockIOResponseBalance response = api.getBalance();
            if (response != null && response.getData() != null && response.getData().getAvailable_balance() != null) {
                return new BigDecimal(response.getData().getAvailable_balance());
            }
        } catch (HttpStatusIOException e) {
            log.error("HTTP error in getCryptoBalance: {}", e.getHttpBody());
        } catch (Exception e) {
            log.error("Error", e);
        }

        return null;
    }

    @Override
    public String sendMany(Collection<Transfer> transfers, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        try {
            // sum amounts for the same address - this wallet cannot send multiple amounts to the same address
            Map<String, BigDecimal> destinationAddressAmounts = transfers.stream()
                .collect(Collectors.toMap(Transfer::getDestinationAddress, Transfer::getAmount, BigDecimal::add));
            List<BigDecimal> amounts = destinationAddressAmounts.values().stream()
                .map(amount -> amount.setScale(8, RoundingMode.FLOOR))
                .collect(Collectors.toList());
            List<String> toAddresses = new ArrayList<>(destinationAddressAmounts.keySet());
            log.info("{} calling withdraw {} to {}", getClass().getSimpleName(), amounts, toAddresses);
            BlockIOResponseWithdrawalToBeSigned response = api.withdrawToAddressesToBeSigned(amounts, toAddresses, priority);
            if (response != null && response.getStatus() != null && "success".equalsIgnoreCase(response.getStatus()) && response.getData() != null) {
                log.debug("Block.io reference_id = " + response.getData().getReference_id());
                BlockIOInput[] inputs = response.getData().getInputs();
                for (int i = 0; i < inputs.length; i++) {
                    BlockIOInput input = inputs[i];
                    BlockIOSigner[] signers = input.getSigners();
                    for (int j = 0; j < signers.length; j++) { //TODO: currently works only with one signer
                        BlockIOSigner signer = signers[j];
                        byte[] transactionSignature = signTransaction(
                            Hex.decode(input.data_to_sign),
                            Hex.decode(signer.getSigner_public_key()),
                            response.getData().getEncrypted_passphrase().getPassphrase(),
                            pin);

                        if (transactionSignature == null) {
                            return null;
                        }

                        signer.signed_data = toHex(transactionSignature);
                    }
                }
                BlockIOResponseWithdrawal resp = api.signAndFinalizeWithdrawal(jsonObjectMapper.writeValueAsString(response.getData()));
                if (resp != null && resp.getStatus() != null && "success".equalsIgnoreCase(resp.getStatus()) && resp.getData() != null) {
                    return resp.getData().getTxid();
                }
                return null;

            }
        } catch (HttpStatusIOException e) {
            log.error("HTTP error in sendCoins: {}", e.getHttpBody());
        } catch (Exception e) {
            log.error("Error", e);
        }

        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        return sendMany(Collections.singleton(new Transfer(destinationAddress, amount)), cryptoCurrency, description);
    }

    private static byte[] signTransaction(byte[] dataToSign, byte[] expectedSignerPublicKey, String passphrase, String pin) {
        //step 1
        final byte[] emptySalt = "".getBytes(UTF_8);
        final byte[] derived1 = pbkdf2WithHmacSha256(pin.getBytes(UTF_8), emptySalt, 128, 1024);
        final String derivedHex1 = toHex(derived1);

        //step 2
        final byte[] derived2 = pbkdf2WithHmacSha256(derivedHex1.getBytes(UTF_8), emptySalt, 256, 1024);

        //step 7
        final byte[] decryptedPassphrase = decryptAesEcb(Base64.getDecoder().decode(passphrase), derived2);

        //step 8
        final byte[] privateKeyBytes = sha256(Hex.decode(decryptedPassphrase));

        //step 10
        final byte[] publicKey = deriveEccPublicKeyFromPrivateKey(privateKeyBytes, "secp256k1");
        if (!Arrays.equals(publicKey, expectedSignerPublicKey)){
            log.error("Error signing block.io transaction. " +
                " Calculated publicKey " + toHex(publicKey) + " vs expected publicKey " + toHex(expectedSignerPublicKey) +
                ". Expected signer public key doesn't match calculated. Wrong pin or more than 1 signers?");
//            System.exit(1);
            return null;
        }

        //step 13
        return sign(dataToSign, privateKeyBytes, "secp256k1");
    }


    /*
    public static void main(String[] args) {
        final BlockIOWalletWithClientSideSigning blockIOWallet = new BlockIOWalletWithClientSideSigning("xxxx", "xxx", "medium");
        final BigDecimal ltc = blockIOWallet.getCryptoBalance("LTC");
        System.out.println("ltc = " + ltc);
        String address  = blockIOWallet.getCryptoAddress("LTC");
        System.out.println("address = " + address);
        String s = blockIOWallet.sendCoins("LSYi8VQjbR3LAAdqkd4jSzj3Ci5B9Pryvk", new BigDecimal("0.008"), "LTC", "blabla");
        System.out.println("s = " + s);

        final byte[] emptySalt = "".getBytes(UTF_8);
        final byte[] emptyIv = new byte[16];
        final String pin = "xxxx";
        // passhprase from response of GET https://block.io/api/v2/withdraw/?api_key=xxx&amounts=0.49&to_addresses=MWEGe5SdhAhVk1AAJ6YR6k6iDaa6deCNXz&priority=medium
        final String passphrase = "4cAMGWkTD9AI1Ot1wyCeBdORrS+a3FSX7mNZVbF6fQTaP3kp5Z4uNmmAYiIWvmSUrjv3I6mkN6xadclxHPbvpBeOskSGTLUgpdHhxMM3lnS2x1pp9o77jN7kAj5LMTwWlN1SFLpr+n0FARuJBuRp/1GGEMCth2mxOsrml7JfWDaJoetjPD4AhAurhAUyFxA0";

        // step 1
        System.out.println("Step 1:");;
        final byte[] derived1 = pbkdf2WithHmacSha256(pin.getBytes(UTF_8), emptySalt, 128, 1024);
        final byte[] derived1Example = pbkdf2WithHmacSha256("7a318f6d01f92eb8cee8b96ae4486c56".getBytes(UTF_8), emptySalt, 128, 1024);
        final String derivedHex1 = toHex(derived1);
        final String derivedHex1Example = toHex(derived1Example);
        System.out.println("derivedHex1 = " + derivedHex1);
        System.out.println("derivedHex1Example = " + derivedHex1Example);
        System.out.println();

        // step 2
        System.out.println("Step 2:");

        final byte[] derived2 = pbkdf2WithHmacSha256(derivedHex1.getBytes(UTF_8), emptySalt, 256, 1024);
        final byte[] derived2Example = pbkdf2WithHmacSha256(derivedHex1Example.getBytes(UTF_8), emptySalt, 256, 1024);
        final String derivedHex2 = toHex(derived2);
        final String derivedHex2Example = toHex(derived2Example);
        System.out.println("derivedHex2 = " + derivedHex2);
        System.out.println("derivedHex2Example = " + derivedHex2Example);
        System.out.println();

        // step 5
        System.out.println("Step 5:");
        final byte[] encryptedExample = encryptAesEcb("block.io".getBytes(UTF_8), derived2Example);
        final String encryptedBase64Example = Base64.getEncoder().encodeToString(encryptedExample);
        System.out.println("encryptedBase64Example = " + encryptedBase64Example);
        System.out.println();

        // step 7
        System.out.println("Step 7:");;
        final byte[] decryptedExample = decryptAesEcb(encryptedExample, derived2Example);
        final byte[] decryptedPassphrase = decryptAesEcb(Base64.getDecoder().decode(passphrase), derived2);
        final String decryptedStringExample = new String(decryptedExample, UTF_8);
        final String decryptedPassphraseString = new String(decryptedPassphrase, UTF_8);
        System.out.println("decryptedPassphrase = " + decryptedPassphraseString);
        System.out.println("decryptedExample = " + decryptedStringExample);
        System.out.println();

        // step 8
        System.out.println("Step 8:");
        final byte[] hashedString = sha256(Hex.decode(decryptedPassphrase)); //NOTICE that we are unhexifying (this is missing in documentation)
        byte[] privateKeyBytes = hashedString;
        final byte[] hashedStringExample = sha256(decryptedExample);
        byte[] privateKeyBytesExample = hashedStringExample;
        System.out.println("hashedString = " + toHex(privateKeyBytes));
        System.out.println("hashedStringExample = " + toHex(privateKeyBytesExample));

        // step 10
        System.out.println("Step 10:");
        final byte[] publicKey = deriveEccPublicKeyFromPrivateKey(privateKeyBytes, "secp256k1");
        final byte[] publicKeyExample = deriveEccPublicKeyFromPrivateKey(privateKeyBytesExample, "secp256k1");
        System.out.println("publicKey = " + toHex(publicKey));
        System.out.println("publicKeyExample = " + toHex(publicKeyExample));

        if (!toHex(publicKey).equalsIgnoreCase("035ad9fa2ce8b272197a5d786727453f3b75a79b258ac4617fdbf74b3e57dfa9a6")) {
            System.out.println("\nERROR: Public key is not matching expected 035ad9fa2ce8b272197a5d786727453f3b75a79b258ac4617fdbf74b3e57dfa9a6.");
            System.exit(1);
        }
        // step 12
        System.out.println("Step 12:");
        byte[] dataToSignExample = "iSignedThisDataThatIs256BitsLong".getBytes(UTF_8);
        System.out.println("dataToSignExample = " + toHex(dataToSignExample));

        // step 13
        System.out.println("Step 13:");
        byte[] signatureExample = sign(dataToSignExample, privateKeyBytesExample, "secp256k1");
        System.out.println("signatureExample = " + toHex(signatureExample));

        IBlockIO api = RestProxyFactory.createProxy(IBlockIO.class, "https://block.io");
        BlockIOResponseVerify verify = api.verify(toHex(dataToSignExample), toHex(signatureExample), toHex(publicKeyExample));
        System.out.println("verify = " + verify.getData().isIs_valid());


    }

    */
    public static byte[] sign(byte[] input, byte[] privateKeyBytes, String curveName) {
        final ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(curveName);
        ECDomainParameters ecParams = new ECDomainParameters(ecSpec.getCurve(), ecSpec.getG(), ecSpec.getN(), ecSpec.getH());

        BigInteger privateKey = new BigInteger(1, privateKeyBytes);
        if (privateKey.bitLength() > ecSpec.getN().bitLength()) {
            privateKey = privateKey.mod(ecSpec.getN());
        }

        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKey, ecParams);
        ECDSASigner signer = new ECDSASigner();
        signer.init(true, privKey);
        BigInteger[] sigs = signer.generateSignature(input);

        BigInteger r = sigs[0];
        BigInteger s = sigs[1];

        if (s.compareTo(ecParams.getN().shiftRight(1)) > 0) { // s > N/2
            //see bip-0062
            s = ecParams.getN().subtract(s);
        }

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            DERSequenceGenerator seq = new DERSequenceGenerator(output);

            seq.addObject(new ASN1Integer(r));
            seq.addObject(new ASN1Integer(s));
            seq.close();
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    public static byte[] pbkdf2WithHmacSha256(byte[] input, byte[] salt, int keyLength, int iterations) {
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
        generator.init(input, salt, iterations);

        final CipherParameters cipherParameters = generator.generateDerivedParameters(keyLength);
        return ((KeyParameter) cipherParameters).getKey();
    }

    public static byte[] encryptAesEcb(byte[] data, byte[] key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new IllegalStateException("Can't encrypt data.", e);
        }
    }

    public static byte[] decryptAesEcb(byte[] data, byte[] key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            return cipher.doFinal(data);
        } catch (BadPaddingException e) {
            throw new IllegalStateException("wrong PIN?", e); // pin is not checked in CAS crypto settings tests
        } catch (Exception e) {
            throw new IllegalStateException("Can't decrypt data.", e);
        }
    }

    public static byte[] sha256(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input);
            return md.digest();
        } catch(Exception e) {
            throw new IllegalStateException("Can't compute SHA256", e);
        }
    }

    public static byte[] deriveEccPublicKeyFromPrivateKey(byte[] privKeyBytes, String curveName) {
        try {
            final ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(curveName);
            BigInteger privKey = new BigInteger(1, privKeyBytes);
            if (privKey.bitLength() > ecSpec.getN().bitLength()) {
                privKey = privKey.mod(ecSpec.getN());
            }
            ECPoint pubKey = new FixedPointCombMultiplier().multiply(ecSpec.getG(), privKey);
            return pubKey.getEncoded(true);
        } catch (Exception e) {
            throw new IllegalStateException("Can't derive public key.", e);
        }
    }

}