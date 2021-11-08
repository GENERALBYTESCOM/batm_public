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

import java.util.List;

/**
 * Representation of Identity. Identity is your customer, or more specifically, the detail about your customer.
 * Every transaction is initiated by an Identity, but how you treat that information is determined by your requirements.
 */
public interface IIdentity extends IIdentitySimple {


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
