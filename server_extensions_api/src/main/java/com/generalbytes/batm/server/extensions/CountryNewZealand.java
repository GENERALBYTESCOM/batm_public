package com.generalbytes.batm.server.extensions;

/**
 * New Zealand regions identifiers.
 * <p>
 * Usage e.g.:
 * CountryNewZealand.NTL.getProvinceName()
 * CountryNewZealand.valueOf("NTL").getProvinceName()
 */
public enum CountryNewZealand {

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

    /**
     * Private constructor.
     */
    CountryNewZealand(String iso, String provinceName) {
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
     * English province/region name officially used by the ISO 3166 Maintenance Agency (ISO 3166/MA).
     */
    public String getProvinceName() {
        return provinceName;
    }
}
