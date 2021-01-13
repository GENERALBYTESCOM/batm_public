/*************************************************************************************
 * Copyright (C) 2015 GENERAL BYTES s.r.o. All rights reserved.
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IIdentity {

    int STATE_NOT_REGISTERED = 0;
    int STATE_REGISTERED = 1;
    int STATE_TO_BE_REGISTERED = 2;
    int STATE_PROHIBITED = 3;
    int STATE_ANONYMOUS = 4;
    int STATE_PROHIBITED_TO_BE_REGISTERED   = 5;

    int TYPE_INTERNAL   = 0; //must not be instantiated by extension
    int TYPE_EXTERNAL   = 1; //this identity was created externally

    boolean isNew();
    String getPublicId();
    String getExternalId();
    int getState();
    int getType();
    Date getCreated();
    Date getRegistered();
    String getCreatedByTerminalSerialNumber();
    IPerson getRegisteredBy();
    BigDecimal getVipBuyDiscount();
    BigDecimal getVipSellDiscount();
    Date getLastUpdatedAt();
    Date getWatchListLastScanAt();
    boolean isWatchListBanned();
    String getNote();
    List<IIdentityNote> getNotes();
    List<IIdentityPiece> getIdentityPieces();

    //Individual limits set on identity
    List<ILimit> getLimitCashPerTransaction();
    List<ILimit> getLimitCashPerHour();
    List<ILimit> getLimitCashPerDay();
    List<ILimit> getLimitCashPerWeek();
    List<ILimit> getLimitCashPerMonth();
    List<ILimit> getLimitCashPer3Months();
    List<ILimit> getLimitCashPer12Months();
    List<ILimit> getLimitCashPerCalendarQuarter();
    List<ILimit> getLimitCashPerCalendarYear();
    List<ILimit> getLimitCashTotalIdentity();

    String getConfigurationCashCurrency();
    IOrganization getOrganization();
}
