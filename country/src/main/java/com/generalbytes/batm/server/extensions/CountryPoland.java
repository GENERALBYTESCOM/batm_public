package com.generalbytes.batm.server.extensions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Poland province (voivodeship) identifiers.
 * <p>
 * Usage e.g.:
 * CountryPoland.MZ.getProvinceName()
 * CountryPoland.valueOf("MZ").getProvinceName()
 */
@Getter
@AllArgsConstructor
public enum CountryPoland implements CountryRegion {

    WP("WP", "Greater Poland"),
    KP("KP", "Kuyavia-Pomerania"),
    MA("MA", "Lesser Poland"),
    LD("LD", "Łódź"),
    DS("DS", "Lower Silesia"),
    LU("LU", "Lublin"),
    LB("LB", "Lubusz"),
    MZ("MZ", "Masovia"),
    OP("OP", "Opole"),
    PD("PD", "Podlaskie"),
    PM("PM", "Pomerania"),
    SL("SL", "Silesia"),
    PK("PK", "Subcarpathia"),
    SK("SK", "Świętokrzyskie"),
    WM("WM", "Warmia-Masuria"),
    ZP("ZP", "West Pomerania");

    private final String iso;
    private final String provinceName;
}
