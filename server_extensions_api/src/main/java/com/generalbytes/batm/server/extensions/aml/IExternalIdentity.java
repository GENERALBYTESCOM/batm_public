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
package com.generalbytes.batm.server.extensions.aml;

public interface IExternalIdentity {

    int STATE_NOT_REGISTERED                = 0;
    int STATE_REGISTERED                    = 1;
    int STATE_TO_BE_REGISTERED              = 2;
    int STATE_PROHIBITED                    = 3;
    int STATE_ANONYMOUS                     = 4;
    int STATE_PROHIBITED_TO_BE_REGISTERED   = 5;
    int STATE_TO_BE_VERIFIED                = 6;
    int STATE_RESTRICTED                    = 7;

    String getId();
    int getState();

    //Optional attributes - may return null
    String getPhoneNumber();
    String getEmail();
    String getFirstname();
    String getLastname();
    String getLanguage();
}
