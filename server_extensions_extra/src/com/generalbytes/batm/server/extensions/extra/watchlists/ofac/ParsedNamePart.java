/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.watchlists.ofac;

public class ParsedNamePart {
    private String partyId;
    private String groupID;
    private String nameType;
    private String aliasType;
    private String value;

    public ParsedNamePart(String partyId, String groupID, String nameType, String aliasType, String value) {
        this.partyId = partyId;
        this.groupID = groupID;
        this.nameType = nameType;
        this.aliasType = aliasType;
        this.value = value;
    }

    public String getPartyId() {
        return partyId;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getNameType() {
        return nameType;
    }

    public String getAliasType() {
        return aliasType;
    }

    public String getValue() {
        return value;
    }
}
