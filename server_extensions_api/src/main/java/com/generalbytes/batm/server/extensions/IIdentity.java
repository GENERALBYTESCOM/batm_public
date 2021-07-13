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

/**
 * Representation of Identity. Identity is your customer, or more specifically, the detail about your customer.
 * Every transaction is initiated by an Identity, but how you treat that information is determined by your requirements.
 */
public interface IIdentity {

    int STATE_NOT_REGISTERED = 0;
    int STATE_REGISTERED = 1;
    int STATE_TO_BE_REGISTERED = 2;
    int STATE_PROHIBITED = 3;
    int STATE_ANONYMOUS = 4;
    int STATE_PROHIBITED_TO_BE_REGISTERED = 5;
    int STATE_TO_BE_VERIFIED = 6;

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
     *      <li>STATE_NOT_REGISTERED = 0 - "Not Registered"</li>
     *      <li>STATE_REGISTERED = 1 - "Registered"</li>
     *      <li>STATE_TO_BE_REGISTERED = 2 - "Awaiting Registration"</li>
     *      <li>STATE_PROHIBITED = 3 - "Rejected"</li>
     *      <li>STATE_ANONYMOUS = 4 - "Anonymous"</li>
     *      <li>STATE_PROHIBITED_TO_BE_REGISTERED = 5 - "Rejected Wants To Be Registered"</li>
     *      <li>STATE_TO_BE_VERIFIED = 6 - "Awaiting Verification"</li>
     * </ul>
     *
     * @return int value representing current identity state.
     */
    int getState();

    /**
     * Identity type, one of the following:
     * <ul>
     *     <li>TYPE_INTERNAL = 0; - must not be instantiated by extension</li>
     *     <li>TYPE_EXTERNAL = 1 -this identity was created externally (by extension)</li>
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
     * Deprecated, use {@link IIdentity#getNotes()} instead.
     *
     * @return Single note related to identity
     */
    @Deprecated
    String getNote();

    /**
     * Notes related to identity represented by {@link IIdentityNote}
     *
     * @return Collection of notes or empty collection when identity has no notes.
     */
    List<IIdentityNote> getNotes();

    /**
     * Identity pieces initialized by {@link IIdentityPiece#getPieceType()}
     *
     * @return Collection of identity pieces or empty collection when none is set
     */
    List<IIdentityPiece> getIdentityPieces();

    //Individual limits set on identity

    /**
     * Individual limit for a single transaction, transaction amount cannot exceed this limit.
     * Overrides limit set in AML/KYC settings applied for the transaction.
     *
     * @return Limits per currency, empty array when limits are not set
     */
    List<ILimit> getLimitCashPerTransaction();

    /**
     * Identity transaction limit. Sum of all identity's transaction amounts from last hour cannot exceed this limit.
     * Overrides limit set in AML/KYC settings applied for the transaction.
     *
     * @return Limits per currency, empty array when limits are not set
     */
    List<ILimit> getLimitCashPerHour();

    /**
     * Identity transaction limit. Sum of all identity's transaction amounts from last day cannot exceed this limit.
     * Overrides limit set in AML/KYC settings applied for the transaction.
     *
     * @return Limits per currency, empty array when limits are not set
     */
    List<ILimit> getLimitCashPerDay();

    /**
     * Identity transaction limit. Sum of all identity's transaction amounts from last week cannot exceed this limit.
     * Overrides limit set in AML/KYC settings applied for the transaction.
     *
     * @return Limits per currency, empty array when limits are not set
     */
    List<ILimit> getLimitCashPerWeek();

    /**
     * Identity transaction limit. Sum of all identity's transaction amounts from last month cannot exceed this limit.
     * Overrides limit set in AML/KYC settings applied for the transaction.
     *
     * @return Limits per currency, empty array when limits are not set
     */
    List<ILimit> getLimitCashPerMonth();

    /**
     * Identity transaction limit. Sum of all identity's transaction amounts from last 3 months cannot exceed this limit.
     * Overrides limit set in AML/KYC settings applied for the transaction.
     *
     * @return Limits per currency, empty array when limits are not set
     */
    List<ILimit> getLimitCashPer3Months();

    /**
     * Identity transaction limit. Sum of all identity's transaction amounts from last 12 months cannot exceed this limit.
     * Overrides limit set in AML/KYC settings applied for the transaction.
     *
     * @return Limits per currency, empty array when limits are not set
     */
    List<ILimit> getLimitCashPer12Months();

    /**
     * Identity transaction limit. Sum of all identity's transaction amounts from the beginning of calendar quarter cannot exceed this limit.
     * Overrides limit set in AML/KYC settings applied for the transaction.
     *
     * @return Limits per currency, empty array when limits are not set
     */
    List<ILimit> getLimitCashPerCalendarQuarter();

    /**
     * Identity transaction limit. Sum of all identity's transaction amounts from the beginning of year cannot exceed this limit.
     * Overrides limit set in AML/KYC settings applied for the transaction.
     *
     * @return Limits per currency, empty array when limits are not set
     */
    List<ILimit> getLimitCashPerCalendarYear();

    /**
     * Identity transaction limit. Sum of all identity's transaction amounts cannot exceed this limit.
     * Overrides limit set in AML/KYC settings applied for the transaction.
     *
     * @return Limits per currency, empty array when limits are not set
     */
    List<ILimit> getLimitCashTotalIdentity();

    /**
     * Defines currency for which limits are used.
     *
     * @return currency code
     */
    String getConfigurationCashCurrency();

    /**
     * Returns organization to which identity belongs.
     *
     * @return {@link IOrganization}
     */
    IOrganization getOrganization();
}
