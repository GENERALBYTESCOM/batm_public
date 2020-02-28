package com.generalbytes.batm.server.extensions.extra.bitcoincash;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.bitrafael.api.wallet.bch.Bech32;

import java.util.Arrays;
import java.util.Objects;

public class BitcoinCashAddress {

    private final String legacy;
    private final String bitcoincash; // without prefix
    private final String simpleledger; // without prefix

    /**
     * @param input address in legacy, bitcoincash or simpleledger format, with human readable prefix or without
     * @return object containing the same input address in all three formats
     * @throws AddressFormatException
     */
    public static BitcoinCashAddress valueOf(String input) throws AddressFormatException {
        if (input == null) {
            throw new AddressFormatException("null input");
        }
        if (input.isEmpty()) {
            throw new AddressFormatException("empty input");
        }
        final byte[] payload;
        final int legacyVersion;
        if (input.startsWith("3") || input.startsWith("1")) {
            byte[] decoded = Base58.decodeChecked(input);
            legacyVersion = decoded[0];
            payload = Arrays.copyOfRange(decoded, 1, decoded.length);
        } else if (!input.contains(":") && (input.startsWith("p") || input.startsWith("q"))) {
            legacyVersion = input.startsWith("p") ? 5 : 0;
            if (Bech32.isValidCashAddress("bitcoincash:" + input)) {
                payload = Bech32.decodeCashAddress("bitcoincash:" + input);
            } else if (Bech32.isValidCashAddress("simpleledger:" + input)) {
                payload = Bech32.decodeCashAddress("simpleledger:" + input);
            } else {
                throw new AddressFormatException("unknown human readable part");
            }
        } else if (input.contains(":") && Bech32.isValidCashAddress(input)) {
            legacyVersion = input.contains(":p") ? 5 : 0;
            payload = Bech32.decodeCashAddress(input);
        } else {
            throw new AddressFormatException("unknown format");
        }

        int cashVersion = legacyVersion == 5 ? 8 : 0;


        byte[] legacyData = new byte[payload.length + 1];
        legacyData[0] = (byte) legacyVersion;
        System.arraycopy(payload, 0, legacyData, 1, payload.length);

        return new BitcoinCashAddress(
            Base58.encodeChecked(legacyData),
            Bech32.encodeHashToBech32Address("bitcoincash", cashVersion, payload),
            Bech32.encodeHashToBech32Address("simpleledger", cashVersion, payload));

    }


    /**
     * @param legacy
     * @param bitcoincash  without prefix
     * @param simpleledger without prefix
     */
    public BitcoinCashAddress(String legacy, String bitcoincash, String simpleledger) throws AddressFormatException {
        this.legacy = Objects.requireNonNull(legacy);
        this.bitcoincash = Objects.requireNonNull(bitcoincash);
        this.simpleledger = Objects.requireNonNull(simpleledger);
        if (this.simpleledger.contains(":") || this.bitcoincash.contains(":")) {
            throw new AddressFormatException("This constructor parameters are required to be without the human readable prefix");
        }
    }

    /**
     * @param address address in legacy, bitcoincash or simpleledger format, with human readable prefix or without
     * @throws AddressFormatException
     */
    public BitcoinCashAddress(String address) throws AddressFormatException {
        BitcoinCashAddress converted = valueOf(address);
        this.legacy = converted.getLegacy();
        this.bitcoincash = converted.getBitcoincash(false);
        this.simpleledger = converted.getSimpleledger(false);
    }

    public String getLegacy() {
        return legacy;
    }

    public String getBitcoincash(boolean includePrefix) {
        return includePrefix ? "bitcoincash:" + bitcoincash : bitcoincash;
    }

    public String getSimpleledger(boolean includePrefix) {
        return includePrefix ? "simpleledger:" + simpleledger : simpleledger;
    }

}
