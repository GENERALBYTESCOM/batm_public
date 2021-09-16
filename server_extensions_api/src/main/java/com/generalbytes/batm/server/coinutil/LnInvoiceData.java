package com.generalbytes.batm.server.coinutil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements LN invoice data part as specified here:
 * https://github.com/lightningnetwork/lightning-rfc/blob/38abac62065172c00722dca10e7d3fc3049afd72/11-payment-encoding.md#data-part
 * For the human readable part (HRP; invoice amount) see {@link LnInvoiceUtil}
 */
public class LnInvoiceData {

    private final int timestamp;
    private final Map<TagType, Collection<Tag>> tags;
    private final byte[] signatureR;
    private final byte[] signatureS;
    private final int recoveryFlag;

    public LnInvoiceData(int timestamp, Map<TagType, Collection<Tag>> tags, byte[] signatureR, byte[] signatureS, int recoveryFlag) {
        this.timestamp = timestamp;
        this.tags = tags;
        this.signatureR = signatureR;
        this.signatureS = signatureS;
        this.recoveryFlag = recoveryFlag;
    }

    public static LnInvoiceData from(String invoice) throws AddressFormatException {
        return new LnInvoiceDataParser(invoice).parse();
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Map<TagType, Collection<Tag>> getTags() {
        return tags;
    }

    public Collection<Tag> getTags(TagType tagType) {
        return tags.get(tagType);
    }

    public Tag getTag(TagType tagType) {
        Collection<Tag> tags = getTags(tagType);
        if (tags == null || tags.size() != 1) {
            throw new IllegalArgumentException(tagType + " tags present: " + (tags == null ? null : tags.size()));
        }
        return tags.iterator().next();
    }

    public byte[] getSignatureR() {
        return signatureR;
    }

    public byte[] getSignatureS() {
        return signatureS;
    }

    public int getRecoveryFlag() {
        return recoveryFlag;
    }

    public enum TagType {
        /**
         * Preimage of this provides proof of payment
         */
        payment_hash('p', 1),
        /**
         * prevents forwarding nodes from probing the payment recipient
         */
        secret('s', 16),
        /**
         * Short description of purpose of payment (UTF-8)
         */
        description('d', 13),
        /**
         * public key of the payee node
         */
        node('n', 19),
        /**
         * description of purpose of payment (SHA256).
         * This is used to commit to an associated description that is over 639 bytes
         */
        description_hash('h', 23),
        /**
         * expiry time in seconds (big-endian). Default is 3600 (1 hour) if not specified.
         */
        expiry('x', 6),
        /**
         * min_final_cltv_expiry to use for the last HTLC in the route. Default is 18 if not specified
         */
        min_final_cltv_expiry('c', 24),
        /**
         * Fallback on-chain address: for Bitcoin, this starts with a 5-bit version and contains a witness program or P2PKH or P2SH address
         */
        fallback_address('f', 9),
        /**
         * One or more entries containing extra routing information for a private route; there may be more than one r field
         */
        routing('r', 3),
        /**
         * One or more 5-bit values containing features supported or required for receiving this payment
         */
        features('9', 5),
        ;
        private static final Map<Character, TagType> byCode = new HashMap<>();
        private static final Map<Integer, TagType> byValue = new HashMap<>();

        static {
            for (TagType t : values()) {
                byCode.put(t.code, t);
                byValue.put(t.value, t);
            }
        }

        private final char code;
        private final int value;

        TagType(char code, int value) {
            this.code = code;
            this.value = value;
        }

        public static TagType of(char code) {
            return byCode.get(code);
        }

        public static TagType of(int value) {
            return byValue.get(value);
        }

        public char getCode() {
            return code;
        }

        public int getValue() {
            return value;
        }
    }

    public static class Tag {
        private final TagType type;
        private final String stringValue;
        private final Integer intValue;
        private final byte[] data;

        private Tag(TagType type, byte[] data, String stringValue, Integer intValue) {
            this.type = type;
            this.data = data;
            this.stringValue = stringValue;
            this.intValue = intValue;
        }

        public Tag(TagType type, byte[] data) {
            this(type, data, null, null);
        }

        public Tag(TagType type, int intValue) {
            this(type, null, null, intValue);
        }

        public Tag(TagType type, String stringValue) {
            this(type, null, stringValue, null);
        }

        public TagType getType() {
            return type;
        }

        public byte[] getData() {
            return data;
        }

        public String getStringValue() {
            return stringValue;
        }

        public Integer getIntValue() {
            return intValue;
        }
    }

    private static class LnInvoiceDataParser {
        private static final int WORD = 5;
        private static final int BYTE = 8;
        private static final int SIGNATURE_BITS = 520;
        private static final int SIGNATURE_WORDS = SIGNATURE_BITS / WORD;

        private final byte[] data;

        private int i;

        public LnInvoiceDataParser(String invoice) throws AddressFormatException {
            this.data = Bech32.decodeUnlimitedLength(invoice).data;
        }

        public LnInvoiceData parse() {
            i = 0;

            int timestamp = consume(7);

            Map<TagType, Collection<Tag>> tags = new HashMap<>();
            while (i < data.length - SIGNATURE_WORDS) {
                Tag tag = parseTag();
                tags.computeIfAbsent(tag.getType(), tt -> new ArrayList<>()).add(tag);
            }

            byte[] signature = Bech32.convertBits(Arrays.copyOfRange(data, i, i + SIGNATURE_WORDS), WORD, BYTE, true);
            i += SIGNATURE_WORDS;
            byte[] signatureR = Arrays.copyOfRange(signature, 0, 32);
            byte[] signatureS = Arrays.copyOfRange(signature, 32, 64);
            int recoveryFlag = signature[64];
            assert i == data.length;

            return new LnInvoiceData(timestamp, tags, signatureR, signatureS, recoveryFlag);
        }

        private Tag parseTag() {
            int tagTypeValue = consume(1);
            TagType tagType = TagType.of(tagTypeValue);
            int tagLength = consume(2);
            byte[] tagData = Arrays.copyOfRange(this.data, i, i + tagLength);
            i += tagLength;
            return getTag(tagType, tagData);
        }

        private Tag getTag(TagType tagType, byte[] tagData) {
            switch (tagType) {
                case payment_hash:
                case secret:
                case node:
                case description_hash:
                    return new Tag(tagType, Bech32.convertBits(tagData, WORD, BYTE, false));
                case description:
                    return new Tag(tagType, new String(Bech32.convertBits(tagData, WORD, BYTE, false), StandardCharsets.UTF_8));
                case expiry:
                case min_final_cltv_expiry:
                    return new Tag(tagType, convertToIntBigEndian(tagData));
                case features:
                    return new Tag(tagType, tagData); // keep original
                case fallback_address: // not implemented
                case routing: // not implemented
                default:
                    return new Tag(tagType, tagData);
            }
        }

        private int convertToIntBigEndian(byte[] data) {
            int res = 0;
            for (byte b : data) {
                res <<= WORD;
                res += b;
            }
            return res;
        }

        private int consume(int words) {
            int res = 0;
            for (int j = 0; j < words; j++) {
                res <<= WORD;
                res += data[i++];
            }
            return res;
        }
    }
}
