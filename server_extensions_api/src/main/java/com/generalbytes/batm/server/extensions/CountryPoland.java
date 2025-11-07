package com.generalbytes.batm.server.extensions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Poland province (voivodeship) identifiers.
 * <p>
 * Usage e.g.:
 * CountryPoland.PL02.getProvinceName()
 * CountryPoland.valueOf("PL02").getProvinceName()
 */
@Getter
@AllArgsConstructor
public enum CountryPoland {

    PL02("PL02", "Lower Silesian"),
    PL04("PL04", "Kuyavian-Pomeranian"),
    PL06("PL06", "Lublin"),
    PL08("PL08", "Lubusz"),
    PL10("PL10", "Łódź"),
    PL12("PL12", "Lesser Poland"),
    PL14("PL14", "Masovian"),
    PL16("PL16", "Opole"),
    PL18("PL18", "Subcarpathian"),
    PL20("PL20", "Podlaskie"),
    PL22("PL22", "Pomeranian"),
    PL24("PL24", "Silesian"),
    PL26("PL26", "Świętokrzyskie"),
    PL28("PL28", "Warmian-Masurian"),
    PL30("PL30", "Greater Poland"),
    PL32("PL32", "West Pomeranian");

    private final String iso;
    private final String provinceName;
}
