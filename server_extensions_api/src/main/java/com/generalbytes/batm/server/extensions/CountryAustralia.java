package com.generalbytes.batm.server.extensions;

/**
 * Australia province identifiers.
 * <p>
 * Usage e.g.:
 * CountryAustralia.NSW.getProvinceName()
 * CountryAustralia.valueOf("NSW").getProvinceName()
 */
public enum CountryAustralia {

    NSW("NSW", "New South Wales"),
    VIC("VIC", "Victoria"),
    QLD("QLD", "Queensland"),
    WA("WA", "Western Australia"),
    SA("SA", "South Australia"),
    TAS("TAS", "Tasmania");

    private final String iso;

    private final String provinceName;

    /**
     * Private constructor.
     */
    CountryAustralia(String iso, String provinceName) {
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
