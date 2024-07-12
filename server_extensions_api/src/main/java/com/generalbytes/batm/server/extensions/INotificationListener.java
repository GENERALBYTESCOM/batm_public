/*************************************************************************************
 * Copyright (C) 2014-2024 GENERAL BYTES s.r.o. All rights reserved.
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

import com.generalbytes.batm.server.extensions.watchlist.WatchListScanIdentityMatchesData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Instances implementing this interface could be registered using {@link IExtensionContext#addNotificationListener}
 * to receive various events about transactions, terminals, etc. The same events are used to trigger notifications
 * configured in Notification Policies in CAS, but extensions using this interface receive all the notifications
 * regardless of any configuration in Notification Policies in CAS.
 *
 * For most of the notifications the listener receives terminal serial number and transaction RID or identity public ID etc.,
 * and the listener can fetch additional information (terminal Location, transaction details etc.) using {@link IExtensionContext}.
 */
public interface INotificationListener {

    /**
     * @param paymentType "CASH", "PAYMENT_CARD", or null
     */
    default void transactionSuccess(String terminalSerialNumber, BigDecimal cashAmount, String cashCurrency, String transactionRemoteId, String paymentType) {}

    /**
     * @param paymentType "CASH", "PAYMENT_CARD", or null
     */
    default void transactionFailed(String terminalSerialNumber, BigDecimal cashAmount, String cashCurrency, String transactionRemoteId, String paymentType) {}

    /**
     * Transaction was successfully inserted into an Output Queue.
     *
     * @param paymentType "CASH", "PAYMENT_CARD", or null
     */
    default void transactionQueued(String terminalSerialNumber, BigDecimal cashAmount, String cashCurrency, String transactionRemoteId, String paymentType) {}

    /**
     * Invoked when one or more transactions in a queue have failed.
     *
     * @param queueName          The name of the queue in which the transactions were queued.
     * @param batchUid           A unique identifier representing the batch of transactions.
     * @param failedTransactions A list of transaction details for those transactions which failed.
     *                           Each transaction detail includes information such as the serial number of the terminal.
     *
     * <p>
     * ITransactionDetails will have the following attributes populated:
     * <ul>
     *     <li><b>Terminal Serial Number</b>: Sourced from the transaction's terminal..</li>
     *     <li><b>Remote ID (Rid)</b>: The Rid associated with the transaction</li>
     *     <li><b>Local ID (Lid)</b>: The Lid associated with the transaction.</li>
     *     <li><b>Cash Amount</b>: The fiat amount of transaction.</li>
     *     <li><b>Cash Currency</b>: The type of fiat currency used in the transaction (e.g., USD, EUR).</li>
     *     <li><b>Crypto Amount</b>: The fiat amount of transaction.</li>
     *     <li><b>Crypto Currency</b>: The type of cryptocurrency used in the transaction (e.g., BTC, ETH).</li>
     *     <li><b>Crypto Address</b>: The destination address for the cryptocurrency transaction.</li>
     *     <li><b>Identity Public ID</b>: Public identity associated with the transaction, if any.</li>
     * </ul>
     * </p>
     * <p>
     * All other attributes are either set to default values or empty.
     * </p>
     *
     */
    default void queuedTransactionsFailed(String queueName, String batchUid, List<ITransactionDetails> failedTransactions) {}

    default void cashbackCreated(String terminalSerialNumber, BigDecimal cashAmount, String cashCurrency) {}

    default void invalidPaymentReceived(String terminalSerialNumber, BigDecimal amount, String cryptoCurrency, String fromAddress, String toAddress, String transactionRemoteId) {}

    /**
     * Note: the "high" and "low" listener callbacks are triggered regardless of any configuration in Notification Policies,
     * you might need to implement your own logic to decide the low and high amount in your listener.
     */
    default void walletCryptoBalanceLow(String terminalSerialNumber, BigDecimal balance, String cryptoCurrency) {}

    default void walletCryptoBalanceHigh(String terminalSerialNumber, BigDecimal balance, String cryptoCurrency) {}

    default void exchangeCryptoBalanceLow(String terminalSerialNumber, BigDecimal balance, String cryptoCurrency) {}

    default void exchangeCryptoBalanceHigh(String terminalSerialNumber, BigDecimal balance, String cryptoCurrency) {}

    default void exchangeFiatBalanceLow(String terminalSerialNumber, BigDecimal balance, String cryptoCurrency) {}

    default void recyclerCashLow(String terminalSerialNumber, BigDecimal recyclerTotalAmount, String currency) {}

    /**
     * Current total cash amount in a cashbox.
     * <p>
     * Note: the "high" and "low" listener callbacks are triggered regardless of any configuration in Notification Policies,
     * you might need to implement your own logic to decide the low and high amount in your listener.
     */
    default void cashHigh(String terminalSerialNumber, BigDecimal cashboxTotalAmount, String currency, String cashboxName) {}

