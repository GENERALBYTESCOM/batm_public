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

import com.generalbytes.batm.server.extensions.exceptions.CashbackException;
import com.generalbytes.batm.server.extensions.exceptions.SellException;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;

import java.io.File;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IExtensionContext {

    int DIRECTION_NONE          = 1;
    int DIRECTION_BUY_CRYPTO    = 2; //from customer view
    int DIRECTION_SELL_CRYPTO   = 4; //from customer view

    int PERMISSION_NONE = 0;
    int PERMISSION_READ = 1;
    int PERMISSION_WRITE = 2;
    int PERMISSION_EXECUTE = 4;

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

    /**
     * Register listener for terminal events
     */
    void addTerminalListener(ITerminalListener listener);

    void removeTerminalListener(ITerminalListener listener);

    /**
     * Finds and returns transaction by given remote or local transaction id
     * @param remoteOrLocalTransactionId
     * @return
     */
    ITransactionDetails findTransactionByTransactionId(String remoteOrLocalTransactionId);

    /**
     * Finds and returns transactions performed by identity
     * @param publicIdentityId
     * @return
     */
    List<ITransactionDetails> findAllTransactionsByIdentityId(String publicIdentityId);

    /**
     * Finds person by chat user id
     * @param chatUserId
     * @return
     */
    IPerson findPersonByChatId(String chatUserId);

    /**
     * Check if person has access to provided object.
     * For example person needs to be in same organization or needs to have at least read-only role
     * @param permissionLevel
     * @param person
     * @param obj
     * @return
     */
    boolean hasPersonPermissionToObject(int permissionLevel, IPerson person, Object obj);

    /**
     * Checks whether the terminal belongs to same organization as person.
     * @param terminalSerialNumber
     * @param person
     * @return
     */
    boolean isTerminalFromSameOrganizationAsPerson(String terminalSerialNumber, IPerson person);

    /**
     * Finds and returns identity based on provided publicIdentityId
     * @param publicIdentityId
     * @return
     */
    IIdentity findIdentityByIdentityId(String publicIdentityId);

    /**
     * Finds and returns identity based on provided phone number. This number has to be in international format (leading +countrycode is required)
     * @param phoneNumber
     * @return
     */
    List<IIdentity> findIdentitiesByPhoneNumber(String phoneNumber);


    /**
     * Finds and returns all identities of given state)
     * @param state @see IIdentity
     * @return
     */
    List<IIdentity> findAllIdentitiesByState(int state);


    /**
     * Finds and returns identity based on provided phone number. If you don't specify country then number has to be in international format (leading +countrycode is required)
     * @param phoneNumber
     * @return
     */
    List<IIdentity> findIdentityByPhoneNumber(String phoneNumber, String countryName);

    /**
     * @param documentNumber
     * @return identities with the provided document (ID, passport, ... ) number. Empty list if nothing is found, never null.
     */
    List<IIdentity> findIdentitiesByDocumentNumber(String documentNumber);

    /**
     *
     * @param configurationCashCurrency
     * @param terminalSerialNumber
     * @param externalId
     * @param limitCashPerTransaction
     * @param limitCashPerHour
     * @param limitCashPerDay
     * @param limitCashPerWeek
     * @param limitCashPerMonth
     * @param note
     * @param state see {@link IIdentity#STATE_REGISTERED} etc.
     * @param vipBuyDiscount buy fee discount in percent
     * @param vipSellDiscount sell fee discount in percent
     * @param created
     * @param registered
     * @return Identity with generated ID (identityPublicId)
     */
    IIdentity addIdentity(String configurationCashCurrency, String terminalSerialNumber, String externalId, List<ILimit> limitCashPerTransaction, List<ILimit> limitCashPerHour, List<ILimit> limitCashPerDay, List<ILimit> limitCashPerWeek, List<ILimit> limitCashPerMonth, String note, int state, BigDecimal vipBuyDiscount, BigDecimal vipSellDiscount, Date created, Date registered);

    /**
     *
     * @param configurationCashCurrency
     * @param terminalSerialNumber
     * @param externalId
     * @param limitCashPerTransaction
     * @param limitCashPerHour
     * @param limitCashPerDay
     * @param limitCashPerWeek
     * @param limitCashPerMonth
     * @param note
     * @param state see {@link IIdentity#STATE_REGISTERED} etc.
     * @param vipBuyDiscount buy fee discount in percent
     * @param vipSellDiscount sell fee discount in percent
     * @param created
     * @param registered
     * @param language
     * @return Identity with generated ID (identityPublicId)
     */
    IIdentity addIdentity(String configurationCashCurrency, String terminalSerialNumber, String externalId, List<ILimit> limitCashPerTransaction, List<ILimit> limitCashPerHour, List<ILimit> limitCashPerDay, List<ILimit> limitCashPerWeek, List<ILimit> limitCashPerMonth, String note, int state, BigDecimal vipBuyDiscount, BigDecimal vipSellDiscount, Date created, Date registered, String language);

    /**
     * adds Identity Piece to an identity specified by identityPublicId
     * @param identityPublicId
     * @param iidentityPiece
     * @return true in case of success
     */
    boolean addIdentityPiece(String identityPublicId, IIdentityPiece iidentityPiece);

    /**
     * @param identityId     public ID of an existing identity to be updated
     * @param state new state to be set
     * @param note new note to be set
     * @param limitCashPerWeek
     * @param limitCashPer3Months
     * @param limitCashPer12Months
     * @param limitCashPerCalendarQuarter
     * @param limitCashPerCalendarYear
     * @param limitCashTotalIdentity
     * @param configurationCashCurrency
     * @return updated identity
     */
    IIdentity updateIdentity(String identityId, String externalId, int state, int type, Date created, Date registered,
                             BigDecimal vipBuyDiscount, BigDecimal vipSellDiscount, String note,
                             List<ILimit> limitCashPerTransaction, List<ILimit> limitCashPerHour, List<ILimit> limitCashPerDay, List<ILimit> limitCashPerWeek,
                             List<ILimit> limitCashPerMonth, List<ILimit> limitCashPer3Months, List<ILimit> limitCashPer12Months, List<ILimit> limitCashPerCalendarQuarter,
                             List<ILimit> limitCashPerCalendarYear, List<ILimit> limitCashTotalIdentity, String configurationCashCurrency);

    /**
     *
     * @return a tunnel manager that allows creating ssh tunnels on a remote ssh server.
     * Used for creating encrypted tunnels for wallets on remote hosts.
     * See {@link ITunnelManager#connectIfNeeded(String, InetSocketAddress)}
     */
    ITunnelManager getTunnelManager();

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
     * @param replyTo - optional
     */
    void sendMailAsync(final String from, final String addressListTo, final String subject, final String messageText, String replyTo);

    /**
     * Sends plain-text email containing attachment asynchronously
     * @param from
     * @param addresslistTo
     * @param subject
     * @param messageText
     * @param attachmentFileName
     * @param attachmentContent
     * @param attachmentMimeType
     * @param replyTo - optional
     */
    void sendMailAsyncWithAttachment(final String from, final String addresslistTo, final String subject, final String messageText, final String attachmentFileName, final byte[] attachmentContent, final String attachmentMimeType, String replyTo);

    /**
     * Sends email containing html text
     * @param from
     * @param addresslistTo
     * @param subject
     * @param messageText
     * @param embeddedEmailImages
     */
    void sendHTMLMailAsync(final String from, final String addresslistTo, final String subject, final String messageText, String replyTo, final EmbeddedEmailImage... embeddedEmailImages);

    /**
     * Sends email containing html and attachments
     * @param from
     * @param addresslistTo
     * @param subject
     * @param messageText
     * @param attachmentFileName
     * @param attachmentContent
     * @param attachmentMimeType
     * @param replyTo - optional
     */
    void sendHTMLMailAsyncWithAttachment(final String from, final String addresslistTo, final String subject, final String messageText, final String attachmentFileName, final byte[] attachmentContent, final String attachmentMimeType, String replyTo);

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
     * @param cryptoAmount - ignored but must be filled - reserved for future.
     * @param cryptoCurrency
     * @param identityPublicId
     * @param discountCode
     * @return - read ITransactionSellInfo.getTransactionUUID() to find out what should be filled in sell QR code.
     */
    ITransactionSellInfo sellCrypto(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, BigDecimal cryptoAmount, String cryptoCurrency, String identityPublicId, String discountCode) throws SellException;

    /**
     * Call this transaction to create a cash back transaction. After this call server will allocate cash for the customer that can visit machine and withdraw cash.
     * @param fiatAmount
     * @param fiatCurrency
     * @param identityPublicId
     * @return - read ITransactionSellInfo.getTransactionUUID() to find out what should be filled in sell QR code.
     */
    ITransactionCashbackInfo cashback(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, String identityPublicId) throws CashbackException;


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

    /**
     * Returns time format by person. In US users they prefer mm/dd/yyyy anywhere else dd.mm.yyyy
     * @param person
     * @return
     */
    SimpleDateFormat getTimeFormatByPerson(IPerson person);

    /**
     * Returns crypto configurations used by terminals of specified serial numbers.
     * @param serialNumbers
     * @return
     */
    List<ICryptoConfiguration> findCryptoConfigurationsByTerminalSerialNumbers(List<String> serialNumbers);

    /**
     * Check presence of person or entity on watch lists
     * @param query
     * @return
     */
    WatchListResult searchWatchList(WatchListQuery query);

    /**
     * Queries the phone number for its type and if it is blocked based on the settings on terminal organization.
     * Same functionality as "Use GB API For Blocking Not Registered Identities By Certain Line Type". Charged Service.
     * @param phoneNumber phone number to be queried. If missing, the country prefix is determined by the terminal location.
     * @param terminalSerialNumber terminal country and owner (Organization) are used while querying the phone number
     * @return
     */
    PhoneNumberQueryResult queryPhoneNumber(String phoneNumber, String terminalSerialNumber);

    /**
     * Returns Cash Collections ordered by sequence ID (primary key).
     * @param terminalSerialNumber
     * @param dateFrom
     * @param dateTo
     * @return
     */
    List<ITerminalCashCollectionRecord> getCashCollections(String terminalSerialNumber, Date dateFrom, Date dateTo);

    /**
     * Returns Event Logs ordered by ordered by sequence ID (primary key).
     * @param terminalSerialNumber
     * @param dateFrom
     * @param dateTo
     * @return
     */
    List<IEventRecord> getEvents(String terminalSerialNumber, Date dateFrom, Date dateTo);
}
