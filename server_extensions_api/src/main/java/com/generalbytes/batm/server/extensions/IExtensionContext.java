/*************************************************************************************
 * Copyright (C) 2014-2025 GENERAL BYTES s.r.o. All rights reserved.
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

import com.generalbytes.batm.server.extensions.customfields.CustomField;
import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinition;
import com.generalbytes.batm.server.extensions.aml.verification.ApplicantCheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityApplicant;
import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinitionAvailability;
import com.generalbytes.batm.server.extensions.customfields.value.CustomFieldValue;
import com.generalbytes.batm.server.extensions.exceptions.BuyException;
import com.generalbytes.batm.server.extensions.exceptions.CashbackException;
import com.generalbytes.batm.server.extensions.exceptions.SellException;
import com.generalbytes.batm.server.extensions.exceptions.UpdateException;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderIdentification;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.IVaspIdentification;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;

import java.io.File;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IExtensionContext {

    int DIRECTION_NONE          = 1;
    int DIRECTION_BUY_CRYPTO    = 2; //from customer view
    int DIRECTION_SELL_CRYPTO   = 4; //from customer view

    int PERMISSION_NONE = 0;
    int PERMISSION_READ = 1;
    int PERMISSION_WRITE = 2;
    int PERMISSION_EXECUTE = 4;

    void addApplicationListener(IApplicationListener listener);

    boolean removeApplicationListener(IApplicationListener listener);

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
     * Registers a listener for receiving notification
     */
    void addNotificationListener(INotificationListener listener);

    /**
     * Stops a listener from receiving notification
     */
    void removeNotificationListener(INotificationListener listener);

    /**
     * Register listener for terminal events
     */
    void addTerminalListener(ITerminalListener listener);

    void removeTerminalListener(ITerminalListener listener);

    /**
     * Register listener for terminal events
     */
    void addIdentityListener(IIdentityListener listener);

    void removeIdentityListener(IIdentityListener listener);


    /**
     * Finds and returns transaction by given remote or local transaction id
     * @param remoteOrLocalTransactionId
     * @return
     */
    ITransactionDetails findTransactionByTransactionId(String remoteOrLocalTransactionId);

    ITransactionDetails findTransactionByTransactionUUIDAndType(String uuid, int type);

    /**
     * Finds and returns transactions performed by identity
     * @param publicIdentityId
     * @return
     */
    List<ITransactionDetails> findAllTransactionsByIdentityId(String publicIdentityId);

    /**
     * @param terminalSerialNumber
     * @param serverTimeFrom limit returned transactions by server time of the transaction
     * @param serverTimeTo
     * @param previousRID if not null only transactions NEWER than this one are returned
     * @param includeBanknotes adds banknote information to the result (performs extra db queries)
     * @return
     */
    List<ITransactionDetails> findTransactions(String terminalSerialNumber, Date serverTimeFrom, Date serverTimeTo, String previousRID, boolean includeBanknotes);

    /**
     * @param rid    remote transaction ID of the transaction to be updated
     * @param status new status to be set or null to keep it unmodified
     * @param detail detail message to be appended if there already is a detail set. Null to keep it unmodified
     * @param tags   tags to be set to the transaction.
     *               If the transaction has some tags assigned already, those will be removed (un-assigned)
     *               and only the tags provided here would be set.
     *               Providing an empty set will un-assign all existing tags of the transaction.
     *               Null keeps the existing tags unchanged.
     *               Tags that are being set must exist (see {@link #getTransactionTags(String)}), non-existent tags are ignored.
     * @return modified transaction details
     * @throws UpdateException if the update was not successful
     */
    ITransactionDetails updateTransaction(String rid, Integer status, String detail, Set<String> tags) throws UpdateException;

    /**
     * @param rid        remote transaction ID of the transaction to be updated
     * @param status     new status to be set or null to keep it unmodified
     * @param detail     detail message to be appended if there already is a detail set. Null to keep it unmodified
     * @param customData custom data to be set to the transaction.
     *                   This will replace existing custom data stored for the transaction.
     *                   If you need to keep existing data obtain them first using {@link ITransactionDetails#getCustomData()}.
     *                   Providing an empty map will remove all existing custom data.
     *                   Null keeps the existing custom data unchanged.
     * @return modified transaction details
     * @throws UpdateException if the update was not successful
     */
    ITransactionDetails updateTransaction(String rid, Integer status, String detail,  Map<String, String> customData) throws UpdateException;

    ITransactionDetails updateTransaction(String rid, Integer status, String detail) throws UpdateException;

    /**
     * @return Names of organization tags that could be assigned to transactions
     */
    Set<String> getTransactionTags(String organizationId);

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
     * Finds and returns identity based on provided publicIdentityId
     * @return identity with only basic attributes without related data represented by collections
     */
    IIdentityBase findIdentityBaseByIdentityId(String publicIdentityId);

    /**
     * Finds and returns identity based on provided phone number. This number has to be in international format (leading +countrycode is required)
     * @param phoneNumber
     * @return
     */
    List<IIdentity> findIdentitiesByPhoneNumber(String phoneNumber);

    /**
     * Finds and returns identity based on provided phone number. This number has to be in international format (leading +countrycode is required)
     * @param phoneNumber
     * @return List of identities (with only basic attributes without related data represented by collections)
     */
    List<IIdentityBase> findIdentitiesBaseByPhoneNumber(String phoneNumber);

    /**
     * Finds and returns all identities of given state)
     * @param state @see IIdentity
     * @return
     */
    List<IIdentity> findAllIdentitiesByState(int state);

    /**
     * Finds and returns all identities of given state)
     * @param state @see IIdentity
     * @return list of identities with only basic attributes without related data represented by collections
     */
    List<IIdentityBase> findAllIdentitiesBaseByState(int state);

    /**
     * Finds and returns identity based on provided phone number. If you don't specify country then number has to be in international format (leading +countrycode is required)
     * @param phoneNumber
     * @return
     */
    List<IIdentity> findIdentityByPhoneNumber(String phoneNumber, String countryName);

    /**
     * Finds and returns identity based on provided phone number. If you don't specify country then number has to be in international format (leading +countrycode is required)
     *
     * @param phoneNumber phone number
     * @param countryCode ISO 3166 Alpha-2 code
     * @return List of found IIdentity
     */
    List<IIdentity> findIdentitiesByPhoneNumber(String phoneNumber, String countryCode);

    /**
     * Finds and returns identity based on provided phone number. If you don't specify country then number has to be in international format (leading +countrycode is required)
     *
     * @param phoneNumber phone number
     * @param countryCode ISO 3166 Alpha-2 code
     * @return List of identities (with only basic attributes without related data represented by collections)
     */
    List<IIdentityBase> findIdentitiesBaseByPhoneNumber(String phoneNumber, String countryCode);

    /**
     * @param documentNumber
     * @return identities with the provided document (ID, passport, ... ) number. Empty list if nothing is found, never null.
     */
    List<IIdentity> findIdentitiesByDocumentNumber(String documentNumber);

    /**
     * @return identities (with only basic attributes without related data represented by collections)
     * with the provided document (ID, passport, ... ) number. Empty list if nothing is found, never null.
     */
    List<IIdentityBase> findIdentitiesBaseByDocumentNumber(String documentNumber);

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
     * Add note to identity.
     * @param identityPublicId Public ID of identity.
     * @param note             Text of note.
     * @return Return true if note has been set to the identity. Otherwise, return false.
     */
    default boolean addNoteToIdentity(String identityPublicId, String note) {
        return false;
    }

    /**
     * Update an existing personal info identity piece.
     *
     * <p>This method can only be used to update <b>Personal Info</b> identity pieces.
     *
     * @param identityPublicId public ID of an existing identity to be updated
     * @param identityPiece identity piece to be updated
     * @return true in case of success, false otherwise
     */
    boolean updateIdentityPiecePersonalInfo(String identityPublicId, IIdentityPiece identityPiece);

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
     * Updates the marketing opt-in agreement for the identity identified by {@code identityId}).
     *
     * @param identityId              public ID of an existing identity to be updated
     * @param agreeWithMarketingOptIn True if the customer agrees to marketing opt-in, false otherwise.
     */
    void updateIdentityMarketingOptIn(String identityId, boolean agreeWithMarketingOptIn);

    /**
     * @param customFieldDefinitionId use {@link CustomFieldDefinition#getId()} of a custom field to set
     */
    void setIdentityCustomField(String identityPublicId,
                                long customFieldDefinitionId,
                                CustomFieldValue customFieldValue) throws RuntimeException;

    /**
     * @param customFieldDefinitionId use {@link CustomFieldDefinition#getId()} of a custom field to set
     */
    void setLocationCustomField(String locationPublicId,
                                long customFieldDefinitionId,
                                CustomFieldValue customFieldValue) throws RuntimeException;

    Collection<CustomField> getIdentityCustomFields(String identityPublicId) throws RuntimeException;

    Collection<CustomField> getLocationCustomFields(String locationPublicId) throws RuntimeException;

    Collection<CustomFieldDefinition> getCustomFieldDefinitions(String organizationId, CustomFieldDefinitionAvailability availability) throws RuntimeException;

    /**
     *
     * @return a tunnel manager that allows creating ssh tunnels on a remote ssh server.
     * Used for creating encrypted tunnels for wallets on remote hosts.
     * See {@link ITunnelManager#connectIfNeeded(String, String, InetSocketAddress)}
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
     * @throws SellException
     */
    ITransactionSellInfo sellCrypto(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, BigDecimal cryptoAmount, String cryptoCurrency, String identityPublicId, String discountCode) throws SellException;

    /**
     * Call this transaction to create a sell transaction. After this call server will await crypto transaction to arrive and allocate cash for the customer.
     * @param fiatAmount
     * @param fiatCurrency
     * @param cryptoAmount - ignored but must be filled - reserved for future.
     * @param cryptoCurrency
     * @param identityPublicId
     * @param discountCode
     * @param phoneNumber The phone number used at the sell transaction. (optional)
     * @return - read ITransactionSellInfo.getTransactionUUID() to find out what should be filled in sell QR code.
     * @throws SellException
     */
    ITransactionSellInfo sellCrypto(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, BigDecimal cryptoAmount, String cryptoCurrency, String identityPublicId, String discountCode, String phoneNumber) throws SellException;

    /**
     * Create a buy transaction (sends crypto to the provided destination address)
     * @param terminalSerialNumber
     * @param fiatAmount
     * @param fiatCurrency
     * @param cryptoAmount
     * @param cryptoCurrency
     * @param destinationAddress
     * @return
     * @throws BuyException
     */
    ITransactionBuyInfo buyCrypto(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, BigDecimal cryptoAmount, String cryptoCurrency, String destinationAddress, String identityPublicId, String discountCode) throws BuyException;
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
     * Gets a wallet instance from terminal's Crypto Settings of the given crypto currency
     * @param terminalSerialNumber
     * @param cryptoCurrency
     * @return
     */
    IWallet findBuyWallet(String terminalSerialNumber, String cryptoCurrency);

    /**
     * @param applicantId as returned in {@link com.generalbytes.batm.server.extensions.aml.verification.CreateApplicantResponse}.
     * @return a provider instance based on parameters configured in Organization of the Applicant.
     */
    IIdentityVerificationProvider findIdentityVerificationProviderByApplicantId(String applicantId);

    /**
     * @param organizationId as in {@link IOrganization#getId()}.
     * @return a provider instance based on parameters configured in the Organization.
     */
    IIdentityVerificationProvider findIdentityVerificationProviderByOrganizationId(long organizationId);

    /**
     * @return the identity applicant identified by the provided applicant ID.
     */
    IdentityApplicant findIdentityVerificationApplicant(String applicantId);

    /**
     * Saves the verification result to the DB,
     * updates Identity state based on the verification result,
     * sends an SMS to the identity to inform them about the verification result.
     *
     * @param rawPayload raw data received from the identity verification provider (e.g. in a webhook). Might be used
     *                   by a different extension to access additional data not recognized by the identity verification extension.
     * @param result data parsed by the identity verification extension
     */
    void processIdentityVerificationResult(String rawPayload, ApplicantCheckResult result);

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
     * Triggers a watch list scan for given identity public ID.
     * Also updates identity's watchListBanned and watchListLastScanAt fields.
     * @param identityPublicId public ID of an existing identity
     * @return true if identity is banned on any of watch lists
     */
    boolean watchListScan(String identityPublicId);

    /**
     * Queries the phone number for its type and if it is blocked based on the settings on terminal organization.
     * Same functionality as "Use GB API For Blocking Not Registered Identities By Certain Line Type". Charged Service.
     * @param phoneNumber phone number to be queried. If missing, the country prefix is determined by the terminal location.
     * @param terminalSerialNumber terminal country and owner (Organization) are used while querying the phone number
     * @return
     */
    PhoneNumberQueryResult queryPhoneNumber(String phoneNumber, String terminalSerialNumber);

    /**
     * Returns Cash Collections ordered by sequence ID (primary key). This variant uses TERMINAL time.
     * @param terminalSerialNumber
     * @param terminalTimeFrom
     * @param terminalTimeTo
     * @return
     */
    List<ITerminalCashCollectionRecord> getCashCollections(String terminalSerialNumber, Date terminalTimeFrom, Date terminalTimeTo);

    /**
     * Gets cash collection records. This variant uses SERVER time
     * @param terminalSerialNumber get collection records of the terminal defined by this serial number
     * @param serverTimeFrom limit the result only to collection records newer than given SERVER time. Optional
     * @param serverTimeTo limit the result only to collection records older than given SERVER time. Optional
     * @param publicIdFrom limit the result only to collection records newer than the one with this public ID. Optional
     * @return list of csah collection records for the given terminal
     */
    List<ITerminalCashCollectionRecord> getCashCollections(String terminalSerialNumber, Date serverTimeFrom, Date serverTimeTo, String publicIdFrom);

    /**
     * Returns Event Logs ordered by ordered by sequence ID (primary key).
     * @param terminalSerialNumber
     * @param dateFrom
     * @param dateTo
     * @return
     */
    List<IEventRecord> getEvents(String terminalSerialNumber, Date dateFrom, Date dateTo);

    /**
     * Returns remaining limits for an identity.
     *
     * @param fiatCurrency         currency of the limit amounts
     * @param terminalSerialNumber serial number for obtaining AML/KYC settings
     * @param identityPublicId     public ID of the identity
     * @return Remaining limits for the identity.
     */
    List<ILimitExtended> getIdentityRemainingLimits(String fiatCurrency, String terminalSerialNumber, String identityPublicId);

    /**
     * Return initial limits for the identity.
     * This is not affected by transactions already performed by the identity
     * (see {@link #getIdentityRemainingLimits(String, String, String)} instead)
     * nor by VIP limits set to the identity
     * (see {@link IIdentity#getLimitCashPerDay()} etc. methods instead).
     *
     * @param fiatCurrency         currency of the limit amounts
     * @param terminalSerialNumber serial number for obtaining AML/KYC settings
     * @param identityPublicId     public ID of the identity
     * @return Initial (total) limits for the identity.
     */
    List<ILimitExtended> getIdentityInitialLimits(String fiatCurrency, String terminalSerialNumber, String identityPublicId);

    /**
     * Authenticate API key
     * @param apiKey apiKey to search for
     * @param apiAccessType - Enum code of the ThirdParty extension type e.g. morphis, osw, everytrade - watch enum ThirdPartyType.java
     * @return null if token is not found or is not valid
     */
    IApiAccess getAPIAccessByKey(String apiKey, ApiAccessType apiAccessType);

    /**
     * Creates Discount.
     *
     * @param organization Organization for which the Discount to be created.
     * @param discountSpec Discount specification.
     *
     * @return Newly created Discount details.
     */
    IDiscount createDiscount(IOrganization organization, DiscountSpec discountSpec);

    /**
     * Returns List of Organizations.
     *
     * @return List of Organizations.
     */
    List<IOrganization> getOrganizations();

    IOrganization getOrganization(String gbApiKey);

    /**
     * Triggers Surveillance Photo Capture aka Collect Photo on specified Terminals.
     *
     * @param terminalSerialNumbers Terminal serial numbers.
     * @return True only if collectPhoto was successfully requested on all of the specified terminals.
     */
    boolean triggerCollectPhoto(List<String> terminalSerialNumbers);

    /**
     * Returns detail of Location.
     * @param locationPublicId - public id of queried Location
     * @return detail of location
     */
    ILocationDetail getLocationByPublicId(String locationPublicId);

    /**
     * Finds a location by its external ID
     *
     * @param externalLocationId External Location ID
     * @return the location identified by its External ID
     */
    ILocationDetail getLocationByExternalId(String externalLocationId, IOrganization organization);

    /**
     * Updates Location.
     * @param publicId - public id of updated Location
     * @param location - new state of location to be set
     * @return updated location
     */
    ILocation updateLocationById(String publicId, ILocationDetail location);

    /**
     * Creates new Location.
     * @param location - Location to be create
     * @return created location
     */
    ILocation addLocation(ILocationDetail location);

    /**
     * Starts identity verification.
     * @param publicIdentityId public ID of an existing identity to be verified
     * @param messageToCustomer - The message that will be displayed to the customer via SMS.
     *                          Message may contain '{link}' placeholder to be replaced by link to verification provider.
     *                          If this argument is not given, the default text will be used.
     *
     * @return info about result of action
     */
    IVerificationInfo startVerificationByIdentityId(String publicIdentityId, String messageToCustomer);

    /**
     * Reads the given file from the server config directory,
     * parses the file as java Properties file format ({@code <key>=<value>} on each line)
     * and searches for the property with the specified key in the file.
     * <p>
     * The {@code defaultValue} argument is returned if the property is not found in the file,
     * if the file does not exist or if reading the file failed.
     * <p>
     * The returned value is cached for a few seconds.
     * <p>
     * The config directory is typically "/batm/config" so for example
     * when the {@code fileNameInConfigDirectory} parameter is "sms",
     * the {@code key} is "country_prefix" and the "/batm/config/sms" file contains a line
     * "country_prefix=+420", the value "+420" will be returned.
     * <p>
     * The extension author calling this method must make sure not to expose any sensitive information
     * from the server config directory, e.g. do not use any user provided values as the filename parameter etc.
     */
    String getConfigProperty(String fileNameInConfigDirectory, String key, String defaultValue);

    /**
     * @return true if a file of the provided name exist in the server config directory.
     * <p>
     * The config directory is typically "/batm/config" so for example
     * when the {@code fileNameInConfigDirectory} parameter is "extension.debug",
     * this method will check if a file called "/batm/config/extension.debug" exist.
     */
    boolean configFileExists(String fileNameInConfigDirectory);

    /**
     * @return contents of the given file in the server config directory.
     * The config directory is typically "/batm/config" so for example
     * when the {@code fileNameInConfigDirectory} parameter is "version"
     * the return value will be the content of "/batm/config/version" file.
     * <p>
     * The returned value is cached for a few seconds.
     * <p>
     * If the file does not exist or any other error occurs
     * while trying to read the file empty string is returned.
     * <p>
     * The extension author calling this method must make sure not to expose any sensitive information
     * from the server config directory, e.g. do not use any user provided values as the filename parameter etc.
     */
    String getConfigFileContent(final String fileNameInConfigDirectory);

    /**
     * Load key store from config directory.
     *
     * @param keyStoreType Type of key store, for example: "PKCS12".
     * @param filePath     File path relative to the config directory (typically "/batm/config"). The file must contain an extension.
     * @param passphrase   Passphrase for access to key store. Can be {code null} if passphrase is not used.
     * @return Key store object or {@code null} if error was occurred.
     */
    default KeyStore loadKeyStoreFromConfigDirectory(String keyStoreType, Path filePath, String passphrase) {
        return null;
    }

    /**
     * Load X.509 certificate from config directory.
     *
     * @param filePath File path relative to the config directory (typically "/batm/config"). The file must contain an extension.
     * @return X.509 certificate or {@code null} if error was occurred.
     */
    default X509Certificate loadX509CertificateFromConfigDirectory(Path filePath) {
        return null;
    }

    /**
     * @return true if the extension is running on global server.
     * Custom extensions will typically run on a standalone server, not global.
     */
    boolean isGlobalServer();

    /**
     * Marks transaction as withdrawn by given remote or local transaction id.
     * @param remoteOrLocalTransactionId
     */
    void markTransactionAsWithdrawn(String remoteOrLocalTransactionId);

    /**
     * Allows to manage the unlock time of a specific transaction.
     * This can be used to unlock the transaction sooner or prolong it.
     * Transactions that are locked will remain in the output queue and won't be flushed until they are unlocked.
     *
     * @param rid                Remote transaction ID
     * @param serverTimeToUnlock The absolute datetime when the transaction should unlock.
     */
    void unlockTransaction(String rid, Date serverTimeToUnlock);

    /**
     * Returns the list of custom strings.
     *
     * @param serialNumber     Serial number of terminal or GB Safe.
     * @param customStringName Name of custom string. If null, returns all custom strings of selected terminal or GB Safe.
     * @return Values of the selected custom string in all available languages from the selected terminal or GB Safe.
     *         Returns an empty list if no custom string is found.
     */
    default List<ICustomString> getCustomStrings(String serialNumber, String customStringName) {
        return new ArrayList<>();
    }

    /**
     * Retrieves the data required to generate a receipt, including placeholder variables
     * and any additional elements needed based on the specified template and transaction details.
     *
     * @param receiptTransferMethod The method used to send the receipt,
     *                              determining the format and additional data required.
     * @param transactionDetails    The details of the transaction for which the receipt
     *                              is being generated, providing values for relevant placeholders.
     * @param template              The template used for generating the receipt,
     *                              which defines the structure and required placeholders.
     * @return A {@link ReceiptData} instance containing all necessary data and placeholders
     * for constructing the receipt according to the specified template.
     */
    ReceiptData getReceiptData(ReceiptTransferMethod receiptTransferMethod, ITransactionDetails transactionDetails, String template);

    /**
     * Returns the list of all configured Travel Rule Providers.
     *
     * @return List of all configured Travel Rule Providers.
     */
    default List<ITravelRuleProviderIdentification> getTravelRuleProviders() { return new ArrayList<>(); }

    /**
     * Returns the list of all VASPs of given Travel Rule Provider.
     *
     * @param travelRuleProviderId ID of Travel Rule Provider.
     * @return List of all VASPs.
     */
    default List<IVaspIdentification> getVasps(long travelRuleProviderId) { return new ArrayList<>(); }

    /**
     * Adds a blockchain transaction hash to the transaction identified by the given remote transaction ID.
     *
     * <p>A single business-level transaction may correspond to multiple underlying blockchain transactions.
     * This method allows additional hashes to be associated with the same transaction record.</p>
     *
     * @param transactionRemoteId the remote transaction ID identifying the transaction to update
     * @param transactionHash     the blockchain transaction hash to add
     * @return the updated transaction details after the hash is added
     * @throws IllegalArgumentException if the remote id or hash is null or blank
     * @throws UpdateException          if the transaction does not exist, or the update fails
     */
    ITransactionDetails addTransactionHash(String transactionRemoteId, String transactionHash) throws UpdateException;

    /**
     * Finds and returns travel rule transfer data associated with the given address.
     *
     * @param address the address to search for associated travel rule transfer data
     * @return the travel rule transfer data associated with the given address, or {@code null} if no data is found
     */
    default ITravelRuleTransferData findTravelRuleTransferByAddress(String address) {
        return null;
    }

    /**
     * Converts the given crypto amount from standard units to base units.
     * For example: 1 BTC (standard units) = 100 000 000 sats (base units)
     *
     * @param amount         Amount in standard units to convert.
     * @param cryptocurrency Cryptocurrency code.
     * @return Amount in base units.
     * @throws IllegalArgumentException for unsupported cryptocurrencies.
     */
    default long convertCryptoToBaseUnit(BigDecimal amount, String cryptocurrency) {
        return 0;
    }

    /**
     * Converts the given crypto amount from base units to standard units.
     * For example: 100 000 000 sats (base units) = 1 BTC (standard units)
     *
     * @param amount         Amount in base units to convert.
     * @param cryptocurrency Cryptocurrency code.
     * @return Amount in standard unit.
     * @throws IllegalArgumentException for unsupported cryptocurrencies.
     */
    default BigDecimal convertCryptoFromBaseUnit(long amount, String cryptocurrency) {
        return BigDecimal.ZERO;
    }
}
