package com.generalbytes.batm.server.coinutil;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LnInvoiceUtilTest {
    private final LnInvoiceUtil util = new LnInvoiceUtil();

    @Test
    void zeroAmount() {
        assertThat(util.getAmount("lnbc1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdpl2pkx2ctnv5sxxmmwwd5kgetjypeh2ursdae8g6twvus8g6rfwvs8qun0dfjkxaq8rkx3yf5tcsyz3d73gafnh3cax9rn449d9p5uxz9ezhhypd0elx87sjle52x86fux2ypatgddc6k63n7erqz25le42c4u4ecky03ylcqca784w")).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void lnbc11() { // 11 BTC, no multiplier
        assertThat(util.getAmount("lnbc111ps0aznupp5ueywkfv97xmpauhrneqjh7vuwqemjgsksg0rm89weu38v06r0ttsdqqcqzzgxqrrssrzjqw8c7yfutqqy3kz8662fxutjvef7q2ujsxtt45csu0k688lkzu3ld63fm02a9cknhcqqqqryqqqqthqqpysp5n0nz9acp8lag87yxqs6akqk7hzssmztwze6pjecuu0k8xkxndh9q9qypqsqr5knmj4c75deeut20ye7sv24sut3a39jw0z2sxcjn53marau0a8s5xvp8f07hkdfgeggrf98n067wlrzgnfefhzeztz74mks94uaz0qpzf9azn")).isEqualByComparingTo("11");
    }

    @Test
    void lnbc20m() {
        assertThat(util.getAmount("lnbc20m1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqhp58yjmdan79s6qqdhdzgynm4zwqd5d7xmw5fk98klysy043l2ahrqscc6gd6ql3jrc5yzme8v4ntcewwz5cnw92tz0pc8qcuufvq7khhr8wpald05e92xw006sq94mg8v2ndf4sefvf9sygkshp5zfem29trqq2yxxz7")).isEqualByComparingTo("0.020");
    }

    @Test
    void lnbc2500u() {
        assertThat(util.getAmount("lnbc2500u1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdq5xysxxatsyp3k7enxv4jsxqzpuaztrnwngzn3kdzw5hydlzf03qdgm2hdq27cqv3agm2awhz5se903vruatfhq77w3ls4evs3ch9zw97j25emudupq63nyw24cg27h2rspfj9srp")).isEqualByComparingTo("0.002500");
    }


    @Test
    void lnbc5555550n() {
        assertThat(util.getAmount("lnbc5555550n1ps0azvfpp5jtfmhd3qtxa4txxnvm28fv52a0pr9kalvzjan2e62d4gl7h3m2xsdqu2askcmr9wssx7e3q2dshgmmndp5scqzpgxqyz5vqsp5vqwujjv6cka03aryl4znur6nxu8w2f3qg4s4faz6tuny026uepeq9qyyssq5646c8w8jrnea4wg3nd8yc4zytqz82hnvaywmplk0vlvpvfe0uxr6qgghksjc49nrcwqk9vxc5krlxsee3ngjjg0j0hs4t8458wedjsqh50wrc")).isEqualByComparingTo("0.005555550");
    }

    @Test
    void lnbc9678785340p() {
        assertThat(util.getAmount("lnbc9678785340p1pwmna7lpp5gc3xfm08u9qy06djf8dfflhugl6p7lgza6dsjxq454gxhj9t7a0sd8dgfkx7cmtwd68yetpd5s9xar0wfjn5gpc8qhrsdfq24f5ggrxdaezqsnvda3kkum5wfjkzmfqf3jkgem9wgsyuctwdus9xgrcyqcjcgpzgfskx6eqf9hzqnteypzxz7fzypfhg6trddjhygrcyqezcgpzfysywmm5ypxxjemgw3hxjmn8yptk7untd9hxwg3q2d6xjcmtv4ezq7pqxgsxzmnyyqcjqmt0wfjjq6t5v4khxxqyjw5qcqp2rzjq0gxwkzc8w6323m55m4jyxcjwmy7stt9hwkwe2qxmy8zpsgg7jcuwz87fcqqeuqqqyqqqqlgqqqqn3qq9qn07ytgrxxzad9hc4xt3mawjjt8znfv8xzscs7007v9gh9j569lencxa8xeujzkxs0uamak9aln6ez02uunw6rd2ht2sqe4hz8thcdagpleym0j")).isEqualByComparingTo("0.009678785340");
    }

    @Test
    void testnetPrefix() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> util.getAmount("lntb20m1pvjluezhp58yjmdan79s6qqdhdzgynm4zwqd5d7xmw5fk98klysy043l2ahrqspp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqfpp3x9et2e20v6pu37c5d9vax37wxq72un98kmzzhznpurw9sgl2v0nklu2g4d0keph5t7tj9tcqd8rexnd07ux4uv2cjvcqwaxgj7v4uwn5wmypjd5n69z2xm3xgksg28nwht7f6zspwp3f9t"));
        assertEquals("Failed to match HRP pattern", e.getMessage());
    }

    @Test
    void invalidChecksum() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> util.getAmount("lnbc2500u1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdpquwpc4curk03c9wlrswe78q4eyqc7d8d0xqzpuyk0sg5g70me25alkluzd2x62aysf2pyy8edtjeevuv4p2d5p76r4zkmneet7uvyakky2zr4cusd45tftc9c5fh0nnqpnl2jfll544esqchsrnt"));
        assertEquals("Cannot decode invoice", e.getMessage());
    }

    @Test
    void malformedBech32() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> util.getAmount("pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdpquwpc4curk03c9wlrswe78q4eyqc7d8d0xqzpuyk0sg5g70me25alkluzd2x62aysf2pyy8edtjeevuv4p2d5p76r4zkmneet7uvyakky2zr4cusd45tftc9c5fh0nnqpnl2jfll544esqchsrny"));
        assertEquals("Cannot decode invoice", e.getMessage());
    }

    @Test
    void mixedCase() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> util.getAmount("LNBC2500u1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdpquwpc4curk03c9wlrswe78q4eyqc7d8d0xqzpuyk0sg5g70me25alkluzd2x62aysf2pyy8edtjeevuv4p2d5p76r4zkmneet7uvyakky2zr4cusd45tftc9c5fh0nnqpnl2jfll544esqchsrny"));
        assertEquals("Cannot decode invoice", e.getMessage());
    }

    @Test
    void invalidMultiplier() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> util.getAmount("lnbc2500x1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdq5xysxxatsyp3k7enxv4jsxqzpujr6jxr9gq9pv6g46y7d20jfkegkg4gljz2ea2a3m9lmvvr95tq2s0kvu70u3axgelz3kyvtp2ywwt0y8hkx2869zq5dll9nelr83zzqqpgl2zg"));
        assertEquals("Failed to match HRP pattern", e.getMessage());
    }

    @Test
    void invalidPrecision() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> util.getAmount("lnbc2500000001p1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdq5xysxxatsyp3k7enxv4jsxqzpu7hqtk93pkf7sw55rdv4k9z2vj050rxdr6za9ekfs3nlt5lr89jqpdmxsmlj9urqumg0h9wzpqecw7th56tdms40p2ny9q4ddvjsedzcplva53s"));
        assertEquals("sub-millisatoshi amount", e.getMessage());
    }

    @Test
    void find() {
        assertEquals("lnbc2500000001p1pvjluez", util.findInvoice("aaaa bbbb lnbc2500000001p1pvjluez cccc dddd"));
        assertEquals("lnbc2500000001p1pvjluez", util.findInvoice("lnbc2500000001p1pvjluez"));
        assertEquals("lnbc2500000001p1pvjluez", util.findInvoice("  \t lnbc2500000001p1pvjluez    \n\t"));
        assertEquals("lnbc1p1aaaa", util.findInvoice("  \t lnbc1p1aaaa    \n\t lnbc2p1bbb"));
    }

    @Test
    void findNull() {
        assertNull(util.findInvoice(null));
        assertNull(util.findInvoice(""));
        assertNull(util.findInvoice("    \t    "));
        assertNull(util.findInvoice("     aaaaaaa   "));
        assertNull(util.findInvoice("lntb2500000001p1pvjl"));
        assertNull(util.findInvoice("lnbc250000000x1pvjluez"));
    }
}