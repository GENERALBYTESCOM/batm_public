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
package com.generalbytes.batm.server.extensions;

import com.generalbytes.batm.server.extensions.exceptions.SellException;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IExtensionContext {
    int DIRECTION_NONE          = 1;
    int DIRECTION_BUY_CRYPTO    = 2; //from customer view
    int DIRECTION_SELL_CRYPTO   = 4; //from customer view

    /**
     * Registers listener for listening to transaction events
     * @param listener
     */
    void addTransactionListener(ITransactionListener listener);

    /**
     * Stops listening for transaction events
     * @param listener
     * @return
     */
    boolean removeTransactionListener(ITransactionListener listener);


    //Email related stuff
    public static class EmbeddedEmailImage {
        public String contentId;
        public File pathToImage;
    }

    /**
     * Sends plain-text email asynchronously
     * @param from
     * @param addressListTo
     * @param subject
     * @param messageText
     */
    void sendMailAsync(final String from, final String addressListTo, final String subject, final String messageText);

    /**
     * Sends plain-text email containing attachment asynchronously
     * @param from
     * @param addresslistTo
     * @param subject
     * @param messageText
     * @param attachmentFileName
     * @param attachmentContent
     * @param attachmentMimeType
     */
    void sendMailAsyncWithAttachment(final String from, final String addresslistTo, final String subject, final String messageText, final String attachmentFileName, final byte[] attachmentContent, final String attachmentMimeType);

    /**
     * Sends email containing html text
     * @param from
     * @param addresslistTo
     * @param subject
     * @param messageText
     * @param embeddedEmailImages
     */
    void sendHTMLMailAsync(final String from, final String addresslistTo, final String subject, final String messageText, final EmbeddedEmailImage... embeddedEmailImages);

    /**
     * Sends email containing html and attachments
     * @param from
     * @param addresslistTo
     * @param subject
     * @param messageText
     * @param attachmentFileName
     * @param attachmentContent
     * @param attachmentMimeType
     */
    void sendHTMLMailAsyncWithAttachment(final String from, final String addresslistTo, final String subject, final String messageText, final String attachmentFileName, final byte[] attachmentContent, final String attachmentMimeType);

    /**
     * Sends SMS message to specified phone number asynchronously. Terminal serial number is used to detect country code prefix from its location
     * @param terminalSN
     * @param phonenumber
     * @param messageText
     */
    void sendSMSAsync(final String terminalSN, final String phonenumber, final String messageText);

    /**
     * Add task to server's task manager
     * @param name
     * @param tt
     * @param onFinish
     */
    void addTask(String name, final ITask tt, final Runnable onFinish);

    /**
     * Returns version of the server software. Useful for compatibility checks
     * @return
     */
    String getServerVersion();

    /**
     * Returns cash amount that is available to be dispensed by sell transactions.
     * Algorithm takes into account also that some of the banknotes are reserved(are excluded from sum) for upcoming withdrawals by other customers.
     * @param terminalSerialNumber
     * @param fiatCurrency
     * @return
     */
    BigDecimal calculateCashAvailableForSell(String terminalSerialNumber, String fiatCurrency);


    /**
     * Returns banknotes that are available to be dispensed by sell transactions.
     * Algorithm takes into account also that some of the banknotes are reserved(are excluded from sum) for upcoming withdrawals by other customers.
     * First String in returning
     * @param terminalSerialNumber
     * @param fiatCurrency
     * @return
     */
    Map<BigDecimal, Integer> getAvailableBanknotesConsideringFutureWithdrawals(String terminalSerialNumber, String fiatCurrency);


    /**
     * Call this transaction to create a sell transaction. After this call server will await crypto transaction to arrive and allocate cash for the customer.
     * @param fiatAmount
     * @param fiatCurrency
     * @param cryptoAmount - ignored - reserved for future.
     * @param cryptoCurrency
     * @param identityPublicId
     * @param discountCode
     * @return - read ITransactionSellInfo.getTransactionUUID() to find out what should be filled in sell QR code.
     */
    ITransactionSellInfo sellCrypto(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, BigDecimal cryptoAmount, String cryptoCurrency, String identityPublicId, String discountCode) throws SellException;


    /**
     * This method is used to get exchange rates of specific terminal and specified directions (@see DIRECTION_BUY_CRYPTO|DIRECTION_SELL_CRYPTO  etc)
     * @param terminalSerialNumber
     * @param directions
     * @return
     */
    Map<Integer,List<IExchangeRateInfo>> getExchangeRateInfo(String terminalSerialNumber, int directions);

    /**
     * This method is used to calculate crypto amounts for various cryotocurrencies based on one specified fiat amount and currency, optional discount code and identity id can influence calculation due to discounts
     * @param terminalSerialNumber
     * @param cryptoCurrencies
     * @param cashAmount
     * @param cashCurrency
     * @param direction
     * @param discountCode
     * @param identityPublicId
     * @return
     */
    Map<String,IAmountWithDiscount> calculateCryptoAmounts(String terminalSerialNumber, List<String> cryptoCurrencies, BigDecimal cashAmount, String cashCurrency, int direction, String discountCode, String identityPublicId);

    /**
     * Returns details about cash inside of the machine
     * @param terminalSerialNumber
     * @return
     */
    List<IBanknoteCounts> getCashBoxes(String terminalSerialNumber);


    /**
     * Returns list of all terminals registered on the system.
     * @return
     */
    List<ITerminal> findAllTerminals();

    /**
     * Returns terminal by serial number.
     * @param serialNumber
     * @return
     */
    ITerminal findTerminalBySerialNumber(String serialNumber);


    /**
     * Returns list of terminal serial numbers of terminals that have available cash to be dispensed by sell transactions.
     * Algorithm takes into account also if amount can be built by banknotes in output cash boxes and
     * if some of the banknotes are reserved for upcoming withdrawals by other customers.
     * @param fiatAmount
     * @param fiatCurrency
     * @param listOfTerminalSerialNumbers - null means all
     * @return
     */
    List<String> findTerminalsWithAvailableCashForSell(BigDecimal fiatAmount, String fiatCurrency,List<String> listOfTerminalSerialNumbers);

    /**
     * This method helps to you to package and store paper wallet in password encrypted archive.
     * It also includes privateKey and address in form of QR code into the archive.
     * Such package is safe to later be sent via email as used 7ZIP AES_STRENGTH_256 seems sufficient protection against brute-force.
     * @param privateKey
     * @param address
     * @param password
     * @param cryptoCurrency
     * @return
     */
    byte[] createPaperWallet7ZIP(String privateKey, String address, String password, String cryptoCurrency);
}
