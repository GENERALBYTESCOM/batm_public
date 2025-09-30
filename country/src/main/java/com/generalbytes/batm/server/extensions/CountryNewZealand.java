package com.generalbytes.batm.server.extensions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * New Zealand regions identifiers.
 * <p>
 * Usage e.g.:
 * CountryNewZealand.NTL.getProvinceName()
 * CountryNewZealand.valueOf("NTL").getProvinceName()
 */
@Getter
@AllArgsConstructor
public enum CountryNewZealand implements CountryRegion {

    NTL("NTL", "Northland"),
    AUK("AUK", "Auckland"),
    WKO("WKO", "Waikato"),
    BOP("BOP", "Bay of Plenty"),
    GIS("GIS", "Gisborne"),
    HKB("HKB", "Hawke's Bay"),
    TKI("TKI", "Taranaki"),
    MWT("MWT", "Manawatū-Whanganui"),
    WGN("WGN", "Wellington"),
    TAS("TAS", "Tasman"),
    NSN("NSN", "Nelson"),
    MBH("MBH", "Marlborough"),
    WTC("WTC", "West Coast"),
    CAN("CAN", "Canterbury"),
    OTA("OTA", "Otago"),
    STL("STL", "Southland");

    private final String iso;
    private final String provinceName;

}
