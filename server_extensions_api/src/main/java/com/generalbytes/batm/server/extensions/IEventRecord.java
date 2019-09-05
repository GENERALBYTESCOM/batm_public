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
package com.generalbytes.batm.server.extensions;

import java.util.Date;

public interface IEventRecord {
    int TYPE_NONE = 0;
    int TERMINAL_STARTED = 2;
    int TERMINAL_CONNECTED = 3;
    int TRANSACTION_SUBMITTED = 4;
    int TRANSACTION_RECEIVED = 5;
    int TRANSACTION_ACCEPTED = 6;
    int TRANSACTION_REFUSED = 7;
    int TRANSACTION_PREPARED = 8;
    int UPGRADE_STARTED = 9;
    int UPGRADE_DONE = 10;
    int UPGRADE_FAILED = 11;
    int COUNTERS_SHORT_CLEARED = 12;
    int TYPE_CASH_INSERTED_LOCAL = 13;
    int TYPE_CASH_INSERTED_REMOTE = 14;

    int TRANSACTION_LIMIT_REACHED = 15;
    int TYPE_CASH_DISPENSED_LOCAL = 16;
    int TYPE_CASH_DISPENSED_REMOTE = 17;
    int CASH_SET_REMOTE = 18;
    int DISPENSE_REQUEST_RECEIVED = 19;
    int DISPENSE_RESULT_RECEIVED = 20;

    int TYPE_CASH_TRANSFERRED_REMOTE = 21;
    int TYPE_CASH_TRANSFERRED_LOCAL = 22;

    int TERMINAL_BALANCE_CLEARED = 23;

    int TRANSACTION_SUPPLY_LIMIT_REACHED = 24;

    int COINNECTIONS_SENT = 25;
    int COINNECTIONS_ERROR = 26;

    int BLACKLISTED_ADDRESS = 27;

    int CASH_SET_IN_CAS = 28;

    // 30 - 36 used !!! continue e.g. with 50 => 27, 28, 29, 50, 51, ... 99

    int PROOF_CREATED = 301;
    int PROOF_NOT_CREATED = 302;

    int ACC_ERROR                    = 30;
    int ACC_FRAUD_DETECTED           = 31;
    int ACC_STACKER_IN               = 32;
    int ACC_STACKER_OUT              = 33;
    int ACC_STACKER_FULL             = 34;
    int ACC_DEBUG                    = 35;
    int ACC_JAM                      = 36;
    int ACC_ACCEPTOR_IS_NOT_DETECTED = 37;

    int UI_STATS_SCREEN_SAVER_ENTERED    = 100;
    int UI_STATS_SCREEN_ERROR_ENTERED    = 101;
    int UI_STATS_SCREEN_ADMINISTRATION_ENTERED = 102;
    int UI_STATS_SCREEN_LANGUAGE_ENTERED = 103;
    int UI_STATS_SCREEN_DESTINATION_ADDRESS_ENTERED = 104;
    int UI_STATS_SCREEN_INSERT_CASH_ENTERED         = 105;
    int UI_STATS_SCREEN_TRANSACTION_IN_PROGRESS_ENTERED = 106;
    int UI_STATS_SCREEN_TRANSACTION_FAILED_ENTERED = 107;
    int UI_STATS_SCREEN_TRANSACTION_DONE_ENTERED = 108;
    int UI_STATS_SCREEN_CHOOSE_LIMIT_ENTERED = 109;
    int UI_STATS_SCREEN_PRIVACY_NOTICE = 110;
    int UI_STATS_SCREEN_COLLECT_FINGER_ENTERED = 111;
    int UI_STATS_SCREEN_COLLECT_ID_CARD_ENTERED = 112;
    int UI_STATS_SCREEN_COLLECT_EMAIL_ENTERED = 113;
    int UI_STATS_SCREEN_ARE_YOU_REGISTERED = 114;
    int UI_STATS_SCREEN_SUBMIT_REGISTRATION = 115;
    int UI_STATS_SCREEN_NO_WALLET_ENTERED = 116;
    int UI_STATS_SCREEN_DESTINATION_EMAIL_ENTERED = 117;
    int UI_STATS_SCREEN_ONE_TIME_PASSWORD = 118;
    int UI_STATS_SCREEN_CHOOSE_ALTCOIN_ENTERED = 119;
    int UI_STATS_SCREEN_COLLECT_PHONENUMBER_ENTERED = 120;
    int UI_STATS_SCREEN_CHECK_WALLET_BALANCE_ENTERED = 121;
    int UI_STATS_SCREEN_TERMS_AND_CONDITIONS_ENTERED = 122;
    int UI_STATS_SCREEN_SELL_CHOOSE_AMOUNT_ENTERED = 123;
    int UI_STATS_SCREEN_SCAN_REDEEM_TICKET_ENTERED = 124;
    int UI_STATS_SCREEN_COLLECT_SELFIE_ENTERED = 125;
    int UI_STATS_SCREEN_COLLECT_NAME_ENTERED = 126;
    int UI_STATS_SCREEN_STORE_LISTING_ENTERED = 130;
    int UI_STATS_SCREEN_STORE_CATEGORY_ENTERED = 131;
    int UI_STATS_SCREEN_STORE_ARTICLE_ENTERED = 132;
    int UI_STATS_SCREEN_STORE_CART_ENTERED = 134;
    int UI_STATS_SCREEN_STORE_DELIVERY_ENTERED = 135;
    int UI_STATS_SCREEN_PAYMENT_ENTERED = 136;