    /**
     * Deprecated since 1.1.7, use {@link INotificationListener#cashLow(String, Map, String)} instead.
     *
     * @param terminalSerialNumber Serial number of terminal.
     * @param cashboxTotalAmount   Total amount in cashboxes.
     * @param currency             Currency.
     * @param cashboxName          Cashbox name.
     */
    @Deprecated
    default void cashLow(String terminalSerialNumber, BigDecimal cashboxTotalAmount, String currency, String cashboxName) {}

    /**
     * Triggered when the total cash amount is less than the threshold set in Notification Policies.
     *
     * @param terminalSerialNumber Serial number of terminal.
     * @param amountsPerCashbox    Amounts per cashbox. Key = cashbox name, Value = amount.
     * @param currency             Currency.
     */
    default void cashLow(String terminalSerialNumber, Map<String, BigDecimal> amountsPerCashbox, String currency) {}

    /**
     * Current banknote count in a cashbox
     */
    default void cashCountHigh(String terminalSerialNumber, String cashboxName, int cashboxBanknoteCount) {}

    default void cashCountLow(String terminalSerialNumber, String cashboxName, int cashboxBanknoteCount) {}

    default void stackerFull(String terminalSerialNumber) {}

    default void stackerIn(String terminalSerialNumber) {}

    default void stackerOut(String terminalSerialNumber) {}

    default void stackerOutOutsideOpeningHours(String terminalSerialNumber) {}

    default void stackerOutForPeriod(String terminalSerialNumber, Date lastStackerOutEventTime) {}

    default void doorSensor(String terminalSerialNumber, String eventData) {}

    /**
     * There was attempt to perform transaction with machine door opened
     */
    default void possibleFraud(String terminalSerialNumber) {}

    default void qrCodeStickerDetected(String terminalSerialNumber, String qrCodeContent) {}

    /**
     * Alarm has been armed or disarmed
     *
     * @param alarmStatus new alarm status (ARMED, DISARMED)
     */
    default void alarmStatus(String terminalSerialNumber, String alarmStatus) {}

    default void alarmPinRejected(String terminalSerialNumber) {}

    default void alarmPinAccepted(String terminalSerialNumber) {}

    default void alarmTriggered(String terminalSerialNumber) {}

    /**
     * Terminal recognized that cassette/s were removed from the bill dispenser
     */
    default void dispenserCassetteOut(String terminalSerialNumber, String cassetteInfo) {}

    /**
     * Terminal recognized that cassette/s were inserted into the bill dispenser
     */
    default void dispenserCassetteIn(String terminalSerialNumber, String cassetteInfo) {}

    /**
     * Terminal recognized cassette position change during restart
     */
    default void dispenserCassettePositionChange(String terminalSerialNumber) {}

    /**
     * User have changed cassette position and confirmed that banknote denomination and count written in cassette config are correct
     */
    default void userConfirmedDispenserCassettePositionChange(String terminalSerialNumber) {}

    default void dispenserInitFailed(String terminalSerialNumber) {}

    /**
     * Dispenser reported that a cassette is empty when it attempted to dispense
     */
    default void cashboxEmptyError(String terminalSerialNumber, String emptyCassetteName) {}

    /**
     * A customer have trouble finishing a transaction few times in row
     */
    default void transactionAttemptsReached(String terminalSerialNumber) {}

    /**
     * Acceptance problem acceptor could not accept banknote few times in a row
     */
    default void acceptorAcceptanceProblem(String terminalSerialNumber) {}

    default void acceptorIsNotDetected(String terminalSerialNumber) {}

    default void acceptorDisconnected(String terminalSerialNumber) {}

    /**
     * Machine is inactive for long time period
     */
    default void machineInactivity(String terminalSerialNumber) {}

    default void ipWhitelistRejected(String terminalSerialNumber, String oldIpWhitelist, String newIpWhitelist) {}

    default void ipWhitelistAutoUpdated(String terminalSerialNumber, String oldIpWhitelist, String newIpWhitelist) {}

    /**
     * Server rejected terminal due to certificate fingerprint mismatch
     */
    default void certificateFingerprintMismatch(String terminalSerialNumber) {}

    default void terminalOffline(String terminalSerialNumber, Date lastPing) {}

    default void terminalWentOnlineAfterOffline(String terminalSerialNumber, Date onlineSince) {}

    default void terminalError(String terminalSerialNumber, List<String> terminalErrorNames) {}

    default void terminalRecoveredFromError(String terminalSerialNumber, List<String> recoveredFromErrorNames) {}

    default void rateSourceNotAvailable(String terminalSerialNumber, List<String> notAvailableRateSources) {}

    default void rateSourceAvailable(String terminalSerialNumber, List<String> newlyAvailableRateSources) {}

    default void clearedBalance(String terminalSerialNumber) {}

    default void countersShortCleared(String terminalSerialNumber, String cashCollectionPublicId) {}

