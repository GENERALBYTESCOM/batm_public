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

/**
 * Detailed information about some {@link IIdentity}.
 * Values are set according to the piece type. This means that for some types only some values are set but the others remain unset.
 */
public interface IIdentityPiece {
    int TYPE_FINGERPRINT = 0;
    int TYPE_EMAIL = 1;
    int TYPE_ID_SCAN = 2;
    int TYPE_PERSONAL_INFORMATION = 3;
    int TYPE_CELLPHONE = 4;
    int TYPE_SELFIE = 5;
    int TYPE_CAMERA_IMAGE = 6;

    int DOCUMENT_TYPE_ID_CARD = 1;
    int DOCUMENT_TYPE_PASSPORT = 2;
    int DOCUMENT_TYPE_DRIVING_LICENCE = 3;

    /**
     * Defines what type of data will be processes and what will values will be set. Possible values are:
     * <ul>
     *     <li>TYPE_FINGERPRINT = 0</li>
     *     <li>TYPE_EMAIL = 1</li>
     *     <li>TYPE_ID_SCAN = 2</li>
     *     <li>TYPE_PERSONAL_INFORMATION = 3</li>
     *     <li>TYPE_CELLPHONE = 4</li>
     *     <li>TYPE_SELFIE = 5</li>
     *     <li>TYPE_CAMERA_IMAGE = 6</li>
     * </ul>
     *
     * @return int value representing current identity piece type.
     */
    int getPieceType();

    /**
     * Date when identity piece was created.
     *
     * @return {@link Date}
     */
    Date getCreated();

    /**
     * Identity's phone number, used with TYPE_CELLPHONE
     *
     * @return phone number or null
     */
    String getPhoneNumber();

    /**
     * Uploaded file mime type, used with TYPE_ID_SCAN, TYPE_SELFIE and TYPE_CAMERA_IMAGE
     *
     * @return mime type or null
     */
    String getMimeType();

    /**
     * File name, used with TYPE_ID_SCAN, TYPE_SELFIE, TYPE_CAMERA_IMAGE and TYPE_FINGERPRINT
     *
     * @return filename or null
     */
    String getFilename();

    /**
     * Get file content data, used with TYPE_ID_SCAN, TYPE_SELFIE, TYPE_CAMERA_IMAGE and TYPE_FINGERPRINT
     *
     * @return file data if available
     */
    byte[] getData();

    /**
     * Identity's email address, used with TYPE_EMAIL
     *
     * @return email address or null
     */
    String getEmailAddress();

    /**
     * Identity first name, used with TYPE_PERSONAL_INFORMATION
     *
     * @return first name or null
     */
    String getFirstname();

    /**
     * Identity last name, used with TYPE_PERSONAL_INFORMATION
     *
     * @return last name or null
     */
    String getLastname();

    /**
     * Identity contact address, used with TYPE_PERSONAL_INFORMATION
     *
     * @return contact address or null
     */
    String getContactAddress();

    /**
     * Identity contact city, used with TYPE_PERSONAL_INFORMATION
     *
     * @return contact city or null
     */
    String getContactCity();

    /**
     * Identity contact country, used with TYPE_PERSONAL_INFORMATION
     *
     * @return contact country or null
     */
    String getContactCountry();

    /**
     * Identity contact country ISO 3166 Alpha-2 code, used with TYPE_PERSONAL_INFORMATION
     *
     * @return contact country ISO code or null
     */
    String getContactCountryIso2();

    /**
     * Identity contact province, used with TYPE_PERSONAL_INFORMATION
     *
     * @return contact province (e.g. a state in the US) or null
     */
    String getContactProvince();

    /**
     * Identity contact ZIP, used with TYPE_PERSONAL_INFORMATION
     *
     * @return contact ZIP or null
     */
    String getContactZIP();

    /**
     * Country of identity document issuing jurisdiction, used with TYPE_PERSONAL_INFORMATION
     *
     * @return issuing jurisdiction country ISO 3166 Alpha-2 code or null
     */
    String getIssuingJurisdictionCountry();

    /**
     * Province of identity document issuing jurisdiction, used with TYPE_PERSONAL_INFORMATION
     *
     * @return issuing jurisdiction province (e.g. a state in the US) or null
     */
    String getIssuingJurisdictionProvince();

    /**
     * ID card number, used with TYPE_PERSONAL_INFORMATION
     *
     * @return identity's id card number if known
     */
    String getIdCardNumber();

    /**
     * Returns identification of person who created this identity piece.
     *
     * @return {@link IPerson} when known, {@code null} otherwise
     */
    IPerson getCreatedBy();

    /**
     * Document validity, used with TYPE_PERSONAL_INFORMATION
     *
     * @return {@link Date}
     */
    Date getDocumentValidTo();

    /**
     * used with TYPE_PERSONAL_INFORMATION, can be of:
     * <ul>
     *     <li>{@link IIdentityPiece#DOCUMENT_TYPE_ID_CARD}</li>
     *     <li>{@link IIdentityPiece#DOCUMENT_TYPE_PASSPORT}</li>
     *     <li>{@link IIdentityPiece#DOCUMENT_TYPE_DRIVING_LICENCE}</li>
     * </ul>
     *
     * @return null or above mentioned document type
     */
    Integer getDocumentType();

    /**
     * Date of birth, used with TYPE_PERSONAL_INFORMATION
     *
     * @return {@link Date}
     */
    Date getDateOfBirth();

    /**
     * Occupation, used with TYPE_PERSONAL_INFORMATION
     *
     * @return identity's job/occupation
     */
    String getOccupation();

    /**
     * SSN, used with TYPE_PERSONAL_INFORMATION
     *
     * @return null or social security number
     */
    String getSSN();

}
