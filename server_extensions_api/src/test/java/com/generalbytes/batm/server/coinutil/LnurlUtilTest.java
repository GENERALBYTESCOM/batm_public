package com.generalbytes.batm.server.coinutil;

import org.junit.Assert;
import org.junit.Test;

public class LnurlUtilTest {

    LnurlUtil lnurlUtil = new LnurlUtil();

    @Test
    public void decode() throws AddressFormatException {
        String lnurlPay = "lightning:LNURL1DP68GURN8GHJ7MRWW4EXCTNXD9SHG6NPVCHXXMMD9AKXUATJDSKHQCTE8AEK2UMND9HKU0TPX5EXYVTYXEJX2E3KV43N2DFCXE3KXWTRX3JXXWFNVVMNJC3KXVCNJDRY8QUKZVFNXS6NYD3SVCMXYDF3VGMRJDM9XD3X2VEKVYMN2Q0JTL3";
        String expected = "https://lnurl.fiatjaf.com/lnurl-pay?session=a52b1d6def6ec5586cc9c4dc93c79b63194d89a1345260f6b51b697e3be36a75";
        Assert.assertEquals(expected, lnurlUtil.decode(lnurlPay));
    }

    @Test
    public void decodeWithoutScheme() throws AddressFormatException {
        String lnurlPay = "LNURL1DP68GURN8GHJ7MRWW4EXCTNXD9SHG6NPVCHXXMMD9AKXUATJDSKHQCTE8AEK2UMND9HKU0TPX5EXYVTYXEJX2E3KV43N2DFCXE3KXWTRX3JXXWFNVVMNJC3KXVCNJDRY8QUKZVFNXS6NYD3SVCMXYDF3VGMRJDM9XD3X2VEKVYMN2Q0JTL3";
        String expected = "https://lnurl.fiatjaf.com/lnurl-pay?session=a52b1d6def6ec5586cc9c4dc93c79b63194d89a1345260f6b51b697e3be36a75";
        Assert.assertEquals(expected, lnurlUtil.decode(lnurlPay));
    }

    @Test
    public void schemeOnly() {
        AddressFormatException e = Assert.assertThrows(AddressFormatException.class, () -> lnurlUtil.decode("lightning:"));
        Assert.assertEquals("Missing human-readable part", e.getMessage());
    }

    @Test
    public void separatorOnly() {
        AddressFormatException e = Assert.assertThrows(AddressFormatException.class, () -> lnurlUtil.decode(":"));
        Assert.assertEquals("Missing human-readable part", e.getMessage());
    }

    @Test
    public void empty() {
        AddressFormatException e = Assert.assertThrows(AddressFormatException.class, () -> lnurlUtil.decode(""));
        Assert.assertEquals("Missing human-readable part", e.getMessage());
    }

    @Test
    public void nullLnurl() {
        NullPointerException e = Assert.assertThrows(NullPointerException.class, () -> lnurlUtil.decode(null));
        Assert.assertEquals("lnurl cannot be null", e.getMessage());
    }
}