/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Country identifiers.
 * <p>
 * Usage e.g.:
 * Country.US.getCountryName()
 * Country.valueOf("US").getCountryName()
 */
@Getter
public enum Country {

    AF("AF", "AFG", "Afghanistan"),
    AX("AX", "ALA", "\u00C5land Islands"),
    AL("AL", "ALB", "Albania"),
    DZ("DZ", "DZA", "Algeria"),
    AS("AS", "ASM", "American Samoa"),
    AD("AD", "AND", "Andorra"),
    AO("AO", "AGO", "Angola"),
    AI("AI", "AIA", "Anguilla"),
    AQ("AQ", "ATA", "Antarctica"),
    AG("AG", "ATG", "Antigua and Barbuda"),
    AR("AR", "ARG", "Argentina"),
    AM("AM", "ARM", "Armenia"),
    AW("AW", "ABW", "Aruba"),
    AU("AU", "AUS", "Australia", CountryAustralia.values()),
    AT("AT", "AUT", "Austria"),
    AZ("AZ", "AZE", "Azerbaijan"),
    BS("BS", "BHS", "Bahamas"),
    BH("BH", "BHR", "Bahrain"),
    BD("BD", "BGD", "Bangladesh"),
    BB("BB", "BRB", "Barbados"),
    BY("BY", "BLR", "Belarus"),
    BE("BE", "BEL", "Belgium"),
    BZ("BZ", "BLZ", "Belize"),
    BJ("BJ", "BEN", "Benin"),
    BM("BM", "BMU", "Bermuda"),
    BT("BT", "BTN", "Bhutan"),
    BO("BO", "BOL", "Bolivia (Plurinational State of)"),
    BQ("BQ", "BES", "Bonaire, Sint Eustatius and Saba"),
    BA("BA", "BIH", "Bosnia and Herzegovina"),
    BW("BW", "BWA", "Botswana"),
    BV("BV", "BVT", "Bouvet Island"),
    BR("BR", "BRA", "Brazil"),
    IO("IO", "IOT", "British Indian Ocean Territory"),
    BN("BN", "BRN", "Brunei Darussalam"),
    BG("BG", "BGR", "Bulgaria"),
    BF("BF", "BFA", "Burkina Faso"),
    BI("BI", "BDI", "Burundi"),
    CV("CV", "CPV", "Cabo Verde"),
    KH("KH", "KHM", "Cambodia"),
    CM("CM", "CMR", "Cameroon"),
    CA("CA", "CAN", "Canada", CountryCanada.values()),
    KY("KY", "CYM", "Cayman Islands"),
    CF("CF", "CAF", "Central African Republic"),
    TD("TD", "TCD", "Chad"),
    CL("CL", "CHL", "Chile"),
    CN("CN", "CHN", "China"),
    CX("CX", "CXR", "Christmas Island"),
    CC("CC", "CCK", "Cocos (Keeling) Islands"),
    CO("CO", "COL", "Colombia"),
    KM("KM", "COM", "Comoros"),
    CG("CG", "COG", "Congo"),
    CD("CD", "COD", "Congo (Democratic Republic of the)"),
    CK("CK", "COK", "Cook Islands"),
    CR("CR", "CRI", "Costa Rica"),
    CI("CI", "CIV", "C\u00F4te d'Ivoire"),
    HR("HR", "HRV", "Croatia"),
    CU("CU", "CUB", "Cuba"),
    CW("CW", "CUW", "Cura\u00E7ao"),
    CY("CY", "CYP", "Cyprus"),
    CZ("CZ", "CZE", "Czech Republic"),
    DK("DK", "DNK", "Denmark"),
    DJ("DJ", "DJI", "Djibouti"),
    DM("DM", "DMA", "Dominica"),
    DO("DO", "DOM", "Dominican Republic"),
    EC("EC", "ECU", "Ecuador"),
    EG("EG", "EGY", "Egypt"),
    SV("SV", "SLV", "El Salvador"),
    GQ("GQ", "GNQ", "Equatorial Guinea"),
    ER("ER", "ERI", "Eritrea"),
    EE("EE", "EST", "Estonia"),
    ET("ET", "ETH", "Ethiopia"),
    FK("FK", "FLK", "Falkland Islands (Malvinas)"),
    FO("FO", "FRO", "Faroe Islands"),
    FJ("FJ", "FJI", "Fiji"),
    FI("FI", "FIN", "Finland"),
    FR("FR", "FRA", "France"),
    GF("GF", "GUF", "French Guiana"),
    PF("PF", "PYF", "French Polynesia"),
    TF("TF", "ATF", "French Southern Territories"),
    GA("GA", "GAB", "Gabon"),
    GM("GM", "GMB", "Gambia"),
    GE("GE", "GEO", "Georgia"),
    DE("DE", "DEU", "Germany"),
    GH("GH", "GHA", "Ghana"),
    GI("GI", "GIB", "Gibraltar"),
    GR("GR", "GRC", "Greece"),
    GL("GL", "GRL", "Greenland"),
    GD("GD", "GRD", "Grenada"),
    GP("GP", "GLP", "Guadeloupe"),
    GU("GU", "GUM", "Guam"),
    GT("GT", "GTM", "Guatemala"),
    GG("GG", "GGY", "Guernsey"),
    GN("GN", "GIN", "Guinea"),
    GW("GW", "GNB", "Guinea-Bissau"),
    GY("GY", "GUY", "Guyana"),
    HT("HT", "HTI", "Haiti"),
    HM("HM", "HMD", "Heard Island and McDonald Islands"),
    VA("VA", "VAT", "Holy See"),
    HN("HN", "HND", "Honduras"),
    HK("HK", "HKG", "Hong Kong", CountryHongKong.values()),
    HU("HU", "HUN", "Hungary"),
    IS("IS", "ISL", "Iceland"),
    IN("IN", "IND", "India"),
    ID("ID", "IDN", "Indonesia"),
    IR("IR", "IRN", "Iran (Islamic Republic of)"),
    IQ("IQ", "IRQ", "Iraq"),
    IE("IE", "IRL", "Ireland"),
    IM("IM", "IMN", "Isle of Man"),
    IL("IL", "ISR", "Israel"),
    IT("IT", "ITA", "Italy", CountryItaly.values()),
    JM("JM", "JAM", "Jamaica"),
    JP("JP", "JPN", "Japan"),
    JE("JE", "JEY", "Jersey"),
    JO("JO", "JOR", "Jordan"),
    KZ("KZ", "KAZ", "Kazakhstan"),
    KE("KE", "KEN", "Kenya"),
    KI("KI", "KIR", "Kiribati"),
    KP("KP", "PRK", "Korea (Democratic People's Republic of)"),
    KR("KR", "KOR", "Korea (Republic of)"),
    XK("XK", "XKX", "Kosovo"), // "user assigned" ISO 3166 code not designated by the standard
    KW("KW", "KWT", "Kuwait"),
    KG("KG", "KGZ", "Kyrgyzstan"),
    LA("LA", "LAO", "Lao People's Democratic Republic"),
    LV("LV", "LVA", "Latvia"),
    LB("LB", "LBN", "Lebanon"),
    LS("LS", "LSO", "Lesotho"),
    LR("LR", "LBR", "Liberia"),
    LL("LL", "LLD", "Liberland"),
    LY("LY", "LBY", "Libya"),
    LI("LI", "LIE", "Liechtenstein"),
    LT("LT", "LTU", "Lithuania"),
    LU("LU", "LUX", "Luxembourg"),
    MO("MO", "MAC", "Macao"),
    MK("MK", "MKD", "North Macedonia"),
    MG("MG", "MDG", "Madagascar"),
    MW("MW", "MWI", "Malawi"),
    MY("MY", "MYS", "Malaysia"),
    MV("MV", "MDV", "Maldives"),
    ML("ML", "MLI", "Mali"),
    MT("MT", "MLT", "Malta"),
    MH("MH", "MHL", "Marshall Islands"),
    MQ("MQ", "MTQ", "Martinique"),
    MR("MR", "MRT", "Mauritania"),
    MU("MU", "MUS", "Mauritius"),
    YT("YT", "MYT", "Mayotte"),
    MX("MX", "MEX", "Mexico"),
    FM("FM", "FSM", "Micronesia (Federated States of)"),
    MD("MD", "MDA", "Moldova (Republic of)"),
    MC("MC", "MCO", "Monaco"),
    MN("MN", "MNG", "Mongolia"),
    ME("ME", "MNE", "Montenegro"),
    MS("MS", "MSR", "Montserrat"),
    MA("MA", "MAR", "Morocco"),
    MZ("MZ", "MOZ", "Mozambique"),
    MM("MM", "MMR", "Myanmar"),
    NA("NA", "NAM", "Namibia"),
    NR("NR", "NRU", "Nauru"),
    NP("NP", "NPL", "Nepal"),
    NL("NL", "NLD", "Netherlands"),
    NC("NC", "NCL", "New Caledonia"),
    NZ("NZ", "NZL", "New Zealand", CountryNewZealand.values()),
    NI("NI", "NIC", "Nicaragua"),
    NE("NE", "NER", "Niger"),
    NG("NG", "NGA", "Nigeria"),
    NU("NU", "NIU", "Niue"),
    NF("NF", "NFK", "Norfolk Island"),
    MP("MP", "MNP", "Northern Mariana Islands"),
    NO("NO", "NOR", "Norway"),
    OM("OM", "OMN", "Oman"),
    PK("PK", "PAK", "Pakistan"),
    PW("PW", "PLW", "Palau"),
    PS("PS", "PSE", "Palestine, State of"),
    PA("PA", "PAN", "Panama"),
    PG("PG", "PNG", "Papua New Guinea"),
    PY("PY", "PRY", "Paraguay"),
    PE("PE", "PER", "Peru"),
    PH("PH", "PHL", "Philippines"),
    PN("PN", "PCN", "Pitcairn"),
    PL("PL", "POL", "Poland", CountryPoland.values()),
    PT("PT", "PRT", "Portugal"),
    PR("PR", "PRI", "Puerto Rico"),
    QA("QA", "QAT", "Qatar"),
    RE("RE", "REU", "R\u00E9union"),
    RO("RO", "ROU", "Romania"),
    RU("RU", "RUS", "Russian Federation"),
    RW("RW", "RWA", "Rwanda"),
    BL("BL", "BLM", "Saint Barth\u00E9lemy"),
    SH("SH", "SHN", "Saint Helena, Ascension and Tristan da Cunha"),
    KN("KN", "KNA", "Saint Kitts and Nevis"),
    LC("LC", "LCA", "Saint Lucia"),
    MF("MF", "MAF", "Saint Martin (French part)"),
    PM("PM", "SPM", "Saint Pierre and Miquelon"),
    VC("VC", "VCT", "Saint Vincent and the Grenadines"),
    WS("WS", "WSM", "Samoa"),
    SM("SM", "SMR", "San Marino"),
    ST("ST", "STP", "Sao Tome and Principe"),
    SA("SA", "SAU", "Saudi Arabia"),
    SN("SN", "SEN", "Senegal"),
    RS("RS", "SRB", "Serbia"),
    SC("SC", "SYC", "Seychelles"),
    SL("SL", "SLE", "Sierra Leone"),
    SG("SG", "SGP", "Singapore"),
    SX("SX", "SXM", "Sint Maarten (Dutch part)"),
    SK("SK", "SVK", "Slovakia"),
    SI("SI", "SVN", "Slovenia"),
    SB("SB", "SLB", "Solomon Islands"),
    SO("SO", "SOM", "Somalia"),
    ZA("ZA", "ZAF", "South Africa"),
    GS("GS", "SGS", "South Georgia and the South Sandwich Islands"),
    SS("SS", "SSD", "South Sudan"),
    ES("ES", "ESP", "Spain"),
    LK("LK", "LKA", "Sri Lanka"),
    SD("SD", "SDN", "Sudan"),
    SR("SR", "SUR", "Suriname"),
    SJ("SJ", "SJM", "Svalbard and Jan Mayen"),
    SZ("SZ", "SWZ", "Swaziland"),
    SE("SE", "SWE", "Sweden"),
    CH("CH", "CHE", "Switzerland"),
    SY("SY", "SYR", "Syrian Arab Republic"),
    TW("TW", "TWN", "Taiwan, Province of China"),
    TJ("TJ", "TJK", "Tajikistan"),
    TZ("TZ", "TZA", "Tanzania, United Republic of"),
    TH("TH", "THA", "Thailand"),
    TL("TL", "TLS", "Timor-Leste"),
    TG("TG", "TGO", "Togo"),
    TK("TK", "TKL", "Tokelau"),
    TO("TO", "TON", "Tonga"),
    TT("TT", "TTO", "Trinidad and Tobago"),
    TN("TN", "TUN", "Tunisia"),
    TR("TR", "TUR", "Turkey"),
    TM("TM", "TKM", "Turkmenistan"),
    TC("TC", "TCA", "Turks and Caicos Islands"),
    TV("TV", "TUV", "Tuvalu"),
    UG("UG", "UGA", "Uganda"),
    UA("UA", "UKR", "Ukraine"),
    AE("AE", "ARE", "United Arab Emirates"),
    GB("GB", "GBR", "United Kingdom of Great Britain and Northern Ireland"),
    US("US", "USA", "United States of America", CountryUnitedStates.values()),
    UM("UM", "UMI", "United States Minor Outlying Islands"),
    UY("UY", "URY", "Uruguay"),
    UZ("UZ", "UZB", "Uzbekistan"),
    VU("VU", "VUT", "Vanuatu"),
    VE("VE", "VEN", "Venezuela (Bolivarian Republic of)"),
    VN("VN", "VNM", "Viet Nam"),
    VG("VG", "VGB", "Virgin Islands (British)"),
    VI("VI", "VIR", "Virgin Islands (U.S.)"),
    WF("WF", "WLF", "Wallis and Futuna"),
    EH("EH", "ESH", "Western Sahara"),
    YE("YE", "YEM", "Yemen"),
    ZM("ZM", "ZMB", "Zambia"),
    ZW("ZW", "ZWE", "Zimbabwe");

