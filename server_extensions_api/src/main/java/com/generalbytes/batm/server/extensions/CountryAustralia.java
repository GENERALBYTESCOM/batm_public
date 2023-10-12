package com.generalbytes.batm.server.extensions;

/**
 * Australia province identifiers.
 * <p>
 * Usage e.g.:
 * CountryAustralia.NSW.getProvinceName()
 * CountryAustralia.valueOf("NSW").getProvinceName()
 */
public enum CountryAustralia {

    AU_NSW("AU-NSW", "New South Wales"),
    AU_QLD("AU-QLD", "Queensland"),
    AU_SA("AU-SA", "South Australia"),
    AU_TAS("AU-TAS", "Tasmania"),
    AU_VIC("AU-VIC", "Victoria"),
    AU_WA("AU-WA", "Western Australia"),
    AU_ACT("AU-ACT", "Australian Capital Territory"),
    AU_NT("AU-NT", "Northern Territory");

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
