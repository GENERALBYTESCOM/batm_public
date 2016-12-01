package com.generalbytes.batm.server.extensions;

/**
 * United States province identifiers.
 *
 * Usage e.g.:
 *      CountryUnitedStates.WA.getProvinceName()
 *      CountryUnitedStates.valueOf("WA").getProvinceName()
 */
public enum CountryUnitedStates {

    AL("AL","Alabama"),
    AK("AK","Alaska"),
    AZ("AZ","Arizona"),
    AR("AR","Arkansas"),
    CA("CA","California"),
    CO("CO","Colorado"),
    CT("CT","Connecticut"),
    DE("DE","Delaware"),
    FL("FL","Florida"),
    GA("GA","Georgia"),
    HI("HI","Hawaii"),
    ID("ID","Idaho"),
    IL("IL","Illinois"),
    IN("IN","Indiana"),
    IA("IA","Iowa"),
    KS("KS","Kansas"),
    KY("KY","Kentucky"),
    LA("LA","Louisiana"),
    ME("ME","Maine"),
    MD("MD","Maryland"),
    MA("MA","Massachusetts"),
    MI("MI","Michigan"),
    MN("MN","Minnesota"),
    MS("MS","Mississippi"),
    MO("MO","Missouri"),
    MT("MT","Montana"),
    NE("NE","Nebraska"),
    NV("NV","Nevada"),
    NH("NH","New Hampshire"),
    NJ("NJ","New Jersey"),
    NM("NM","New Mexico"),
    NY("NY","New York"),
    NC("NC","North Carolina"),
    ND("ND","North Dakota"),
    OH("OH","Ohio"),
    OK("OK","Oklahoma"),
    OR("OR","Oregon"),
    PA("PA","Pennsylvania"),
    RI("RI","Rhode Island"),
    SC("SC","South Carolina"),
    SD("SD","South Dakota"),
    TN("TN","Tennessee"),
    TX("TX","Texas"),
    UT("UT","Utah"),
    VT("VT","Vermont"),
    VA("VA","Virginia"),
    WA("WA","Washington"),
    WV("WV","West Virginia"),
    WI("WI","Wisconsin"),
    WY("WY","Wyoming"),
    DC("DC","District of Columbia"),
    AS("AS","American Samoa"),
    GU("GU","Guam"),
    MP("MP","Northern Mariana Islands"),
    PR("PR","Puerto Rico"),
    UM("UM","United States Minor Outlying Islands"),
    VI("VI","Virgin Islands, U.S.");

    private final String iso;

    private final String provinceName;

    /**
     * Private constructor.
     */
    CountryUnitedStates(String iso, String provinceName) {
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
     * English state/district/area name officially used by the ISO 3166 Maintenance Agency (ISO 3166/MA).
     */
    public String getProvinceName() {
        return provinceName;
    }
}
