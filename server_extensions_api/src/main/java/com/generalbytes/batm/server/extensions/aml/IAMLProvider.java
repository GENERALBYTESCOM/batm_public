/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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

import com.generalbytes.batm.server.extensions.Contact;

/**
 *  Anti-Money-Laundering Provider.
 */
public interface IAMLProvider {

    /**
     * @return List of country codes supported by provider. Codes are in ISO 3166-1 alpha-2 format (2 letters).
     */
    String[] getSupportedCountries();

    /**
     * @param phoneNumberInternational Phone number in international format. (It begins with the country dialing code, for example "+1" for North America.)
     * @return Contact information about phone number holder.
     */
    Contact getContactByPhoneNumber(String phoneNumberInternational);
}
