
package com.generalbytes.batm.server.extensions;

/**
 * Australia district identifiers.
 * <p>
 * Usage e.g.:
 * CountryHongKong.HCW.getProvinceName()
 * CountryHongKong.valueOf("HCW").getProvinceName()
 */
public enum CountryHongKong {
    HCW("HCW", "Central and Western Hong Kong Island"),
    HEA("HEA", "Eastern Hong Kong Island"),
    HSO("HSO", "Southern Hong Kong Island"),
    HWC("HWC", "Wan Chai Hong Kong Island"),
    KKC("KKC", "Kowloon City Kowloon"),
    KKT("KKT", "Kwun Tong Kowloon"),
    KSS("KSS", "Sham Shui Po Kowloon"),
    KWT("KWT", "Wong Tai Sin Kowloon"),
    KYT("KYT", "Yau Tsim Mong Kowloon"),
    NIS("NIS", "Islands New Territories"),
    NKT("NKT", "Kwai Tsing New Territories"),
    NNO("NNO", "North New Territories"),
    NSK("NSK", "Sai Kung New Territories"),
    NST("NST", "Sha Tin New Territories"),
    NTM("NTM", "Tuen Mun New Territories"),
    NTP("NTP", "Tai Po New Territories"),
    NTW("NTW", "Tsuen Wan New Territories"),
    NYL("NYL", "Yuen Long New Territories");

    private final String iso;

    private final String provinceName;

    /**
     * Private constructor.
     */
    CountryHongKong(String iso, String provinceName) {
        this.iso = iso;
        this.provinceName = provinceName;
    }

    /**
     * ISO 3166-2 code of the province (2 digits).
     */
    public String getIso() {
        return iso;
    }

    /**
     * English province/territory name officially used by the ISO 3166 Maintenance Agency (ISO 3166/MA).
     */
    public String getProvinceName() {
        return provinceName;
    }
}