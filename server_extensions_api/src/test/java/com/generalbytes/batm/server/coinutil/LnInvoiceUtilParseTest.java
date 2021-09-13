package com.generalbytes.batm.server.coinutil;

import com.generalbytes.batm.server.coinutil.LnInvoiceData.TagType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LnInvoiceUtilParseTest {
    @Test
    public void zeroAmount() throws AddressFormatException {
        LnInvoiceData d = LnInvoiceData.from("lnbc1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdpl2pkx2ctnv5sxxmmwwd5kgetjypeh2ursdae8g6twvus8g6rfwvs8qun0dfjkxaq8rkx3yf5tcsyz3d73gafnh3cax9rn449d9p5uxz9ezhhypd0elx87sjle52x86fux2ypatgddc6k63n7erqz25le42c4u4ecky03ylcqca784w");
        Assert.assertEquals(2, d.getTags().size());
        Assert.assertEquals(1, d.getTags(LnInvoiceData.TagType.payment_hash).size());
        Assert.assertEquals("0001020304050607080900010203040506070809000102030405060708090102", Hex.bytesToHexString(d.getTag(TagType.payment_hash).getData()));
        Assert.assertEquals("Please consider supporting this project", getDescription(d));
    }

    @Test
    public void lnbc11() throws AddressFormatException { // 11 BTC, no multiplier
        LnInvoiceData d = LnInvoiceData.from("lnbc111ps0aznupp5ueywkfv97xmpauhrneqjh7vuwqemjgsksg0rm89weu38v06r0ttsdqqcqzzgxqrrssrzjqw8c7yfutqqy3kz8662fxutjvef7q2ujsxtt45csu0k688lkzu3ld63fm02a9cknhcqqqqryqqqqthqqpysp5n0nz9acp8lag87yxqs6akqk7hzssmztwze6pjecuu0k8xkxndh9q9qypqsqr5knmj4c75deeut20ye7sv24sut3a39jw0z2sxcjn53marau0a8s5xvp8f07hkdfgeggrf98n067wlrzgnfefhzeztz74mks94uaz0qpzf9azn");
        Assert.assertEquals(7, d.getTags().size());
        Assert.assertEquals(1, d.getRecoveryFlag());
        Assert.assertEquals("1d2d3dcab8f51b9cf16a7933e8315587171ec4b273c4a81b129d23be8fbc7f4f", Hex.bytesToHexString(d.getSignatureR()));
        Assert.assertEquals("0a19813a5febd9a9465081a4a79bf5e77c6244d394dc5912c5eaeed02d79d13c", Hex.bytesToHexString(d.getSignatureS()));
        Assert.assertTrue(d.getTags().containsKey(TagType.routing));
        Assert.assertEquals("9be622f7013ffa83f8860435db02deb8a10d896e167419671ce3ec7358d36dca", Hex.bytesToHexString(d.getTag(TagType.secret).getData()));
        Assert.assertEquals("e648eb2585f1b61ef2e39e412bf99c7033b92216821e3d9caecf22763f437ad7", Hex.bytesToHexString(d.getTag(TagType.payment_hash).getData()));
        Assert.assertEquals(Integer.valueOf(72), d.getTag(TagType.min_final_cltv_expiry).getIntValue());
        Assert.assertEquals(Integer.valueOf(3600), d.getTag(TagType.expiry).getIntValue());
        Assert.assertArrayEquals(new byte[]{1, 0, 16, 0}, d.getTag(TagType.features).getData());
        Assert.assertEquals("", getDescription(d));

    }

    @Test
    public void hashed() throws AddressFormatException, NoSuchAlgorithmException {
        LnInvoiceData d = LnInvoiceData.from("lnbc20m1pvjluezsp5zyg3zyg3zyg3zyg3zyg3zyg3zyg3zyg3zyg3zyg3zyg3zyg3zygspp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqhp58yjmdan79s6qqdhdzgynm4zwqd5d7xmw5fk98klysy043l2ahrqs6e4fy93me7wjwdf9sxgrzr8xldm570z02ur92rv6pa7wkhzpfehnecuyhp4mdhsv5t7em4jz4tjtchs8zmx3tr555yl59lk848due0gqvkanpl");
        Assert.assertEquals(1496314658, d.getTimestamp());
        Assert.assertEquals("1111111111111111111111111111111111111111111111111111111111111111", Hex.bytesToHexString(d.getTag(TagType.secret).getData()));
        Assert.assertEquals("0001020304050607080900010203040506070809000102030405060708090102", Hex.bytesToHexString(d.getTag(TagType.payment_hash).getData()));

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] description = "One piece of chocolate cake, one icecream cone, one pickle, one slice of swiss cheese, one slice of salami, one lollypop, one piece of cherry pie, one sausage, one cupcake, and one slice of watermelon".getBytes(StandardCharsets.UTF_8);
        byte[] expected = digest.digest(description);
        Assert.assertArrayEquals(expected, d.getTag(TagType.description_hash).getData());
    }

    @Test
    public void lnbc20m() throws AddressFormatException {
        LnInvoiceData d = LnInvoiceData.from("lnbc20m1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqhp58yjmdan79s6qqdhdzgynm4zwqd5d7xmw5fk98klysy043l2ahrqscc6gd6ql3jrc5yzme8v4ntcewwz5cnw92tz0pc8qcuufvq7khhr8wpald05e92xw006sq94mg8v2ndf4sefvf9sygkshp5zfem29trqq2yxxz7");
        Assert.assertEquals(2, d.getTags().size());
        Assert.assertFalse(d.getTags().containsKey(TagType.description));
    }

    @Test
    public void lnbc2500u() throws AddressFormatException {
        LnInvoiceData d = LnInvoiceData.from("lnbc2500u1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdq5xysxxatsyp3k7enxv4jsxqzpuaztrnwngzn3kdzw5hydlzf03qdgm2hdq27cqv3agm2awhz5se903vruatfhq77w3ls4evs3ch9zw97j25emudupq63nyw24cg27h2rspfj9srp");
        Assert.assertEquals(3, d.getTags().size());
        Assert.assertEquals("1 cup coffee", getDescription(d));
    }


    @Test
    public void lnbc5555550n() throws AddressFormatException {
        LnInvoiceData d = LnInvoiceData.from("lnbc5555550n1ps0azvfpp5jtfmhd3qtxa4txxnvm28fv52a0pr9kalvzjan2e62d4gl7h3m2xsdqu2askcmr9wssx7e3q2dshgmmndp5scqzpgxqyz5vqsp5vqwujjv6cka03aryl4znur6nxu8w2f3qg4s4faz6tuny026uepeq9qyyssq5646c8w8jrnea4wg3nd8yc4zytqz82hnvaywmplk0vlvpvfe0uxr6qgghksjc49nrcwqk9vxc5krlxsee3ngjjg0j0hs4t8458wedjsqh50wrc");
        Assert.assertEquals(6, d.getTags().size());
        Assert.assertEquals("Wallet of Satoshi", getDescription(d));
    }

    @Test
    public void lnbc9678785340p() throws AddressFormatException {
        LnInvoiceData d = LnInvoiceData.from("lnbc9678785340p1pwmna7lpp5gc3xfm08u9qy06djf8dfflhugl6p7lgza6dsjxq454gxhj9t7a0sd8dgfkx7cmtwd68yetpd5s9xar0wfjn5gpc8qhrsdfq24f5ggrxdaezqsnvda3kkum5wfjkzmfqf3jkgem9wgsyuctwdus9xgrcyqcjcgpzgfskx6eqf9hzqnteypzxz7fzypfhg6trddjhygrcyqezcgpzfysywmm5ypxxjemgw3hxjmn8yptk7untd9hxwg3q2d6xjcmtv4ezq7pqxgsxzmnyyqcjqmt0wfjjq6t5v4khxxqyjw5qcqp2rzjq0gxwkzc8w6323m55m4jyxcjwmy7stt9hwkwe2qxmy8zpsgg7jcuwz87fcqqeuqqqyqqqqlgqqqqn3qq9qn07ytgrxxzad9hc4xt3mawjjt8znfv8xzscs7007v9gh9j569lencxa8xeujzkxs0uamak9aln6ez02uunw6rd2ht2sqe4hz8thcdagpleym0j");
        Assert.assertEquals(5, d.getTags().size());
        Assert.assertEquals("Blockstream Store: 88.85 USD for Blockstream Ledger Nano S x 1, \"Back In My Day\" Sticker x 2, \"I Got Lightning Working\" Sticker x 2 and 1 more items", getDescription(d));
    }

    @Test
    public void testnetPrefix() throws AddressFormatException {
        LnInvoiceData d = LnInvoiceData.from("lntb20m1pvjluezhp58yjmdan79s6qqdhdzgynm4zwqd5d7xmw5fk98klysy043l2ahrqspp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqfpp3x9et2e20v6pu37c5d9vax37wxq72un98kmzzhznpurw9sgl2v0nklu2g4d0keph5t7tj9tcqd8rexnd07ux4uv2cjvcqwaxgj7v4uwn5wmypjd5n69z2xm3xgksg28nwht7f6zspwp3f9t");
        Assert.assertEquals(3, d.getTags().size());
    }


    @Test
    public void invalidChecksum() {
        AddressFormatException e = Assert.assertThrows(AddressFormatException.class,
            () -> LnInvoiceData.from("lnbc2500u1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdpquwpc4curk03c9wlrswe78q4eyqc7d8d0xqzpuyk0sg5g70me25alkluzd2x62aysf2pyy8edtjeevuv4p2d5p76r4zkmneet7uvyakky2zr4cusd45tftc9c5fh0nnqpnl2jfll544esqchsrnt"));
        Assert.assertEquals("Invalid Checksum", e.getMessage());
    }

    @Test
    public void malformedBech32() {
        AddressFormatException e = Assert.assertThrows(AddressFormatException.class,
            () -> LnInvoiceData.from("pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdpquwpc4curk03c9wlrswe78q4eyqc7d8d0xqzpuyk0sg5g70me25alkluzd2x62aysf2pyy8edtjeevuv4p2d5p76r4zkmneet7uvyakky2zr4cusd45tftc9c5fh0nnqpnl2jfll544esqchsrny"));
        Assert.assertEquals("Missing human-readable part", e.getMessage());
    }

    @Test
    public void mixedCase() {
        AddressFormatException e = Assert.assertThrows(AddressFormatException.class,
            () -> LnInvoiceData.from("LNBC2500u1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdpquwpc4curk03c9wlrswe78q4eyqc7d8d0xqzpuyk0sg5g70me25alkluzd2x62aysf2pyy8edtjeevuv4p2d5p76r4zkmneet7uvyakky2zr4cusd45tftc9c5fh0nnqpnl2jfll544esqchsrny"));
        Assert.assertEquals("Invalid character: u, pos: 8", e.getMessage());
    }

    @Test
    public void invalidMultiplier() throws AddressFormatException {
        LnInvoiceData d = LnInvoiceData.from("lnbc2500x1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdq5xysxxatsyp3k7enxv4jsxqzpujr6jxr9gq9pv6g46y7d20jfkegkg4gljz2ea2a3m9lmvvr95tq2s0kvu70u3axgelz3kyvtp2ywwt0y8hkx2869zq5dll9nelr83zzqqpgl2zg");
        Assert.assertEquals(3, d.getTags().size());
        Assert.assertEquals("1 cup coffee", getDescription(d));
    }

    @Test
    public void invalidPrecision() throws AddressFormatException {
        LnInvoiceData d = LnInvoiceData.from("lnbc2500000001p1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdq5xysxxatsyp3k7enxv4jsxqzpu7hqtk93pkf7sw55rdv4k9z2vj050rxdr6za9ekfs3nlt5lr89jqpdmxsmlj9urqumg0h9wzpqecw7th56tdms40p2ny9q4ddvjsedzcplva53s");
        Assert.assertEquals(3, d.getTags().size());
        Assert.assertEquals("1 cup coffee", getDescription(d));
    }

    @Test
    public void cupOfNonsense() throws AddressFormatException {
        LnInvoiceData d = LnInvoiceData.from("lnbc2500u1pvjluezsp5zyg3zyg3zyg3zyg3zyg3zyg3zyg3zyg3zyg3zyg3zyg3zyg3zygspp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdpquwpc4curk03c9wlrswe78q4eyqc7d8d0xqzpuy0x9mrk2a32xsyvz6lzmrs38vzemux64pv6vdtmshnr9fc8eqgt4j74fnf3s60tzgeh6dnwzyv56wftaqwyyl3xu9nccq3py2hnnvsgqu5lqmm");
        Assert.assertEquals("ナンセンス 1杯", getDescription(d));
        Assert.assertEquals(Integer.valueOf(60), d.getTag(TagType.expiry).getIntValue());
    }

    private String getDescription(LnInvoiceData d) {
        return d.getTag(TagType.description).getStringValue();
    }

}