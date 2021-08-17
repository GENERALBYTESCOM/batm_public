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

package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public interface ITransactionPreparation {
    //Types of transactions
    int TYPE_BUY_CRYPTO = 0;
    int TYPE_SELL_CRYPTO = 1;
    int TYPE_WITHDRAW_CASH = 2;


    int RESPONSE_WITHDRAWAL_NOT_POSSIBLE = 0;
    int RESPONSE_WITHDRAWAL_POSSIBLE = 1;

    int REASON_NONE = 0;
    int REASON_INVALID_UUID = 1;
    int REASON_WITHDRAWAL_ALREADY_PERFORMED = 2;
    int REASON_PAYMENT_NOT_RECEIVED = 3;
    int REASON_PAYMENT_STILL_ON_THE_WAY = 4;
    int REASON_TRANSACTION_EXPIRED = 5;
    int REASON_NOT_ENOUGH_CASH = 6;

    /**
     * Server time of the transaction
     *
     * @return
     */
    Date getServerTime();


    /**
     * Returns type of the transaction
     * {@value #TYPE_BUY_CRYPTO}
     * {@value #TYPE_SELL_CRYPTO}
     * {@value #TYPE_WITHDRAW_CASH}
     *
     * @return
     */
    int getType();

    /**
     * Returns serial number of the terminal where the transaction was created
     *
     * @return
     */
    String getTerminalSerialNumber();

    /**
     * Returns transaction id generated locally by terminal to perform request on server.
     * Don't use it if you don't have to. It is used only for time before server assignes remote transaction id to a transaction.
     *
     * @return
     */
    String getLocalTransactionId();


    /**
     * Cryptocurrency code (BTC, ETH etc)
     *
     * @return
     */
    String getCryptoCurrency();

    /**
     * Fiat currency code (USD, EUR etc)
     *
     * @return
     */
    String getCashCurrency();

    /**
     * Fiat amount - only valid when withdrawing
     *
     * @return
     */
    BigDecimal getCashAmount();

    /**
     * Destination address where the coins were sent to or where the coins were supposed to be sent to.
     *
     * @return
     */
    String getCryptoAddress();

    /**
     * Server internal identity id of person performing the transaction
     *
     * @return
     */
    String getIdentityPublicId();

    /**
     * Contains customer phonenumber that was used during transaction
     *
     * @return
     */
    String getCellPhoneUsed();


    /**
     * Returns language selected by customer on the terminal
     *
     * @return
     */
    String getLanguage();


    /**
     * Error message displayed to the customer
     * @return
     */
    String getErrorMessage();


    /**
     * Error message displayed to the customer
     * @return
     */
    void setErrorMessage(String errorMessage);


    /**
     * Maximum cash amount that customer can insert/sell into/to machine
     * @return
     */
    Map<String, BigDecimal> getCashTransactionLimit();

    /**
     * Maximum cash amount (with the limit name) that customer can insert/sell into/to machine
     * @return
     */
    Map<String, ITransactionLimit> getCashTransactionLimitWithName();

    /**
     * Minumum cash amount that customer can insert into machine
     * @return
     */
    Map<String, BigDecimal> getCashTransactionMinimum();

    /**
     * Maximum cash that customer can insert into machine due to low supply in server's wallet (must be explicitly enabled on terminal)
     * @return
     */
    Map<String, BigDecimal> getSupplyTransactionLimit();

    /**
     * Returns what was the fixed transaction fee for each fiat currency
     *
     * @return
     */
    Map<String, BigDecimal> getFixedTransactionFee();


    /**
     * Indicates that it is allowed to enter discount code on the terminal
     * @return
     */
    boolean getAllowedDiscountCode();

    /**
     * Used for sell transactions only - instructs UI what banknotes are available and what not.
     * @return
     */
    Map<String, Map<BigDecimal, Integer>> getAvailableBanknotes();

    /**
     * Indicates if withdrawal is possible or not
     * @return
     */
    int getWithdrawalResponse();

    /**
     * Holds information why cash withdrawal is not possible
     * @return
     */
    int getWithdrawalReason();

    /**
     * Allows you to set reason why cash cannot be withdrawn
     * @param reason
     */
    void setWithdrawalReason(int reason);


}
