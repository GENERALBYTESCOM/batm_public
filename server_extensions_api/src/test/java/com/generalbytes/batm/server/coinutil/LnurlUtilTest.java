package com.generalbytes.batm.server.coinutil;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LnurlUtilTest {

    LnurlUtil lnurlUtil = new LnurlUtil();

    @Test
    void decode() throws AddressFormatException {
        String lnurlPay = "lightning:LNURL1DP68GURN8GHJ7MRWW4EXCTNXD9SHG6NPVCHXXMMD9AKXUATJDSKHQCTE8AEK2UMND9HKU0TPX5EXYVTYXEJX2E3KV43N2DFCXE3KXWTRX3JXXWFNVVMNJC3KXVCNJDRY8QUKZVFNXS6NYD3SVCMXYDF3VGMRJDM9XD3X2VEKVYMN2Q0JTL3";
        String expected = "https://lnurl.fiatjaf.com/lnurl-pay?session=a52b1d6def6ec5586cc9c4dc93c79b63194d89a1345260f6b51b697e3be36a75";
        assertEquals(expected, lnurlUtil.decode(lnurlPay));
    }

    @Test
    void decodeWithoutScheme() throws AddressFormatException {
        String lnurlPay = "LNURL1DP68GURN8GHJ7MRWW4EXCTNXD9SHG6NPVCHXXMMD9AKXUATJDSKHQCTE8AEK2UMND9HKU0TPX5EXYVTYXEJX2E3KV43N2DFCXE3KXWTRX3JXXWFNVVMNJC3KXVCNJDRY8QUKZVFNXS6NYD3SVCMXYDF3VGMRJDM9XD3X2VEKVYMN2Q0JTL3";
        String expected = "https://lnurl.fiatjaf.com/lnurl-pay?session=a52b1d6def6ec5586cc9c4dc93c79b63194d89a1345260f6b51b697e3be36a75";
        assertEquals(expected, lnurlUtil.decode(lnurlPay));
    }

    @Test
    void schemeOnly() {
        AddressFormatException e = assertThrows(AddressFormatException.class, () -> lnurlUtil.decode("lightning:"));
        assertEquals("Missing human-readable part", e.getMessage());
    }

    @Test
    void separatorOnly() {
        AddressFormatException e = assertThrows(AddressFormatException.class, () -> lnurlUtil.decode(":"));
        assertEquals("Missing human-readable part", e.getMessage());
    }

    @Test
    void empty() {
        AddressFormatException e = assertThrows(AddressFormatException.class, () -> lnurlUtil.decode(""));
        assertEquals("Missing human-readable part", e.getMessage());
    }

    @Test
    void nullLnurl() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> lnurlUtil.decode(null));
        assertEquals("lnurl cannot be null", e.getMessage());
    }
}