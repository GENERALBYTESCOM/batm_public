package com.generalbytes.batm.server.extensions;

/**
 * Canada province identifiers.
 *
 * Usage e.g.:
 *      CountryCanada.QC.getProvinceName()
 *      CountryCanada.valueOf("QC").getProvinceName()
 */
public enum CountryCanada {

    AB("AB","Alberta"),
    BC("BC","British Columbia"),
    MB("MB","Manitoba"),
    NB("NB","New Brunswick"),
    NL("NL","Newfoundland and Labrador"),
    NS("NS","Nova Scotia"),
    ON("ON","Ontario"),
    PE("PE","Prince Edward Island"),
    QC("QC","Quebec"),
    SK("SK","Saskatchewan"),
    NT("NT","Northwest Territories"),
    NU("NU","Nunavut"),
    YT("YT","Yukon");

    private final String iso;

    private final String provinceName;

    /**
     * Private constructor.
     */
    CountryCanada(String iso, String provinceName) {
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
