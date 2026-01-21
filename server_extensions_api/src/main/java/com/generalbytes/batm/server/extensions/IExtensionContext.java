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
import com.generalbytes.batm.server.extensions.exceptions.ExternalPaymentProcessingException;
import com.generalbytes.batm.server.extensions.exceptions.ExternalPaymentNotFoundException;
import com.generalbytes.batm.server.extensions.exceptions.SellException;
import com.generalbytes.batm.server.extensions.exceptions.UpdateException;
import com.generalbytes.batm.server.extensions.payment.external.ExternalPaymentUpdate;
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
import java.util.Collections;
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
     *
     * @param listener {@link ITransactionListener} that will receive transaction events (must not be null)
     */
    void addTransactionListener(ITransactionListener listener);

    /**
     * Stops listening for transaction events
     *
     * @param listener {@link ITransactionListener} that will stop receiving transaction events (must not be null)
     * @return true if the listener was removed successfully, false otherwise
     */
    boolean removeTransactionListener(ITransactionListener listener);

    /**
     * Registers a listener for receiving notification
     *
     * @param listener {@link INotificationListener} that will receive notification (must not be null)
     */
    void addNotificationListener(INotificationListener listener);

    /**
     * Stops a listener from receiving notification
     *
     * @param listener {@link INotificationListener} that will stop receiving notification (must not be null)
     */
    void removeNotificationListener(INotificationListener listener);

    /**
     * Register listener for terminal events
     *
     * @param listener {@link ITerminalListener} that will receive terminal events (must not be null)
     */
    void addTerminalListener(ITerminalListener listener);

    /**
     * Stops a listener from receiving terminal events
     * @param listener {@link ITerminalListener} that will stop receiving terminal events (must not be null)
     */
    void removeTerminalListener(ITerminalListener listener);

    /**
     * Register listener for identity events.
     *
     * @param listener {@link IIdentityListener} that will receive identity events (must not be null)
     */
    void addIdentityListener(IIdentityListener listener);

    /**
     * Stops a listener from receiving identity events.
     * @param listener {@link IIdentityListener} that will stop receiving identity events (must not be null)
     */
    void removeIdentityListener(IIdentityListener listener);

    /**
     * Registers a listener for receiving remote printer receipt data asynchronously.
     * No response is expected; the listener should not block.
     *
     * @param listener the printer listener to register (must not be null)
     */
    default void addRemotePrinterReceiptListener(IRemotePrinterListener listener) {
    }

    /**
     * Stops the remote printer listener from receiving receipt data.
     *
     * @param listener the printer listener to remove (must not be null)
     */
    default void removeRemotePrinterReceiptListener(IRemotePrinterListener listener) {
    }

    /**
     * Finds and returns transaction by given remote or local transaction id.
     *
     * @param remoteOrLocalTransactionId remote or local transaction ID (must not be null)
     * @return transaction details, or {@code null} if no transaction is found with the given ID
     */
    ITransactionDetails findTransactionByTransactionId(String remoteOrLocalTransactionId);

    /**
     * Finds and returns transaction by transaction UUID and type.
     *
     * @param uuid transaction UUID (must not be null)
     * @param type transaction type
     * @return transaction details, or {@code null} if no transaction is found with the given UUID and type
     */
    ITransactionDetails findTransactionByTransactionUUIDAndType(String uuid, int type);

    /**
     * Finds and returns transactions performed by identity.
     *
     * @param publicIdentityId public identity ID (must not be null)
     * @return list of transaction details for the given identity. Returns an empty list if no transactions are found,
     * never {@code null}. Returns an empty list if the identity does not exist.
     */
    List<ITransactionDetails> findAllTransactionsByIdentityId(String publicIdentityId);

    /**
     * Finds transactions for a specific terminal within a time range.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param serverTimeFrom       limit returned transactions by server time of the transaction (can be null to indicate no lower bound)
     * @param serverTimeTo         limit returned transactions by server time of the transaction (can be null to indicate no upper bound)
     * @param previousRID          if not null, only transactions NEWER than this one are returned (can be null)
     * @param includeBanknotes     adds banknote information to the result (performs extra db queries)
     * @return list of transaction details. Returns an empty list if no transactions match the criteria, never {@code null}
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    List<ITransactionDetails> findTransactions(String terminalSerialNumber, Date serverTimeFrom, Date serverTimeTo, String previousRID, boolean includeBanknotes);

    /**
     * Updates a transaction with new status, detail, and/or tags.
     *
     * @param rid    remote transaction ID of the transaction to be updated (must not be null)
     * @param status new status to be set, or {@code null} to keep it unmodified
     * @param detail detail message to be appended if there already is a detail set, or {@code null} to keep it unmodified
     * @param tags   tags to be set to the transaction (can be null).
     *               <ul>
     *               <li>If the transaction has some tags assigned already, those will be removed (un-assigned)
     *               and only the tags provided here would be set.</li>
     *               <li>Providing an empty set will un-assign all existing tags of the transaction.</li>
     *               <li>{@code null} keeps the existing tags unchanged.</li>
     *               <li>Tags that are being set must exist (see {@link #getTransactionTags(String)}), non-existent tags are ignored.</li>
     *               </ul>
     * @return modified transaction details (never null)
     * @throws UpdateException if the transaction does not exist or the update was not successful
     */
    ITransactionDetails updateTransaction(String rid, Integer status, String detail, Set<String> tags) throws UpdateException;

    /**
     * Updates a transaction with new status, detail, and/or custom data.
     *
     * @param rid        remote transaction ID of the transaction to be updated (must not be null)
     * @param status     new status to be set, or {@code null} to keep it unmodified
     * @param detail     detail message to be appended if there already is a detail set, or {@code null} to keep it unmodified
     * @param customData custom data to be set to the transaction (can be null).
     *                   <ul>
     *                   <li>This will replace existing custom data stored for the transaction.</li>
     *                   <li>If you need to keep existing data, obtain them first using {@link ITransactionDetails#getCustomData()}.</li>
     *                   <li>Providing an empty map will remove all existing custom data.</li>
     *                   <li>{@code null} keeps the existing custom data unchanged.</li>
     *                   </ul>
     * @return modified transaction details (never null)
     * @throws UpdateException if the transaction does not exist or the update was not successful
     */
    ITransactionDetails updateTransaction(String rid, Integer status, String detail, Map<String, String> customData) throws UpdateException;

    /**
     * Updates a transaction with new status and/or detail.
     *
     * @param rid    remote transaction ID of the transaction to be updated (must not be null)
     * @param status new status to be set, or {@code null} to keep it unmodified
     * @param detail detail message to be appended if there already is a detail set, or {@code null} to keep it unmodified
     * @return modified transaction details (never null)
     * @throws UpdateException if the transaction does not exist or the update was not successful
     */
    ITransactionDetails updateTransaction(String rid, Integer status, String detail) throws UpdateException;

    /**
     * Returns names of organization tags that could be assigned to transactions.
     *
     * @param organizationId organization ID (must not be null)
     * @return set of tag names that could be assigned to transactions. Returns an empty set if no tags are configured,
     * never {@code null}
     */
    Set<String> getTransactionTags(String organizationId);

    /**
     * Finds person by chat user ID.
     *
     * @param chatUserId chat user ID (must not be null)
     * @return person details, or {@code null} if no person is found with the given chat user ID
     */
    IPerson findPersonByChatId(String chatUserId);

    /**
     * Checks if person has access to provided object.
     * For example, person needs to be in same organization or needs to have at least read-only role.
     *
     * @param permissionLevel permission level (use constants {@link #PERMISSION_NONE}, {@link #PERMISSION_READ}, {@link #PERMISSION_WRITE}, {@link #PERMISSION_EXECUTE})
     * @param person          person to check (must not be null)
     * @param obj             object to check access for (must not be null)
     * @return true if person has the required permission level to the object, false otherwise
     */
    boolean hasPersonPermissionToObject(int permissionLevel, IPerson person, Object obj);

    /**
     * Checks whether the terminal belongs to same organization as person.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param person               person to check (must not be null)
     * @return true if terminal belongs to the same organization as the person, false otherwise
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    boolean isTerminalFromSameOrganizationAsPerson(String terminalSerialNumber, IPerson person);

    /**
     * Finds and returns identity based on provided public identity ID.
     *
     * @param publicIdentityId public identity ID (must not be null)
     * @return identity details, or {@code null} if no identity is found with the given ID
     */
    IIdentity findIdentityByIdentityId(String publicIdentityId);

    /**
     * Finds and returns identity with only basic attributes (without related data represented by collections)
     * based on provided public identity ID.
     *
     * @param publicIdentityId public identity ID (must not be null)
     * @return identity with only basic attributes without related data represented by collections,
     * or {@code null} if no identity is found with the given ID
     */
    IIdentityBase findIdentityBaseByIdentityId(String publicIdentityId);

    /**
     * Finds and returns identities based on provided phone number.
     * The phone number must be in international format (leading +countrycode is required).
     *
     * @param phoneNumber phone number in international format, e.g., "+420123456789" (must not be null)
     * @return list of identities matching the phone number. Returns an empty list if no identities are found,
     * never {@code null}
     */
    List<IIdentity> findIdentitiesByPhoneNumber(String phoneNumber);

    /**
     * Finds and returns identities with only basic attributes (without related data represented by collections)
     * based on provided phone number.
     * The phone number must be in international format (leading +countrycode is required).
     *
     * @param phoneNumber phone number in international format, e.g., "+420123456789" (must not be null)
     * @return list of identities (with only basic attributes without related data represented by collections).
     * Returns an empty list if no identities are found, never {@code null}
     */
    List<IIdentityBase> findIdentitiesBaseByPhoneNumber(String phoneNumber);

    /**
     * Finds and returns all identities of given state.
     *
     * @param state identity state (see constants in {@link IIdentityBase}, e.g., {@link IIdentityBase#STATE_REGISTERED})
     * @return list of identities with the given state. Returns an empty list if no identities match,
     * never {@code null}
     */
    List<IIdentity> findAllIdentitiesByState(int state);

    /**
     * Finds and returns all identities of given state with only basic attributes.
     *
     * @param state identity state (see constants in {@link IIdentityBase}, e.g., {@link IIdentityBase#STATE_REGISTERED})
     * @return list of identities (with only basic attributes without related data represented by collections)
     * with the given state. Returns an empty list if no identities match, never {@code null}
     */
    List<IIdentityBase> findAllIdentitiesBaseByState(int state);

    /**
     * Finds and returns identities based on provided phone number.
     * If country is not specified, the number must be in international format (leading +countrycode is required).
     *
     * @param phoneNumber phone number (must not be null). If countryName is null, must be in international format
     * @param countryName country name (can be null). If null, phoneNumber must be in international format
     * @return list of identities matching the phone number. Returns an empty list if no identities are found,
     * never {@code null}
     */
    List<IIdentity> findIdentityByPhoneNumber(String phoneNumber, String countryName);

    /**
     * Finds and returns identities based on provided phone number.
     * If country code is not specified, the number must be in international format (leading +countrycode is required).
     *
     * @param phoneNumber phone number (must not be null). If countryCode is null, must be in international format
     * @param countryCode ISO 3166 Alpha-2 code (can be null). If null, phoneNumber must be in international format
     * @return list of identities matching the phone number. Returns an empty list if no identities are found,
     * never {@code null}
     */
    List<IIdentity> findIdentitiesByPhoneNumber(String phoneNumber, String countryCode);

    /**
     * Finds and returns identities with only basic attributes based on provided phone number.
     * If country code is not specified, the number must be in international format (leading +countrycode is required).
     *
     * @param phoneNumber phone number (must not be null). If countryCode is null, must be in international format
     * @param countryCode ISO 3166 Alpha-2 code (can be null). If null, phoneNumber must be in international format
     * @return list of identities (with only basic attributes without related data represented by collections).
     * Returns an empty list if no identities are found, never {@code null}
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
     * Finds and returns identities that match all provided field values in {@link IdentityFilter}.
     *
     * @param filter {@link IdentityFilter} with values to search for. All non-null fields are considered (AND logic).
     * @return Matching identities. An empty list if nothing is found.
     */
    default List<IIdentity> findIdentitiesByFilter(IdentityFilter filter) {
        return Collections.emptyList();
    }

    /**
     * Creates a new identity.
     *
     * @param configurationCashCurrency currency code for which limits are used (must not be null)
     * @param terminalSerialNumber      terminal serial number (must not be null; must be a valid serial number format,
     *                                  otherwise {@link IllegalArgumentException} is thrown)
     * @param externalId                external ID for the identity (can be null)
     * @param limitCashPerTransaction   transaction limits (can be null; empty list means no limits)
     * @param limitCashPerHour          hourly limits (can be null; empty list means no limits)
     * @param limitCashPerDay           daily limits (can be null; empty list means no limits)
     * @param limitCashPerWeek          weekly limits (can be null; empty list means no limits)
     * @param limitCashPerMonth         monthly limits (can be null; empty list means no limits)
     * @param note                      note for the identity (can be null)
     * @param state                     identity state (see constants in {@link IIdentity}, e.g., {@link IIdentity#STATE_REGISTERED})
     * @param vipBuyDiscount            buy fee discount in percent (can be null)
     * @param vipSellDiscount           sell fee discount in percent (can be null)
     * @param created                   creation date (can be null; if null, current date is used)
     * @param registered                registration date (can be null)
     * @return identity with generated ID (identityPublicId), never null
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    IIdentity addIdentity(String configurationCashCurrency, String terminalSerialNumber, String externalId, List<ILimit> limitCashPerTransaction, List<ILimit> limitCashPerHour, List<ILimit> limitCashPerDay, List<ILimit> limitCashPerWeek, List<ILimit> limitCashPerMonth, String note, int state, BigDecimal vipBuyDiscount, BigDecimal vipSellDiscount, Date created, Date registered);

    /**
     * Creates a new identity with language preference.
     *
     * @param configurationCashCurrency currency code for which limits are used (must not be null)
     * @param terminalSerialNumber      terminal serial number (must not be null; must be a valid serial number format,
     *                                  otherwise {@link IllegalArgumentException} is thrown)
     * @param externalId                external ID for the identity (can be null)
     * @param limitCashPerTransaction   transaction limits (can be null; empty list means no limits)
     * @param limitCashPerHour          hourly limits (can be null; empty list means no limits)
     * @param limitCashPerDay           daily limits (can be null; empty list means no limits)
     * @param limitCashPerWeek          weekly limits (can be null; empty list means no limits)
     * @param limitCashPerMonth         monthly limits (can be null; empty list means no limits)
     * @param note                      note for the identity (can be null)
     * @param state                     identity state (see constants in {@link IIdentity}, e.g., {@link IIdentity#STATE_REGISTERED})
     * @param vipBuyDiscount            buy fee discount in percent (can be null)
     * @param vipSellDiscount           sell fee discount in percent (can be null)
     * @param created                   creation date (can be null; if null, current date is used)
     * @param registered                registration date (can be null)
     * @param language                  language preference (can be null)
     * @return identity with generated ID (identityPublicId), never null
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    IIdentity addIdentity(String configurationCashCurrency, String terminalSerialNumber, String externalId, List<ILimit> limitCashPerTransaction, List<ILimit> limitCashPerHour, List<ILimit> limitCashPerDay, List<ILimit> limitCashPerWeek, List<ILimit> limitCashPerMonth, String note, int state, BigDecimal vipBuyDiscount, BigDecimal vipSellDiscount, Date created, Date registered, String language);

    /**
     * Adds an identity piece to an identity specified by identity public ID.
     *
     * @param identityPublicId public ID of the identity (must not be null)
     * @param iidentityPiece   identity piece to add (must not be null)
     * @return true if the identity piece was successfully added, false otherwise
     */
    boolean addIdentityPiece(String identityPublicId, IIdentityPiece iidentityPiece);

    /**
     * Adds a note to an identity.
     *
     * @param identityPublicId public ID of the identity (must not be null)
     * @param note             text of the note (must not be null)
     * @return true if the note has been successfully added to the identity, false otherwise
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
     * @param identityPiece    identity piece to be updated
     * @return true in case of success, false otherwise
     */
    boolean updateIdentityPiecePersonalInfo(String identityPublicId, IIdentityPiece identityPiece);

    /**
     * Updates an existing identity.
     *
     * @param identityId                  public ID of an existing identity to be updated (must not be null)
     * @param externalId                  external ID (can be null)
     * @param state                       new state to be set (see constants in {@link IIdentity})
     * @param type                        identity type
     * @param created                     creation date (can be null)
     * @param registered                  registration date (can be null)
     * @param vipBuyDiscount              buy fee discount in percent (can be null)
     * @param vipSellDiscount             sell fee discount in percent (can be null)
     * @param note                        new note to be set (can be null)
     * @param limitCashPerTransaction     transaction limits (can be null; empty list means no limits)
     * @param limitCashPerHour            hourly limits (can be null; empty list means no limits)
     * @param limitCashPerDay             daily limits (can be null; empty list means no limits)
     * @param limitCashPerWeek            weekly limits (can be null; empty list means no limits)
     * @param limitCashPerMonth           monthly limits (can be null; empty list means no limits)
     * @param limitCashPer3Months         3-month limits (can be null; empty list means no limits)
     * @param limitCashPer12Months        12-month limits (can be null; empty list means no limits)
     * @param limitCashPerCalendarQuarter calendar quarter limits (can be null; empty list means no limits)
     * @param limitCashPerCalendarYear    calendar year limits (can be null; empty list means no limits)
     * @param limitCashTotalIdentity      total identity limits (can be null; empty list means no limits)
     * @param configurationCashCurrency   currency code for which limits are used (can be null)
     * @return updated identity, never null
     */
    IIdentity updateIdentity(String identityId, String externalId, int state, int type, Date created, Date registered,
                             BigDecimal vipBuyDiscount, BigDecimal vipSellDiscount, String note,
                             List<ILimit> limitCashPerTransaction, List<ILimit> limitCashPerHour, List<ILimit> limitCashPerDay, List<ILimit> limitCashPerWeek,
                             List<ILimit> limitCashPerMonth, List<ILimit> limitCashPer3Months, List<ILimit> limitCashPer12Months, List<ILimit> limitCashPerCalendarQuarter,
                             List<ILimit> limitCashPerCalendarYear, List<ILimit> limitCashTotalIdentity, String configurationCashCurrency);

    /**
     * Updates the marketing opt-in agreement for the identity identified by {@code identityId}.
     *
     * @param identityId              public ID of an existing identity to be updated (must not be null)
     * @param agreeWithMarketingOptIn true if the customer agrees to marketing opt-in, false otherwise
     */
    void updateIdentityMarketingOptIn(String identityId, boolean agreeWithMarketingOptIn);

    /**
     * Sets a custom field value for an identity.
     *
     * @param identityPublicId        public ID of the identity (must not be null)
     * @param customFieldDefinitionId use {@link CustomFieldDefinition#getId()} of a custom field to set
     * @param customFieldValue        value to set (can be null to clear the field)
     * @throws RuntimeException if the identity or custom field definition is not found, or if the operation fails
     */
    void setIdentityCustomField(String identityPublicId,
                                long customFieldDefinitionId,
                                CustomFieldValue customFieldValue) throws RuntimeException;

    /**
     * Sets a custom field value for a location.
     *
     * @param locationPublicId        public ID of the location (must not be null)
     * @param customFieldDefinitionId use {@link CustomFieldDefinition#getId()} of a custom field to set
     * @param customFieldValue        value to set (can be null to clear the field)
     * @throws RuntimeException if the location or custom field definition is not found, or if the operation fails
     */
    void setLocationCustomField(String locationPublicId,
                                long customFieldDefinitionId,
                                CustomFieldValue customFieldValue) throws RuntimeException;

    /**
     * Gets all custom field values for an identity.
     *
     * @param identityPublicId public ID of the identity (must not be null)
     * @return collection of custom fields. Returns an empty collection if no custom fields are set, never null
     * @throws RuntimeException if the identity is not found or if the operation fails
     */
    Collection<CustomField> getIdentityCustomFields(String identityPublicId) throws RuntimeException;

    /**
     * Gets all custom field values for a location.
     *
     * @param locationPublicId public ID of the location (must not be null)
     * @return collection of custom fields. Returns an empty collection if no custom fields are set, never null
     * @throws RuntimeException if the location is not found or if the operation fails
     */
    Collection<CustomField> getLocationCustomFields(String locationPublicId) throws RuntimeException;

    /**
     * Gets custom field definitions available for an organization.
     *
     * @param organizationId organization ID (must not be null)
     * @param availability   availability filter (must not be null)
     * @return collection of custom field definitions. Returns an empty collection if no definitions match,
     * never null
     * @throws RuntimeException if the organization is not found or if the operation fails
     */
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
     * Sends plain-text email asynchronously.
     *
     * @param from          sender email address (must not be null)
     * @param addressListTo recipient email addresses, comma-separated (must not be null)
     * @param subject       email subject (must not be null)
     * @param messageText   email message body (must not be null)
     * @param replyTo       reply-to email address (can be null)
     */
    void sendMailAsync(final String from, final String addressListTo, final String subject, final String messageText, String replyTo);

    /**
     * Sends plain-text email containing attachment asynchronously.
     *
     * @param from               sender email address (must not be null)
     * @param addresslistTo      recipient email addresses, comma-separated (must not be null)
     * @param subject            email subject (must not be null)
     * @param messageText        email message body (must not be null)
     * @param attachmentFileName name of the attachment file (must not be null)
     * @param attachmentContent  attachment file content (must not be null)
     * @param attachmentMimeType MIME type of the attachment (must not be null)
     * @param replyTo            reply-to email address (can be null)
     */
    void sendMailAsyncWithAttachment(final String from, final String addresslistTo, final String subject, final String messageText, final String attachmentFileName, final byte[] attachmentContent, final String attachmentMimeType, String replyTo);

    /**
     * Sends email containing HTML text asynchronously.
     *
     * @param from                sender email address (must not be null)
     * @param addresslistTo       recipient email addresses, comma-separated (must not be null)
     * @param subject             email subject (must not be null)
     * @param messageText         HTML email message body (must not be null)
     * @param replyTo             reply-to email address (can be null)
     * @param embeddedEmailImages embedded images for the HTML email (can be null or empty)
     */
    void sendHTMLMailAsync(final String from, final String addresslistTo, final String subject, final String messageText, String replyTo, final EmbeddedEmailImage... embeddedEmailImages);

    /**
     * Sends email containing HTML text and attachments asynchronously.
     *
     * @param from               sender email address (must not be null)
     * @param addresslistTo      recipient email addresses, comma-separated (must not be null)
     * @param subject            email subject (must not be null)
     * @param messageText        HTML email message body (must not be null)
     * @param attachmentFileName name of the attachment file (must not be null)
     * @param attachmentContent  attachment file content (must not be null)
     * @param attachmentMimeType MIME type of the attachment (must not be null)
     * @param replyTo            reply-to email address (can be null)
     */
    void sendHTMLMailAsyncWithAttachment(final String from, final String addresslistTo, final String subject, final String messageText, final String attachmentFileName, final byte[] attachmentContent, final String attachmentMimeType, String replyTo);

    /**
     * Sends SMS message to specified phone number asynchronously.
     * Terminal serial number is used to detect country code prefix from its location.
     *
     * @param terminalSN  terminal serial number (must not be null; must be a valid serial number format,
     *                    otherwise {@link IllegalArgumentException} is thrown)
     * @param phonenumber phone number to send SMS to (must not be null)
     * @param messageText SMS message text (must not be null)
     * @throws IllegalArgumentException if terminalSN is null or invalid
     */
    void sendSMSAsync(final String terminalSN, final String phonenumber, final String messageText);

    /**
     * Adds a task to the server's task manager.
     *
     * @param name     task name (must not be null)
     * @param tt       task to execute (must not be null)
     * @param onFinish callback to execute when task finishes (can be null)
     */
    void addTask(String name, final ITask tt, final Runnable onFinish);

    /**
     * Returns version of the server software. Useful for compatibility checks.
     *
     * @return server version string, never null
     */
    String getServerVersion();

    /**
     * Returns cash amount that is available to be dispensed by sell transactions.
     * Algorithm takes into account also that some of the banknotes are reserved (are excluded from sum)
     * for upcoming withdrawals by other customers.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param fiatCurrency         fiat currency code (must not be null)
     * @return available cash amount, never null (can be zero)
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    BigDecimal calculateCashAvailableForSell(String terminalSerialNumber, String fiatCurrency);


    /**
     * Returns banknotes that are available to be dispensed by sell transactions.
     * Algorithm takes into account also that some of the banknotes are reserved (are excluded from sum)
     * for upcoming withdrawals by other customers.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param fiatCurrency         fiat currency code (must not be null)
     * @return map of banknote denominations to available counts. Returns an empty map if no banknotes are available,
     * never null
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    Map<BigDecimal, Integer> getAvailableBanknotesConsideringFutureWithdrawals(String terminalSerialNumber, String fiatCurrency);


    /**
     * Creates a sell transaction. After this call, the server will await crypto transaction to arrive
     * and allocate cash for the customer.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param fiatAmount           fiat amount to be received (must not be null; must be greater than zero)
     * @param fiatCurrency         fiat currency code (must not be null)
     * @param cryptoAmount         crypto amount (ignored but must be filled - reserved for future; must not be null)
     * @param cryptoCurrency       crypto currency code (must not be null)
     * @param identityPublicId     public ID of the identity (can be null for anonymous transactions)
     * @param discountCode         discount code (can be null)
     * @return sell transaction info. Read {@link ITransactionSellInfo#getTransactionUUID()} to find out
     * what should be filled in sell QR code, never null
     * @throws SellException            if the transaction cannot be created (e.g., insufficient cash, invalid parameters)
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    ITransactionSellInfo sellCrypto(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, BigDecimal cryptoAmount, String cryptoCurrency, String identityPublicId, String discountCode) throws SellException;

    /**
     * Creates a sell transaction with phone number. After this call, the server will await crypto transaction
     * to arrive and allocate cash for the customer.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param fiatAmount           fiat amount to be received (must not be null; must be greater than zero)
     * @param fiatCurrency         fiat currency code (must not be null)
     * @param cryptoAmount         crypto amount (ignored but must be filled - reserved for future; must not be null)
     * @param cryptoCurrency       crypto currency code (must not be null)
     * @param identityPublicId     public ID of the identity (can be null for anonymous transactions)
     * @param discountCode         discount code (can be null)
     * @param phoneNumber          phone number used at the sell transaction (can be null)
     * @return sell transaction info. Read {@link ITransactionSellInfo#getTransactionUUID()} to find out
     * what should be filled in sell QR code, never null
     * @throws SellException            if the transaction cannot be created (e.g., insufficient cash, invalid parameters,
     *                                  phone number blacklisted)
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    ITransactionSellInfo sellCrypto(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, BigDecimal cryptoAmount, String cryptoCurrency, String identityPublicId, String discountCode, String phoneNumber) throws SellException;

    /**
     * Creates a buy transaction (sends crypto to the provided destination address).
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param fiatAmount           fiat amount to be paid (must not be null; must be greater than zero)
     * @param fiatCurrency         fiat currency code (must not be null)
     * @param cryptoAmount         crypto amount to be sent (can be null; if null, calculated from fiat amount)
     * @param cryptoCurrency       crypto currency code (must not be null)
     * @param destinationAddress   destination crypto address (must not be null)
     * @param identityPublicId     public ID of the identity (can be null for anonymous transactions)
     * @param discountCode         discount code (can be null)
     * @return buy transaction info, never null
     * @throws BuyException             if the transaction cannot be created (e.g., invalid parameters, phone number blacklisted)
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    ITransactionBuyInfo buyCrypto(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, BigDecimal cryptoAmount, String cryptoCurrency, String destinationAddress, String identityPublicId, String discountCode) throws BuyException;

    /**
     * Creates a cash back transaction. After this call, the server will allocate cash for the customer
     * that can visit machine and withdraw cash.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param fiatAmount           fiat amount to be allocated (must not be null; must be greater than zero)
     * @param fiatCurrency         fiat currency code (must not be null)
     * @param identityPublicId     public ID of the identity (must not be null)
     * @return cashback transaction info. Read {@link ITransactionCashbackInfo#getTransactionUUID()} to find out
     * what should be filled in QR code, never null
     * @throws CashbackException        if the transaction cannot be created (e.g., insufficient cash, invalid parameters)
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    ITransactionCashbackInfo cashback(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, String identityPublicId) throws CashbackException;


    /**
     * Gets exchange rates of specific terminal and specified directions.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param directions           direction flags (use constants {@link #DIRECTION_BUY_CRYPTO}, {@link #DIRECTION_SELL_CRYPTO}, etc.)
     * @return map of direction flags to lists of exchange rate info. Returns an empty map if no rates are available,
     * never null
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    Map<Integer, List<IExchangeRateInfo>> getExchangeRateInfo(String terminalSerialNumber, int directions);

    /**
     * Calculates crypto amounts for various cryptocurrencies based on one specified fiat amount and currency.
     * Optional discount code and identity ID can influence calculation due to discounts.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param cryptoCurrencies     list of crypto currency codes to calculate amounts for (must not be null; empty list returns empty map)
     * @param cashAmount           fiat amount (must not be null; must be greater than zero)
     * @param cashCurrency         fiat currency code (must not be null)
     * @param direction            transaction direction (use constants {@link #DIRECTION_BUY_CRYPTO}, {@link #DIRECTION_SELL_CRYPTO})
     * @param discountCode         discount code (can be null)
     * @param identityPublicId     public ID of the identity (can be null)
     * @return map of crypto currency codes to amounts with discount info. Returns an empty map if no currencies are provided,
     * never null
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    Map<String, IAmountWithDiscount> calculateCryptoAmounts(String terminalSerialNumber, List<String> cryptoCurrencies, BigDecimal cashAmount, String cashCurrency, int direction, String discountCode, String identityPublicId);

    /**
     * Returns details about cash inside of the machine.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @return list of banknote counts by denomination. Returns an empty list if no cash boxes are configured,
     * never null
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    List<IBanknoteCounts> getCashBoxes(String terminalSerialNumber);


    /**
     * Returns list of all terminals registered on the system.
     *
     * @return list of all terminals. Returns an empty list if no terminals are registered, never null
     */
    List<ITerminal> findAllTerminals();

    /**
     * Returns terminal by serial number.
     *
     * @param serialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                     otherwise {@link IllegalArgumentException} is thrown)
     * @return terminal details, or {@code null} if no terminal is found with the given serial number
     * @throws IllegalArgumentException if serialNumber is null or invalid
     */
    ITerminal findTerminalBySerialNumber(String serialNumber);


    /**
     * Returns list of terminal serial numbers of terminals that have available cash to be dispensed by sell transactions.
     * Algorithm takes into account also if amount can be built by banknotes in output cash boxes and
     * if some of the banknotes are reserved for upcoming withdrawals by other customers.
     *
     * @param fiatAmount                  fiat amount to check availability for (must not be null; must be greater than zero)
     * @param fiatCurrency                fiat currency code (must not be null)
     * @param listOfTerminalSerialNumbers list of terminal serial numbers to check (can be null to check all terminals).
     *                                    If not null, all serial numbers must be valid, otherwise {@link IllegalArgumentException} is thrown
     * @return list of terminal serial numbers that have sufficient cash available. Returns an empty list if no terminals
     * have sufficient cash, never null
     * @throws IllegalArgumentException if fiatAmount is null or non-positive, or if any serial number in the list is invalid
     */
    List<String> findTerminalsWithAvailableCashForSell(BigDecimal fiatAmount, String fiatCurrency, List<String> listOfTerminalSerialNumbers);

    /**
     * Packages and stores a paper wallet in a password-encrypted archive.
     * It also includes privateKey and address in form of QR code into the archive.
     * Such package is safe to later be sent via email as used 7ZIP AES_STRENGTH_256 seems sufficient protection against brute-force.
     *
     * @param privateKey     private key (must not be null)
     * @param address        crypto address (must not be null)
     * @param password       password for encryption (must not be null)
     * @param cryptoCurrency crypto currency code (must not be null)
     * @return encrypted archive as byte array, never null
     */
    byte[] createPaperWallet7ZIP(String privateKey, String address, String password, String cryptoCurrency);

    /**
     * Returns time format by person. In US users they prefer mm/dd/yyyy, anywhere else dd.mm.yyyy.
     *
     * @param person person to get time format for (must not be null)
     * @return time format, never null
     */
    SimpleDateFormat getTimeFormatByPerson(IPerson person);

    /**
     * Gets a wallet instance from terminal's Crypto Settings of the given crypto currency.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param cryptoCurrency       crypto currency code (must not be null)
     * @return wallet instance, or {@code null} if no wallet is configured for the given currency
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    IWallet findBuyWallet(String terminalSerialNumber, String cryptoCurrency);

    /**
     * Finds an identity verification provider by applicant ID.
     *
     * @param applicantId applicant ID as returned in {@link com.generalbytes.batm.server.extensions.aml.verification.CreateApplicantResponse}
     *                    (must not be null)
     * @return provider instance based on parameters configured in Organization of the Applicant,
     * or {@code null} if not found
     */
    IIdentityVerificationProvider findIdentityVerificationProviderByApplicantId(String applicantId);

    /**
     * Finds an identity verification provider by organization ID.
     *
     * @param organizationId organization ID as in {@link IOrganization#getId()} (must not be null)
     * @return provider instance based on parameters configured in the Organization,
     * or {@code null} if not found
     */
    IIdentityVerificationProvider findIdentityVerificationProviderByOrganizationId(long organizationId);

    /**
     * Finds the identity applicant identified by the provided applicant ID.
     *
     * @param applicantId applicant ID (must not be null)
     * @return identity applicant, or {@code null} if not found
     */
    IdentityApplicant findIdentityVerificationApplicant(String applicantId);

    /**
     * Saves the verification result to the DB,
     * updates Identity state based on the verification result,
     * sends an SMS to the identity to inform them about the verification result.
     *
     * @param rawPayload raw data received from the identity verification provider (e.g. in a webhook). Might be used
     *                   by a different extension to access additional data not recognized by the identity verification extension.
     *                   can be null.
     * @param result     data parsed by the identity verification extension (must not be null)
     */
    void processIdentityVerificationResult(String rawPayload, ApplicantCheckResult result);

    /**
     * Returns crypto configurations used by terminals of specified serial numbers.
     *
     * @param serialNumbers list of terminal serial numbers (must not be null; all serial numbers must be valid,
     *                      otherwise {@link IllegalArgumentException} is thrown)
     * @return list of crypto configurations. Returns an empty list if no configurations are found, never null
     * @throws IllegalArgumentException if serialNumbers is null or contains invalid serial numbers
     */
    List<ICryptoConfiguration> findCryptoConfigurationsByTerminalSerialNumbers(List<String> serialNumbers);

    /**
     * Checks presence of person or entity on watch lists.
     *
     * @param query watch list query (must not be null)
     * @return watch list search result, never null
     */
    WatchListResult searchWatchList(WatchListQuery query);

    /**
     * Triggers a watch list scan for given identity public ID.
     * Also updates identity's watchListBanned and watchListLastScanAt fields.
     *
     * @param identityPublicId public ID of an existing identity (must not be null)
     * @return true if identity is banned on any of watch lists, false otherwise
     */
    boolean watchListScan(String identityPublicId);

    /**
     * Queries the phone number for its type and if it is blocked based on the settings on terminal organization.
     * Same functionality as "Use GB API For Blocking Not Registered Identities By Certain Line Type". Charged Service.
     *
     * @param phoneNumber          phone number to be queried (can be null; if null, the country prefix is determined by the terminal location)
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown).
     *                             Terminal country and owner (Organization) are used while querying the phone number
     * @return phone number query result, never null
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    PhoneNumberQueryResult queryPhoneNumber(String phoneNumber, String terminalSerialNumber);

    /**
     * Returns Cash Collections ordered by sequence ID (primary key). This variant uses TERMINAL time.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param terminalTimeFrom     lower bound for terminal time (can be null to indicate no lower bound)
     * @param terminalTimeTo       upper bound for terminal time (can be null to indicate no upper bound)
     * @return list of cash collection records. Returns an empty list if no records match, never null
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    List<ITerminalCashCollectionRecord> getCashCollections(String terminalSerialNumber, Date terminalTimeFrom, Date terminalTimeTo);

    /**
     * Gets cash collection records. This variant uses SERVER time.
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param serverTimeFrom       limit the result only to collection records newer than given SERVER time (can be null)
     * @param serverTimeTo         limit the result only to collection records older than given SERVER time (can be null)
     * @param publicIdFrom         limit the result only to collection records newer than the one with this public ID (can be null)
     * @return list of cash collection records for the given terminal. Returns an empty list if no records match, never null
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    List<ITerminalCashCollectionRecord> getCashCollections(String terminalSerialNumber, Date serverTimeFrom, Date serverTimeTo, String publicIdFrom);

    /**
     * Returns Event Logs ordered by sequence ID (primary key).
     *
     * @param terminalSerialNumber terminal serial number (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param dateFrom             lower bound for event date (can be null to indicate no lower bound)
     * @param dateTo               upper bound for event date (can be null to indicate no upper bound)
     * @return list of event records. Returns an empty list if no events match, never null
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    List<IEventRecord> getEvents(String terminalSerialNumber, Date dateFrom, Date dateTo);

    /**
     * Returns remaining limits for an identity.
     *
     * @param fiatCurrency         currency of the limit amounts (must not be null)
     * @param terminalSerialNumber serial number for obtaining AML/KYC settings (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param identityPublicId     public ID of the identity (must not be null)
     * @return remaining limits for the identity. Returns an empty list if no limits are configured, never null
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    List<ILimitExtended> getIdentityRemainingLimits(String fiatCurrency, String terminalSerialNumber, String identityPublicId);

    /**
     * Returns initial limits for the identity.
     * This is not affected by transactions already performed by the identity
     * (see {@link #getIdentityRemainingLimits(String, String, String)} instead)
     * nor by VIP limits set to the identity
     * (see {@link IIdentity#getLimitCashPerDay()} etc. methods instead).
     *
     * @param fiatCurrency         currency of the limit amounts (must not be null)
     * @param terminalSerialNumber serial number for obtaining AML/KYC settings (must not be null; must be a valid serial number format,
     *                             otherwise {@link IllegalArgumentException} is thrown)
     * @param identityPublicId     public ID of the identity (must not be null)
     * @return initial (total) limits for the identity. Returns an empty list if no limits are configured, never null
     * @throws IllegalArgumentException if terminalSerialNumber is null or invalid
     */
    List<ILimitExtended> getIdentityInitialLimits(String fiatCurrency, String terminalSerialNumber, String identityPublicId);

    /**
     * Authenticates API key.
     *
     * @param apiKey        API key to search for (must not be null)
     * @param apiAccessType enum code of the ThirdParty extension type (e.g., morphis, osw, everytrade - see enum ThirdPartyType.java)
     *                      (must not be null)
     * @return API access details if the key is found and valid, or {@code null} if token is not found or is not valid
     */
    IApiAccess getAPIAccessByKey(String apiKey, ApiAccessType apiAccessType);

    /**
     * Creates a discount.
     *
     * @param organization organization for which the discount is to be created (must not be null)
     * @param discountSpec discount specification (must not be null)
     * @return newly created discount details, or {@code null} if creation failed
     */
    IDiscount createDiscount(IOrganization organization, DiscountSpec discountSpec);

    /**
     * Returns list of all organizations.
     *
     * @return list of organizations. Returns an empty list if no organizations exist, never null
     */
    List<IOrganization> getOrganizations();

    /**
     * Gets an organization by GB API key.
     *
     * @param gbApiKey GB API key (must not be null)
     * @return organization details, or {@code null} if not found
     */
    IOrganization getOrganization(String gbApiKey);

    /**
     * Triggers Surveillance Photo Capture (aka Collect Photo) on specified terminals.
     *
     * @param terminalSerialNumbers list of terminal serial numbers (must not be null; all serial numbers must be valid,
     *                              otherwise {@link IllegalArgumentException} is thrown)
     * @return true only if collectPhoto was successfully requested on all of the specified terminals, false otherwise
     * @throws IllegalArgumentException if terminalSerialNumbers is null or contains invalid serial numbers
     */
    boolean triggerCollectPhoto(List<String> terminalSerialNumbers);

    /**
     * Returns detail of location.
     *
     * @param locationPublicId public ID of queried location (must not be null)
     * @return location details, or {@code null} if not found
     */
    ILocationDetail getLocationByPublicId(String locationPublicId);

    /**
     * Finds a location by its external ID.
     *
     * @param externalLocationId external location ID (must not be null)
     * @param organization       organization to search within (must not be null)
     * @return the location identified by its external ID, or {@code null} if not found
     */
    ILocationDetail getLocationByExternalId(String externalLocationId, IOrganization organization);

    /**
     * Updates a location.
     *
     * @param publicId public ID of updated location (must not be null)
     * @param location new state of location to be set (must not be null)
     * @return updated location, never null
     */
    ILocation updateLocationById(String publicId, ILocationDetail location);

    /**
     * Creates a new location.
     *
     * @param location location to be created (must not be null)
     * @return created location, never null
     */
    ILocation addLocation(ILocationDetail location);

    /**
     * Starts identity verification.
     *
     * @param publicIdentityId  public ID of an existing identity to be verified (must not be null)
     * @param messageToCustomer the message that will be displayed to the customer via SMS (can be null).
     *                          Message may contain '{link}' placeholder to be replaced by link to verification provider.
     *                          If this argument is null, the default text will be used.
     * @return info about result of action, never null
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
     *
     * @param fileNameInConfigDirectory name of the file in the server config directory (must not be null)
     * @param key                       key of the property to be read from the file (must not be null)
     * @param defaultValue              value to be returned if the property is not found in the file (can be null)
     * @return value of the property from the file, or {@code defaultValue} if not found
     */
    String getConfigProperty(String fileNameInConfigDirectory, String key, String defaultValue);

    /**
     * The config directory is typically "/batm/config" so for example
     * when the {@code fileNameInConfigDirectory} parameter is "extension.debug",
     * this method will check if a file called "/batm/config/extension.debug" exist.
     *
     * @param fileNameInConfigDirectory name of the file in the server config directory (must not be null)
     * @return true if a file of the provided name exist in the server config directory.
     */
    boolean configFileExists(String fileNameInConfigDirectory);

    /**
     * The config directory is typically "/batm/config" so for example,
     * when the {@code fileNameInConfigDirectory} parameter is "version"
     * the return value will be the content of "/batm/config/version" file.
     * <p>
     * The returned value is cached for a few seconds.
     * <p>
     * If the file does not exist or any other error occurs
     * while trying to read the file, an empty string is returned.
     * <p>
     * The extension author calling this method must make sure not to expose any sensitive information
     * from the server config directory, e.g. do not use any user-provided values as the filename parameter etc.
     *
     * @param fileNameInConfigDirectory name of the file in the server config directory (must not be null)
     * @return contents of the given file in the server config directory.
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
     * Custom extensions will typically run on a standalone server, not global.
     *
     * @return true if the extension is running on a global server.
     */
    boolean isGlobalServer();

    /**
     * Marks transaction as withdrawn by a given remote or local transaction ID.
     *
     * @param remoteOrLocalTransactionId remote or local transaction ID (must not be null).
     *                                   If the transaction is not found, the method returns without error.
     */
    void markTransactionAsWithdrawn(String remoteOrLocalTransactionId);

    /**
     * Allows managing the unlock time of a specific transaction.
     * This can be used to unlock the transaction sooner or prolong it.
     * Transactions that are locked will remain in the output queue and won't be flushed until they are unlocked.
     *
     * @param rid                remote transaction ID (must not be null)
     * @param serverTimeToUnlock the absolute datetime when the transaction should unlock (must not be null)
     * @throws IllegalArgumentException if the transaction is not found
     */
    void unlockTransaction(String rid, Date serverTimeToUnlock);

    /**
     * Returns the list of custom strings.
     *
     * @param serialNumber     Serial number of terminal or GB Safe.
     * @param customStringName Name of custom string. If null, returns all custom strings of selected terminal or GB Safe.
     * @return Values of the selected custom string in all available languages from the selected terminal or GB Safe.
     * Returns an empty list if no custom string is found or an invalid / unknown serial number is provided.
     */
    default List<ICustomString> getCustomStrings(String serialNumber, String customStringName) {
        return new ArrayList<>();
    }

    /**
     * Retrieves the data required to generate a receipt, including placeholder variables
     * and any additional elements needed based on the specified template and transaction details.
     *
     * @param receiptTransferMethod the method used to send the receipt, determining the format and additional data required
     *                              (must not be null)
     * @param transactionDetails    the details of the transaction for which the receipt is being generated,
     *                              providing values for relevant placeholders (must not be null)
     * @param template              the template used for generating the receipt, which defines the structure and required placeholders
     *                              (must not be null)
     * @return a {@link ReceiptData} instance containing all necessary data and placeholders
     * for constructing the receipt according to the specified template, never null
     */
    ReceiptData getReceiptData(ReceiptTransferMethod receiptTransferMethod, ITransactionDetails transactionDetails, String template);

    /**
     * Returns the list of all configured Travel Rule Providers.
     *
     * @return List of all configured Travel Rule Providers. Empty list if no Travel Rule Providers are configured.
     */
    default List<ITravelRuleProviderIdentification> getTravelRuleProviders() { return new ArrayList<>(); }

    /**
     * Returns the list of all VASPs of given Travel Rule Provider.
     *
     * @param travelRuleProviderId ID of Travel Rule Provider.
     * @return List of all VASPs. Empty list if Travel Rule Provider is not found or does not have any VASPs.
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
     * @return the updated transaction details after the hash is added (not null)
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
     * Finds and returns travel rule transfer data by public ID of the transfer.
     *
     * @param transferPublicId Unique identifier of the transfer, generated on the server side.
     * @return The travel rule transfer data, or {@code null} if no data is found for transfer public ID.
     */
    default ITravelRuleTransferData findTravelRuleTransferByPublicId(String transferPublicId) {
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
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Converts the given crypto amount from base units to standard units.
     * For example: 100 000 000 sats (base units) = 1 BTC (standard units)
     *
     * @param amount         Amount in base units to convert.
     * @param cryptocurrency Cryptocurrency code.
     * @return Amount in standard units.
     * @throws IllegalArgumentException for unsupported cryptocurrencies.
     */
    default BigDecimal convertCryptoFromBaseUnit(long amount, String cryptocurrency) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Updates the status of an external payment.
     * @param paymentUpdate External payment update details.
     * @throws ExternalPaymentProcessingException if the payment expired or in an unexpected state.
     * @throws ExternalPaymentNotFoundException if the payment is not found.
     */
    default void updateExternalPayment(ExternalPaymentUpdate paymentUpdate) throws ExternalPaymentProcessingException {
        throw new UnsupportedOperationException("Not implemented");
    }
}
