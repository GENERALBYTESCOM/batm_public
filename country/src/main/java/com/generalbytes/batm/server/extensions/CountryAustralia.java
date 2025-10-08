package com.generalbytes.batm.server.extensions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Australia province identifiers.
 * <p>
 * Usage e.g.:
 * CountryAustralia.NSW.getProvinceName()
 * CountryAustralia.valueOf("NSW").getProvinceName()
 */
@Getter
@AllArgsConstructor
public enum CountryAustralia implements CountryRegion {

    NSW("NSW", "New South Wales"),
    QLD("QLD", "Queensland"),
    SA("SA", "South Australia"),
    TAS("TAS", "Tasmania"),
    VIC("VIC", "Victoria"),
    WA("WA", "Western Australia"),
    ACT("ACT", "Australian Capital Territory"),
    NT("NT", "Northern Territory");

    private final String iso;
    private final String provinceName;

}
