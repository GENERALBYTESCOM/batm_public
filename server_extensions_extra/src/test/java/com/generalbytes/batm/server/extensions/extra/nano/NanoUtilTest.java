package com.generalbytes.batm.server.extensions.extra.nano;

import com.generalbytes.batm.server.extensions.extra.nano.util.NanoUtil;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Karl Oczadly
 */
public class NanoUtilTest {

    static final NanoUtil UTIL = NanoUtil.NANO;
    static final String ACCOUNT = "nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz";


    @Test
    public void testParseAddress() {
        assertEquals(ACCOUNT, UTIL.parseAddress( // Standard nano address
                "nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertEquals(ACCOUNT, UTIL.parseAddress( // Standard xrb address
                "xrb_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertEquals(ACCOUNT, UTIL.parseAddress( // URI
                "nano:nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertEquals(ACCOUNT, UTIL.parseAddress( // URI with query
                "nano:nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz?amount=42"));
    }

    @Test
    public void testAmountToRaw() {
        assertEquals(new BigInteger("1000000000000000000000000000000"),
                UTIL.amountToRaw(BigDecimal.ONE));
        assertEquals(new BigInteger("8029000000000000000000000000000001"),
                UTIL.amountToRaw(new BigDecimal("8029.000000000000000000000000000001")));
        assertEquals(new BigInteger("1"),
                UTIL.amountToRaw(new BigDecimal("0.000000000000000000000000000001")));
        assertEquals(new BigInteger("340282366920938463463374607431768211455"),
                UTIL.amountToRaw(new BigDecimal("340282366.920938463463374607431768211455")));
    }

    @Test
    public void testAmountFromRaw() {
        assertEquals(0, BigDecimal.ONE.compareTo(
                UTIL.amountFromRaw(new BigInteger("1000000000000000000000000000000"))));
        assertEquals(0, new BigDecimal("8029.000000000000000000000000000001").compareTo(
                UTIL.amountFromRaw(new BigInteger("8029000000000000000000000000000001"))));
        assertEquals(0, new BigDecimal("0.000000000000000000000000000001").compareTo(
                UTIL.amountFromRaw(BigInteger.ONE)));
        assertEquals(0, new BigDecimal("340282366.920938463463374607431768211455").compareTo(
                UTIL.amountFromRaw(new BigInteger("340282366920938463463374607431768211455"))));
    }

    @Test
    public void testInvalidValues() {
        try {
            UTIL.validateAmount(new BigDecimal("-1"));
            fail();
        } catch (IllegalArgumentException e) {}
        try {
            UTIL.validateAmount(new BigDecimal("-1"));
            fail();
        } catch (IllegalArgumentException e) {}
        try {
            UTIL.validateAmount(new BigDecimal("340282366.920938463463374607431768211456"));
            fail();
        } catch (IllegalArgumentException e) {}
        try {
            UTIL.validateAmount(new BigDecimal("1.2340000000000000000000000000009"));
            fail();
        } catch (IllegalArgumentException e) {}
    }

}