/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
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

/**
 * Representation of Identity. Identity is your customer, or more specifically, the detail about your customer.
 * Every transaction is initiated by an Identity, but how you treat that information is determined by your requirements.
 * This interface represents only basic attributes. To obtain all entity data including related collections see {@link IIdentity}.
 */
public interface IIdentityBase {

    int STATE_NOT_REGISTERED = 0;
    int STATE_REGISTERED = 1;
    int STATE_TO_BE_REGISTERED = 2;
    int STATE_PROHIBITED = 3;
    int STATE_ANONYMOUS = 4;
    int STATE_PROHIBITED_TO_BE_REGISTERED = 5;
    int STATE_TO_BE_VERIFIED = 6;
    int STATE_PREMIUM = 7;

    int TYPE_INTERNAL = 0; //must not be instantiated by extension
    int TYPE_EXTERNAL = 1; //this identity was created externally

    /**
     * Always true.
     *
     * @return {@code true}
     */
    boolean isNew();

    /**
     * Generated public ID of identity.
     *
     * @return public ID
     */
    String getPublicId();

    /**
     * Custom external ID of identity.
     *
     * @return external ID
     */
    String getExternalId();

    /**
     * Identity state, one of the following:
     * <ul>
     *      <li>{@link IIdentity#STATE_NOT_REGISTERED} = 0 - "Not Registered"</li>
     *      <li>{@link IIdentity#STATE_REGISTERED} = 1 - "Registered"</li>
     *      <li>{@link IIdentity#STATE_TO_BE_REGISTERED} = 2 - "Awaiting Registration"</li>
     *      <li>{@link IIdentity#STATE_PROHIBITED} = 3 - "Rejected"</li>
     *      <li>{@link IIdentity#STATE_ANONYMOUS} = 4 - "Anonymous"</li>
     *      <li>{@link IIdentity#STATE_PROHIBITED_TO_BE_REGISTERED} = 5 - "Rejected Wants To Be Registered"</li>
     *      <li>{@link IIdentity#STATE_TO_BE_VERIFIED} = 6 - "Awaiting Verification"</li>
     *      <li>{@link IIdentity#STATE_PREMIUM} = 7 - "Premium"</li>
     * </ul>
     *
     * @return int value representing current identity state.
     */
    int getState();

    /**
     * Identity type, one of the following:
     * <ul>
     *     <li>{@link IIdentity#TYPE_INTERNAL} = 0 - must not be instantiated by extension</li>
     *     <li>{@link IIdentity#TYPE_EXTERNAL} = 1 -this identity was created externally (by extension)</li>
     * </ul>
     *
     * @return int value representing type, if not provided defaults to TYPE_INTERNAL
     */
    int getType();

    /**
     * Date when identity was created.
     *
     * @return {@link Date}
     */
    Date getCreated();

    /**
     * Date when identity state changed to STATE_REGISTERED
     *
     * @return {@link Date}
     */
    Date getRegistered();

    /**
     * Returns serial number of terminal that created this identity or was assigned to manually created one.
     *
     * @return serial number
     */
    String getCreatedByTerminalSerialNumber();

    /**
     * Returns identification of person who registered this identity.
     *
     * @return {@link IPerson} when known, {@code null} otherwise
     */
    IPerson getRegisteredBy();

    /**
     * Discount From Buy Profit Fee.
     *
     * @return Value of the discount, may return {@code null} when not set.
     */
    BigDecimal getVipBuyDiscount();

    /**
     * Discount From Sell Profit Fee.
     *
     * @return Value of the discount, may return {@code null} when not set.
     */
    BigDecimal getVipSellDiscount();

    /**
     * Last modification date.
     *
     * @return {@link Date}
     */
    Date getLastUpdatedAt();

    /**
     * Last date when identity was checked against watchlist(s).
     *
     * @return {@link Date}
     */
    Date getWatchListLastScanAt();

    /**
     * Informs if identity was banned due to presence in a watchlist.
     *
     * @return {@code true} if banned, {@code false} otherwise
     */
    boolean isWatchListBanned();

    /**
     * Returns organization to which identity belongs.
     *
     * @return {@link IOrganization}
     */
    IOrganization getOrganization();

    /**
     * Returns last used phone number if it's known.
     *
     * @return phone number
     */
    String getLatestPhoneNumber();
}
