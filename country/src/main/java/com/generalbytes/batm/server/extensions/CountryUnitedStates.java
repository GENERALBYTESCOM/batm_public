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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * United States province identifiers.
 *
 * Usage e.g.:
 *      CountryUnitedStates.WA.getProvinceName()
 *      CountryUnitedStates.valueOf("WA").getProvinceName()
 */
@Getter
@AllArgsConstructor
public enum CountryUnitedStates implements CountryRegion {

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

}
