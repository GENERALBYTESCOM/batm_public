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
 * Canada province identifiers.
 *
 * Usage e.g.:
 *      CountryCanada.QC.getProvinceName()
 *      CountryCanada.valueOf("QC").getProvinceName()
 */
@Getter
@AllArgsConstructor
public enum CountryCanada implements CountryRegion {

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

}
