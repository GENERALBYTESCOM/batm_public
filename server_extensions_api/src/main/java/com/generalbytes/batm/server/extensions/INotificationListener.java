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

/**
 * Instances implementing this interface could be registered using {@link IExtensionContext#addNotificationListener}
 * to receive various events about transactions, terminals, etc. The same events are used to trigger notifications
 * configured in Notification Policies in CAS, but extensions using this interface receive all the notifications
 * regardless of the notifications, parameters or actions configured in CAS.
 */
public interface INotificationListener {

    void transactionSuccess(INotificationDetails notificationDetails);

    void transactionFailed(INotificationDetails notificationDetails);

    void walletCryptoBalanceLow(INotificationDetails notificationDetails);

    void exchangeCryptoBalanceLow(INotificationDetails notificationDetails);

    void cashHigh(INotificationDetails notificationDetails);

    void customerEnrolled(INotificationDetails notificationDetails);

    void terminalOffline(INotificationDetails notificationDetails);

    void terminalWentOnlineAfterOffline(INotificationDetails notificationDetails);

    void stackerManipulation(INotificationDetails notificationDetails);

    void terminalError(INotificationDetails notificationDetails);

    void transactionCashLimitReached(INotificationDetails notificationDetails);

    void cashLow(INotificationDetails notificationDetails);

    void cashCountHigh(INotificationDetails notificationDetails);

    void cashCountLow(INotificationDetails notificationDetails);

    void invalidPaymentReceived(INotificationDetails notificationDetails);

    void clearedBalance(INotificationDetails notificationDetails);

    void transactionSupplyLimitReached(INotificationDetails notificationDetails);

    void rateSourceNotAvailable(INotificationDetails notificationDetails);

    void countersShortCleared(INotificationDetails notificationDetails);

    void watchlistScanBan(INotificationDetails notificationDetails);

    void exchangeFiatBalanceLow(INotificationDetails notificationDetails);

    void stackerOutForPeriod(INotificationDetails notificationDetails);

    void doorSensor(INotificationDetails notificationDetails);

    void stackerOutOutsideOpeningHours(INotificationDetails notificationDetails);

    void transactionScoring(INotificationDetails notificationDetails);

    void qrCodeStickerDetected(INotificationDetails notificationDetails);

    void alarmStatus(INotificationDetails notificationDetails);

    void alarmPinRejected(INotificationDetails notificationDetails);

    void alarmPinAccepted(INotificationDetails notificationDetails);

    void alarmTriggered(INotificationDetails notificationDetails);

    void transactionScoringSuspicious(INotificationDetails notificationDetails);

    void ipWhitelistRejected(INotificationDetails notificationDetails);

    void dispenserCassetteManipulation(INotificationDetails notificationDetails);

    void acceptorAcceptanceProblem(INotificationDetails notificationDetails);

    void cashbackCreated(INotificationDetails notificationDetails);

    void cashCollectionMissed(INotificationDetails notificationDetails);

    void dispenserInitFailed(INotificationDetails notificationDetails);

    void transactionAttemptsReached(INotificationDetails notificationDetails);

    void machineInactivity(INotificationDetails notificationDetails);

    void possibleFraud(INotificationDetails notificationDetails);

    void blacklistedAddressUsed(INotificationDetails notificationDetails);

    void watchedIdentityTransaction(INotificationDetails notificationDetails);

    void recyclerCashLow(INotificationDetails notificationDetails);

    void terminalRecoveredFromError(INotificationDetails notificationDetails);

    void transactionQueued(INotificationDetails notificationDetails);

    void ipWhitelistAutoUpdated(INotificationDetails notificationDetails);

    void certificateFingerprintMismatch(INotificationDetails notificationDetails);

    void cashboxEmptyError(INotificationDetails notificationDetails);

    void watchlistScanIdentityMatches(INotificationDetails notificationDetails);

    void lifetimeIdentityVolumeReached(INotificationDetails notificationDetails);

    void customerAgreedToMarketingOptIn(INotificationDetails notificationDetails);

    void walletCryptoBalanceHigh(INotificationDetails notificationDetails);

    void exchangeCryptoBalanceHigh(INotificationDetails notificationDetails);

    void travelRuleSubmissionFailed(INotificationDetails notificationDetails);
}
