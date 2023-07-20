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

/**
 * Italy province identifiers.
 * Usage e.g.:
 * CountryItaly.QC.getProvinceName()
 * CountryItaly.valueOf("QC").getProvinceName()
 */
public enum CountryItaly {
    AG("AG", "Agrigento"),
    AL("AL", "Alessandria"),
    AN("AN", "Ancona"),
    AO("AO", "Aosta"),
    AR("AR", "Arezzo"),
    AP("AP", "Ascoli Piceno"),
    AT("AT", "Asti"),
    AV("AV", "Avellino"),
    BA("BA", "Bari"),
    BT("BT", "Barletta-Andria-Trani"),
    BL("BL", "Belluno"),
    BN("BN", "Benevento"),
    BG("BG", "Bergamo"),
    BI("BI", "Biella"),
    BO("BO", "Bologna"),
    BS("BS", "Brescia"),
    BR("BR", "Brindisi"),
    CA("CA", "Cagliari"),
    CL("CL", "Caltanissetta"),
    CB("CB", "Campobasso"),
    CE("CE", "Caserta"),
    CT("CT", "Catania"),
    CZ("CZ", "Catanzaro"),
    CH("CH", "Chieti"),
    CO("CO", "Como"),
    CS("CS", "Cosenza"),
    CR("CR", "Cremona"),
    KR("KR", "Crotone"),
    CN("CN", "Cuneo"),
    EN("EN", "Enna"),
    FM("FM", "Fermo"),
    FE("FE", "Ferrara"),
    FI("FI", "Florence"),
    FG("FG", "Foggia"),
    FC("FC", "Forlì-Cesena"),
    FR("FR", "Frosinone"),
    GE("GE", "Genoa"),
    GO("GO", "Gorizia"),
    GR("GR", "Grosseto"),
    IM("IM", "Imperia"),
    IS("IS", "Isernia"),
    AQ("AQ", "L'Aquila"),
    SP("SP", "La Spezia"),
    LT("LT", "Latina"),
    LE("LE", "Lecce"),
    LC("LC", "Lecco"),
    LI("LI", "Livorno"),
    LO("LO", "Lodi"),
    LU("LU", "Lucca"),
    MC("MC", "Macerata"),
    MN("MN", "Mantua"),
    MS("MS", "Massa and Carrara"),
    MT("MT", "Matera"),
    ME("ME", "Messina"),
    MI("MI", "Milan"),
    MO("MO", "Modena"),
    MB("MB", "Monza and Brianza"),
    NA("NA", "Naples"),
    NO("NO", "Novara"),
    NU("NU", "Nuoro"),
    OR("OR", "Oristano"),
    PD("PD", "Padua"),
    PA("PA", "Palermo"),
    PR("PR", "Parma"),
    PV("PV", "Pavia"),
    PG("PG", "Perugia"),
    PU("PU", "Pesaro and Urbino"),
    PE("PE", "Pescara"),
    PC("PC", "Piacenza"),
    PI("PI", "Pisa"),
    PT("PT", "Pistoia"),
    PN("PN", "Pordenone"),
    PZ("PZ", "Potenza"),
    PO("PO", "Prato"),
    RG("RG", "Ragusa"),
    RA("RA", "Ravenna"),
    RC("RC", "Reggio Calabria"),
    RE("RE", "Reggio Emilia"),
    RI("RI", "Rieti"),
    RN("RN", "Rimini"),
    RM("RM", "Rome"),
    RO("RO", "Rovigo"),
    SA("SA", "Salerno"),
    SS("SS", "Sassari"),
    SV("SV", "Savona"),
    SI("SI", "Siena"),
    SO("SO", "Sondrio"),
    CI("SU", "South Sardinia"),
    BZ("BZ", "South Tyrol"),
    SR("SR", "Syracuse"),
    TA("TA", "Taranto"),
    TE("TE", "Teramo"),
    TR("TR", "Terni"),
    TP("TP", "Trapani"),
    TN("TN", "Trento"),
    TV("TV", "Treviso"),
    TS("TS", "Trieste"),
    TO("TO", "Turin"),
    UD("UD", "Udine"),
    VA("VA", "Varese"),
    VE("VE", "Venice"),
    VB("VB", "Verbano-Cusio-Ossola"),
    VC("VC", "Vercelli"),
    VR("VR", "Verona"),
    VV("VV", "Vibo Valentia"),
    VI("VI", "Vicenza"),
    VT("VT", "Viterbo");

    private final String iso;
    private final String provinceName;

    /**
     * Private constructor.
     */
    CountryItaly(String iso, String provinceName) {
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