    int UI_STATS_SCREEN_POS_ENTER_AMOUNT_ENTERED = 140;

    int UI_STATS_SCREEN_CASHBOXES_ENTERED = 151;
    int UI_STATS_SCREEN_CASHBOXES_COLLECTION_ENTERED = 152;

    int UI_STATS_SCREEN_VIDEO_HELP_ENTERED = 153;

    int UI_STATS_SCREEN_CHOOSE_RECEIPT_DELIVERY_SMS_OR_EMAIL_ENTERED = 154;
    int UI_STATS_SCREEN_RECEIPT_DELIVERED_ENTERED = 155;

    int UI_STATS_SCREEN_COLLECT_PHONE_NUMBER_FOR_NO_WALLET_ENTERED = 156;
    int UI_STATS_SCREEN_COLLECT_PHONE_NUMBER_FOR_RECEIPT_DELIVERY_ENTERED = 157;

    int UI_STATS_SCREEN_OTHER_LOCATIONS_MAP_HELP_ENTERED = 158;

    int UI_STATS_SCREEN_DISPENSE_CARD_ENTERED = 159;

    int UI_STATS_SCREEN_COLLECT_PHONE_NUMBER_FOR_CARD_DISPENSING_ENTERED = 160;

    int UI_STATS_SCREEN_NFC_CARD_CHOOSE_ACTION_ENTERED = 161;
    int UI_STATS_SCREEN_NFC_CARD_LIST_OF_TRANSACTIONS_ENTERED = 162;
    int UI_STATS_SCREEN_NFC_CARD_SEND_SELECTED_AMOUNT_ENTERED = 163;
    int UI_STATS_SCREEN_NFC_CARD_FINAL_INFO_ENTERED = 164;

    int UI_STATS_SCREEN_CHOOSE_CASH_CURRENCY_ENTERED = 165;

    int UI_STATS_ACTION_QR_CODE_SCANNED = 200;
    int UI_STATS_ACTION_STORE_ARTICLE_ADDED = 201;
    int UI_STATS_ACTION_WALLET_PRINTED = 202;
    int UI_STATS_ACTION_TERMS_AND_CONDITIONS_AGREED = 203;


    int CURRENCY_MISMATCH = 204;

    int PAPER_LOW = 205;
    int PAPER_MISSING = 206;
    int PAPER_REFILL = 207;
    int NO_NFC_CARD = 208;
    int LAST_NFC_DISPENSE_ERROR = 209;
    int NFC_COLLECT_BIN_FULL = 210;
    int NFC_CARDS_RUNNING_LOW = 211;
    int PRINTER_DISCONNECTED = 212;
    int WITHDRAWAL_NOT_POSSIBLE = 213;
    int CUSTOMER_IS_POLITICALLY_EXPOSED_PERSON = 214;
    int CUSTOMER_IS_NOT_POLITICALLY_EXPOSED_PERSON = 215;
    int REBOOT_NO_CONNECTION = 216;
    int WITHDRAWAL_FAILED = 217;

    /**
     * Returns serial number of a terminal
     * @return
     */
    String getTerminalSerialNumber();

    /**
     * Return event type @see constants in this interface
     * @return
     */
    int getType();

    /**
     * Returns string representation of event type just in case you want to print it somewhere.
     * @return
     */
    String getTypeAsText();

    /**
     * Returns data part of the event.
     * @return
     */
    String getData();

    /**
     * Returns data part of the event in text representation. Usually some of the enums are translated.
     * @return
     */
    String getReadableData();

    /**
     * Timestamp when event was created on terminal
     * @return
     */
    Date getTerminalTime();

    /**
     * Timestamp when event was delivered and stored on server
     * @return
     */
    Date getServerTime();
}