    /**
     * Planned cash collection is missing
     */
    default void cashCollectionMissed(String terminalSerialNumber, LocalDate missedCashCollectionDate) {}

    /**
     * Invoked when a new cash collection record has been created.
     *
     * @param cashCollectionRecord Represents the details of the created cash collection record.
     *
     * <p>
     * ITerminalCashCollectionRecord will have the following attributes populated:
     * <ul>
     *     <li><b>Terminal Serial Number</b>: The serial number of the terminal where the cash collection event occurred.</li>
     *     <li><b>Terminal Time</b>: Timestamp when event was created on terminal.</li>
     *     <li><b>Server Time</b>: Timestamp when event was delivered and stored on server.</li>
     *     <li><b>Amounts</b>: A collection of the total amounts in the cashbox, broken down by fiat currency. For example, if the ATM only sells BTC for USD, this will contain one member.</li>
     *     <li><b>Collecting Person</b>: Details of the person who performed the cash collection, if available.</li>
     *     <li><b>Contains</b>: A string containing a description of what was in the cashbox during the cash collection.</li>
     *     <li><b>Note</b>: Any additional text description set by the user via admin.</li>
     *     <li><b>Counters Long</b>: The value of the long counter at the time of cash collection.</li>
     *     <li><b>Counters Short</b>: The value of the short counter at the time of cash collection before it was reset.</li>
     *     <li><b>Cashbox Name</b>: The name of the cashbox.</li>
     *     <li><b>Public ID</b>: The public ID of the cash collection.</li>
     *     <li><b>Location Public ID</b>: The public ID of the related location.</li>
     * </ul>
     * </p>
     */
    default void cashCollectionCreated(ITerminalCashCollectionRecord cashCollectionRecord) {}

    default void customerEnrolled(String terminalSerialNumber, String identityPublicId) {}

    default void identityCreated(String terminalSerialNumber, String identityPublicId) {}

    default void dispenserBanknoteCountSet(String terminalSerialNumber, String cashBoxName, String cashBoxItemCurrency, BigDecimal cashBoxItemDenomination, int cashBoxItemCountNew, int cashBoxItemCountPrevious) {}

    default void customerAgreedToMarketingOptIn(String terminalSerialNumber, String identityPublicId) {}

    default void blacklistedAddressUsed(String terminalSerialNumber, String cryptoCurrency, String address, String identityPublicId) {}

    /**
     * Watched identity performed a transaction
     */
    default void watchedIdentityTransaction(String terminalSerialNumber, String identityPublicId, String transactionRemoteId) {}

    /**
     * name found on a watchlist
     */
    default void watchlistScanBan(String terminalSerialNumber) {}

    /**
     * Deprecated since 1.6.4, use {@link INotificationListener#watchlistScanIdentityMatches(WatchListScanIdentityMatchesData)} instead.
     *
     * @param identityPublicId Public ID of Identity.
     */
    @Deprecated
    default void watchlistScanIdentityMatches(String identityPublicId) {}

    /**
     * Triggered if there is a match on the WatchList with the Identity. Contains detailed information.
     *
     * @param data Object containing detailed information about match result.
     */
    default void watchlistScanIdentityMatches(WatchListScanIdentityMatchesData data) {}

    default void lifetimeIdentityVolumeReached(String terminalSerialNumber, BigDecimal lifetimeVolume, String cashCurrency, BigDecimal preConditionAmount, String identityPublicId) {}

    default void transactionSupplyLimitReached(String terminalSerialNumber) {}

    default void transactionCashLimitReached(String terminalSerialNumber, BigDecimal cashAmount, String cashCurrency, String identityPublicId, String limitName, BigDecimal resultingLimit, Map<String, BigDecimal> limitsReached) {}

    /**
     * Transaction scoring provider flagged this address as high risk and blocked it
     */
    default void highRiskAddress(String terminalSerialNumber, String address, String cryptoCurrency, String identityPublicId) {}

    /**
     * Transaction scoring provider flagged this incoming transaction as high risk and blocked it
     */
    default void highRiskTransaction(String terminalSerialNumber, String transactionHash, String cryptoCurrency, String identityPublicId) {}

    /**
     * Transaction scoring provider flagged this address as suspicious (not blocked)
     */
    default void suspiciousAddress(String terminalSerialNumber, String address, String cryptoCurrency, String identityPublicId) {}

    /**
     * Transaction scoring provider flagged this incoming transaction as suspicious (not blocked)
     */
    default void suspiciousTransaction(String terminalSerialNumber, String transactionHash, String cryptoCurrency, String identityPublicId) {}

    /**
     * Travel Rule transaction submission failed
     */
    default void travelRuleSubmissionFailed(String terminalSerialNumber, String identityPublicId, String response, BigDecimal cryptoAmount, String cryptoCurrency, String cryptoAddress) {}

    default void verificationProviderApproved(String terminalSerialNumber, String identityPublicId) {}

    default void verificationProviderDeclined(String terminalSerialNumber, String identityPublicId) {}

}
