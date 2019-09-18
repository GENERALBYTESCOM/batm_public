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
package com.generalbytes.batm.server.extensions.extra.examples.identity;

import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.IPerson;

import java.util.Date;

class IdentityPieceExample implements IIdentityPiece {

    private final int type;
    private final String phoneNumber;
    private final String emailAddress;
    private final String firstName;
    private final String lastName;
    private final String idCardNumber;
    private final String contactZIP;
    private final String contactCountry;
    private final String contactProvince;
    private final String contactCity;
    private final String contactAddress;

    IdentityPieceExample(int type, String phoneNumber, String emailAddress, String firstName, String lastName, String idCardNumber, String contactZIP, String contactCountry, String contactProvince, String contactCity, String contactAddress) {
        this.type = type;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idCardNumber = idCardNumber;
        this.contactZIP = contactZIP;
        this.contactCountry = contactCountry;
        this.contactProvince = contactProvince;
        this.contactCity = contactCity;
        this.contactAddress = contactAddress;
    }

    public static IdentityPieceExample fromEmailAddress(String emailAddress) {
        return new IdentityPieceExample(TYPE_EMAIL, null, emailAddress, null, null, null, null, null, null,null, null);
    }

    public static IdentityPieceExample fromPhoneNumber(String phoneNumber) {
        return new IdentityPieceExample(TYPE_CELLPHONE, phoneNumber, null, null, null, null, null, null, null,null, null);
    }

    public static IdentityPieceExample fromPersonalInfo(String firstName, String lastName, String idCardNumber, String contactZIP, String contactCountry, String contactProvince, String contactCity, String contactAddress) {
        return new IdentityPieceExample(TYPE_PERSONAL_INFORMATION, null, null, firstName, lastName, idCardNumber, contactZIP, contactCountry, contactProvince, contactCity, contactAddress);
    }

    @Override
    public int getPieceType() {
        return type;
    }

    @Override
    public Date getCreated() {
        return null;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String getMimeType() {
        return null;
    }

    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public byte[] getData() {
        return new byte[0];
    }

    @Override
    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public String getFirstname() {
        return firstName;
    }

    @Override
    public String getLastname() {
        return lastName;
    }

    @Override
    public String getContactAddress() {
        return contactAddress;
    }

    @Override
    public String getContactCity() {
        return contactCity;
    }

    @Override
    public String getContactCountry() {
        return contactCountry;
    }
    @Override
    public String getContactProvince() {
        return contactProvince;
    }

    @Override
    public String getContactZIP() {
        return contactZIP;
    }

    @Override
    public String getIdCardNumber() {
        return idCardNumber;
    }

    @Override
    public IPerson getCreatedBy() {
        return null;
    }
}