    /**
     * ISO 3166-1 alpha-2 code of the country (2 letters).
     */
    private final String iso2;

    /**
     * ISO 3166-1 alpha-3 code of the country (3 letters).
     */
    private final String iso3;

    /**
     * English short country name officially used by the ISO 3166 Maintenance Agency (ISO 3166/MA).
     */
    private final String countryName;

    /**
     * Retrieves the list of regions associated with the country.
     * <p>
     * Returns an array of {@code CountryRegion} instances representing the regions of the country
     * or {@code null} if country doesn't support regions
     */
    private final CountryRegion[] regions;

    Country(String iso2, String iso3, String countryName, CountryRegion[] regions) {
        this.iso2 = iso2;
        this.iso3 = iso3;
        this.countryName = countryName;
        this.regions = regions;
    }

    Country(String iso2, String iso3, String countryName) {
        this.iso2 = iso2;
        this.iso3 = iso3;
        this.countryName = countryName;
        this.regions = null;
    }

    private static final Map<String, Country> values;
    private static final Map<String, Country> iso2toCountry;
    private static final Map<String, Country> iso3toCountry;

    static {
        values = new HashMap<>();
        iso2toCountry = new HashMap<>();
        iso3toCountry = new HashMap<>();
        for (Country country : Country.values()) {
            values.put(country.name(), country);
            iso2toCountry.put(country.getIso2(), country);
            iso3toCountry.put(country.getIso3(), country);
        }
    }

    /**
     * Custom implementation of Country#valueOf, method doesn't throw IllegalArgumentException for unknown name.
     *
     * @param name Country#name
     * @return Country or null if name is unknown.
     */
    public static Country value(String name) {
        return values.get(name);
    }

    /**
     * Retrieves a {@link Country} instance corresponding to the given ISO-2 country code.
     *
     * @param iso2 the two-letter ISO country code used to look up the corresponding country
     * @return the {@link Country} instance associated with the given ISO-2 code, or null if no matching country is found
     */
    public static Country getByIso2(String iso2) {
        return iso2toCountry.get(iso2);
    }

    /**
     * Retrieves a {@code Country} instance corresponding to the given ISO-3 country code.
     *
     * @param iso3 the three-letter ISO country code used to look up the corresponding country
     * @return the {@code Country} instance associated with the given ISO-3 code, or null if no matching country is found
     */
    public static Country getByIso3(String iso3) {
        return iso3toCountry.get(iso3);
    }

    /**
     * Determines whether the country has associated regions.
     *
     * @return true if the country has regions (non-null and non-empty), false otherwise
     */
    public boolean hasRegions() {
        return this.regions != null && this.regions.length > 0;
    }

}
