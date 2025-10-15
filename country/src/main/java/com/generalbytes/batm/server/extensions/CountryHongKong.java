
package com.generalbytes.batm.server.extensions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Australia district identifiers.
 * <p>
 * Usage e.g.:
 * CountryHongKong.HCW.getProvinceName()
 * CountryHongKong.valueOf("HCW").getProvinceName()
 */
@Getter
@AllArgsConstructor
public enum CountryHongKong implements CountryRegion {
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

}