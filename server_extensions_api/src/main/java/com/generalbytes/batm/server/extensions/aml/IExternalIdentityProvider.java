/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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

public interface IExternalIdentityProvider {

    /**
     * Called by server to retrieve existing identity information from external system.
     * @param identityExternalId - ide defined by external system.
     * @return
     */
    IExternalIdentity findIdentityByExternalId(String identityExternalId);

    /**
     * Called by server to find identity by cell phone number.
     * @param cellPhoneNumber - cell phone number always contains +country code and spaces between digits.
     * @return
     */
    IExternalIdentity findIdentityByPhoneNumber(String cellPhoneNumber);

    /**
     * Called by server to find identity by email address
     * @param emailAddress
     * @return
     */
    IExternalIdentity findIdentityByEmail(String emailAddress);
}
