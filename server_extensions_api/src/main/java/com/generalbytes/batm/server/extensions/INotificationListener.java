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

    default void transactionSuccess(String terminalSerialNumber, BigDecimal cashAmount, String cashCurrency, String transactionRemoteId) {}

    default void transactionFailed(String terminalSerialNumber, BigDecimal cashAmount, String cashCurrency, String transactionRemoteId) {}

    /**
     * Transaction was successfully inserted into an Output Queue
     */
    default void transactionQueued(String terminalSerialNumber, BigDecimal cashAmount, String cashCurrency, String transactionRemoteId) {}

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

    default void cashLow(String terminalSerialNumber, BigDecimal cashboxTotalAmount, String currency, String cashboxName) {}

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

    default void customerEnrolled(String terminalSerialNumber, String identityPublicId) {}

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
     * There is a match for Identity on a Watchlist
     */
    default void watchlistScanIdentityMatches(String identityPublicId) {}

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
