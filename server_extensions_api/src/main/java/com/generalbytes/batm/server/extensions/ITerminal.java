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

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ITerminal {

    int TYPE_PHYSICAL = 0;
    int TYPE_VIRTUAL = 1;

    int MODE_REJECTED_REASON_UNKNOWN                          = 0;
    int MODE_REJECTED_REASON_NOT_REGISTERED_ON_SERVER         = 1;
    int MODE_REJECTED_REASON_NOT_PAIRED                       = 2;
    int MODE_REJECTED_REASON_EXCHANGE_RATE_NOT_AVAILABLE      = 3;
    int MODE_REJECTED_REASON_IP_WHITELIST                     = 4;
    int MODE_REJECTED_REASON_CLIENT_CERT_FINGERPRINT_MISMATCH = 5;
    int MODE_REJECTED_REASON_OUTSIDE_OF_OPENING_HOURS         = 6;

    int MODE_OPERATIONAL_ALL_OK              = 0;
    int MODE_REJECTED_BY_SERVER              = 1;
    int MODE_OPERATION_SUSPENDED_BY_OPERATOR = 2;

    long ERROR_NO_ERROR                                    = 0; //"NO ERROR"
    long ERROR_SERVER_IS_NOT_REACHABLE                     = 1 <<  0; //"SERVER IS NOT REACHABLE"
    long ERROR_ACCEPTOR_STACKER_OUT                        = 1 <<  1; //"ACCEPTOR STACKER OUT"
    long ERROR_ACCEPTOR_STACKER_FULL                       = 1 <<  2; //"ACCEPTOR STACKER FULL"
    long ERROR_ACCEPTOR_OTHER_ERROR                        = 1 <<  3; //"ACCEPTOR UNKNOWN ERROR"
    long ERROR_NO_CAMERA                                   = 1 <<  4; //"NO CAMERA"
    long WARNING_NO_PRINTER_PAPER                          = 1 <<  5; //"PAPER MISSING"
    long ERROR_ACCEPTOR_JAM_ERROR                          = 1 <<  6; //"ACCEPTOR JAM ERROR"
    long WARNING_NO_NFC_CARD                               = 1 <<  7; //"NFC CARD MISSING"
    long ERROR_CONNECTION_REJECTED                         = 1 <<  8; //"CONNECTION REJECTED ERROR"
    long ERROR_CONNECTION_TIMEOUT                          = 1 <<  9; //"CONNECTION TIMEOUT ERROR"
    long UNKNOWN_NETWORK_ERROR                             = 1 << 10; //"UNKNOWN NETWORK ERROR"
    long ERROR_TLS_SERVER_CERTIFICATE_FINGERPRINT_MISMATCH = 1 << 11; //"SERVER CERTIFICATE MISMATCH"
    long ERROR_TLS_ERROR                                   = 1 << 12; //"TLS ERROR"
    long WARNING_PRINTER_PAPER_LOW                         = 1 << 13; //"PAPER LOW"
    long WARNING_NFC_COLLECT_BIN_FULL                      = 1 << 14; //"NFC COLLECT BIN FULL"
    long WARNING_LAST_NFC_DISPENSE_ERROR                   = 1 << 15; //"LAST NFC DISPENSE ERROR"
    long WARNING_NFC_CARDS_RUNNING_LOW                     = 1 << 16; //"NFC CARDS RUNNING LOW"
    long ERROR_PRINTER_DISCONNECTED                        = 1 << 17; //"PRINTER DISCONNECTED"
    long ERROR_ACCEPTOR_IS_NOT_DETECTED                    = 1 << 18; //"ACCEPTOR IS NOT DETECTED"

    /**
     * Get the type of this terminal. See TYPE_PHYSICAL and TYPE_VIRTUAL.
     *
     * @return The type.
     */
    Integer getType();

    /**
     * Get the serial number of this terminal.
     *
     * @return The serial number.
     */
    String getSerialNumber();

    /**
     * Get the name of this terminal.
     *
     * @return The name.
     */
    String getName();

    /**
     * Terminal is active = Server will not refuse terminal communication requests
     *
     * @return True if this terminal is active
     */
    boolean isActive();

    /**
     * Terminal is locked
     *
     * @return True if this terminal is locked
     */
    boolean isLocked();

    /**
     * Deleted terminals are not displayed in administration but are still stored
     * in a database and marked as deleted.
     *
     * @return True if this terminal is deleted
     */
    boolean isDeleted();

    /**
     * Get the location of this terminal.
     *
     * @return The location
     */
    ILocation getLocation();

    /**
     * Get the server time of the moment this terminal last connected to the server.
     *
     * @return The date and time
     */
    Date getConnectedAt();

    /**
     * Get the server time of the moment the server last received a ping request from this terminal.
     *
     * @return The date and time
     */
    Date getLastPingAt();

    /**
     * Duration in number of milliseconds of last ping operation measured by terminal (request + response time)
     *
     * @return The duration
     */

    long getLastPingDuration();

    /**
     * Server date and time when the terminal asked for exchange rate the last time
     *
     * @return The date and time
     */
    Date getExchangeRateUpdatedAt();

    /**
     * String representation of BUY exchange rate displayed on terminal on last successful ping
     *
     * @return The exchange rate
     */
    String getExchangeRatesBuy();

    /**
     * String representation of SELL exchange rate displayed on terminal on last successful ping
     *
     * @return The exchange rate
     */
    String getExchangeRatesSell();

    /**
     * Terminal error signalized by bit value see error constants in this interface ie ERROR_NO_ERROR
     *
     * @return The error code
     */
    long getErrors();

    /**
     * Signalizes current mode in which is terminal operating see different modes in this interface i.e.: MODE_
     *
     * @return The mode in which is terminal operating
     */
    int getOperationalMode();

    /**
     * When a server refuses to talk to the terminal, it signalizes this by a REASON.
     * See the list of possible reasons in this interface:
     * MODE_REJECTED_REASON_...
     *
     * @return The reason why the server refused to talk to the terminal
     */
    int getRejectedReason();

    /**
     * Returns list of enabled fiat currencies on this terminal.
     *
     * @return The list of fiat currencies
     */
    List<String> getAllowedCashCurrencies();

    /**
     * Returns list of enabled cryptocurrencies on this terminal.
     *
     * @return The list of cryptocurrencies
     */
    List<String> getAllowedCryptoCurrencies();

    /**
     * Returns a set of tags that are attached to this terminal.
     *
     * @return The tags.
     */
    Set<String> getTags();

}
