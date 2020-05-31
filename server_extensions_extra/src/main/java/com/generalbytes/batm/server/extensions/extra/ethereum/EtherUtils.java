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
package com.generalbytes.batm.server.extensions.extra.ethereum;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.bitrafael.tools.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.tools.wallet.eth.MasterPrivateKeyETH;
import com.generalbytes.bitrafael.tools.wallet.eth.WalletToolsETH;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;


import org.web3j.crypto.Hash;

import java.security.SecureRandom;

import static org.web3j.crypto.Hash.sha256;

public class EtherUtils {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String ETHEREUM_WALLET_PASSWORD = "";
    private static final String HEX_CHARS = "0123456789abcdef";

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String generateMnemonic(){
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);
        return MnemonicUtils.generateMnemonic(initialEntropy);
    }

    public static String generateMnemonicFromPassword(String userPassword){
        byte[] entropy = userPassword.getBytes();
        for (int i=0;i<1000;i++) {
            entropy = Hash.sha256(entropy);
        }
        return MnemonicUtils.generateMnemonic(entropy);
    }

    public static Credentials loadBip39Credentials(String mnemonic, String password) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        return Credentials.create(ECKeyPair.create(sha256(seed)));
    }

    public static Credentials loadBip44Credentials(String mnemonic, String password) {
        WalletToolsETH wt = new WalletToolsETH();
        MasterPrivateKeyETH m = wt.getMasterPrivateKey(mnemonic, password, CryptoCurrency.ETH.getCode(), IWalletTools.STANDARD_BIP44);
        String walletPrivateKey = wt.getWalletPrivateKey(m, CryptoCurrency.ETH.getCode(), 0, 0, 0);
        return Credentials.create(walletPrivateKey);
    }



    public static boolean isEtherAddressValid (String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        address = address.trim();

        byte[] addrBytes = decodeAddressAsBytes(address);
        if (addrBytes != null) {
            if (address.equals(address.toLowerCase()) || address.equals(address.toUpperCase())) {
                //address doesn't contain checksum
                return true;
            }else{
                //if address contains checksum, we should check that too
                final String encodedAddress = encodeAddressToChecksumedAddress(addrBytes);
                if (address.equals(encodedAddress)){
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
    }

    public static String encodeAddressToChecksumedAddress(byte[] addrBytes) {
        String address = bytesToHexString(addrBytes);
        return encodeAddressToChecksumedAddress(address);
    }

    public static String encodeAddressToChecksumedAddress(String address) {
        if (address == null) {
            return null;
        }
        address = address.trim();
        if (address.isEmpty()) {
            return null;
        }

        final String addressHash = bytesToHexString(Hash.sha3(address.toLowerCase().getBytes()));

        String checksumAddress ="";

        final char[] addrChars = address.toCharArray();
        final char[] addrHashChars = addressHash.toCharArray();

        for (int i = 0; i < addrChars.length; i++ ) {
            // If ith character is 9 to f then make it uppercase
            if (Integer.parseInt((addrHashChars[i]+""), 16) > 7) {
                checksumAddress += (addrChars[i] +"").toUpperCase();
            } else {
                checksumAddress += (addrChars[i] +"").toLowerCase();
            }
        }
        return "0x"+checksumAddress;
    }

    public static byte[] decodeAddressAsBytes(String address) {
        if (address == null) {
            return null;
        }
        address = address.trim();

        if (address.toLowerCase().startsWith("0x")) {
            address = address.substring(2);
        }

        if (address.length() == 42) {
            //probably this format 0xf8b483DbA2c3B7176a3Da549ad41A48BB3121069
            if (address.toLowerCase().startsWith("0x")) {
                address = address.substring(2);
            }
        }

        if (address.length() == 40) {
            //probably this format f8b483DbA2c3B7176a3Da549ad41A48BB3121069
            if (isAllLowerCaseHex(address.toLowerCase())) {
                return hexStringToByteArray(address);
            }
        }
        return null;
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static boolean isAllLowerCaseHex(String string) {
        final char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (HEX_CHARS.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

//    public static void main(String[] args) {
//        Credentials credentials = EtherUtils.loadBip44Credentials("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about","");
//        String address = credentials.getAddress();
//        log.info("address = " + address);
//    }
}
