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

import java.util.Date;

public interface IIdentityPiece {
    int TYPE_FINGERPRINT = 0;
    int TYPE_EMAIL = 1;
    int TYPE_ID_SCAN = 2;
    int TYPE_PERSONAL_INFORMATION = 3;
    int TYPE_CELLPHONE = 4;
    int TYPE_SELFIE = 5;
    int TYPE_CAMERA_IMAGE = 6;

    int getPieceType();
    Date getCreated();
    String getPhoneNumber();
    String getMimeType();
    String getFilename();
    byte[] getData();
    String getEmailAddress();
    String getFirstname();
    String getLastname();
    String getContactAddress();
    String getContactCity();
    String getContactCountry();

    /**
     *
     * @return e.g. a state in the US
     */
    String getContactProvince();
    String getContactZIP();
    String getIdCardNumber();
    IPerson getCreatedBy();
}
