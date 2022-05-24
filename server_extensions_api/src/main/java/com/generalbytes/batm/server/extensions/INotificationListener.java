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

    default void transactionSuccess(INotificationDetails notificationDetails){}

    default void transactionFailed(INotificationDetails notificationDetails){}

    default void walletCryptoBalanceLow(INotificationDetails notificationDetails){}

    default void exchangeCryptoBalanceLow(INotificationDetails notificationDetails){}

    default void cashHigh(INotificationDetails notificationDetails){}

    default void customerEnrolled(INotificationDetails notificationDetails){}

    default void terminalOffline(INotificationDetails notificationDetails){}

    default void terminalWentOnlineAfterOffline(INotificationDetails notificationDetails){}

    default void stackerManipulation(INotificationDetails notificationDetails){}

    default void terminalError(INotificationDetails notificationDetails){}

    default void transactionCashLimitReached(INotificationDetails notificationDetails){}

    default void cashLow(INotificationDetails notificationDetails){}

    default void cashCountHigh(INotificationDetails notificationDetails){}

    default void cashCountLow(INotificationDetails notificationDetails){}

    default void invalidPaymentReceived(INotificationDetails notificationDetails){}

    default void clearedBalance(INotificationDetails notificationDetails){}

    default void transactionSupplyLimitReached(INotificationDetails notificationDetails){}

    default void rateSourceNotAvailable(INotificationDetails notificationDetails){}

    default void countersShortCleared(INotificationDetails notificationDetails){}

    default void watchlistScanBan(INotificationDetails notificationDetails){}

    default void exchangeFiatBalanceLow(INotificationDetails notificationDetails){}

    default void stackerOutForPeriod(INotificationDetails notificationDetails){}

    default void doorSensor(INotificationDetails notificationDetails){}

    default void stackerOutOutsideOpeningHours(INotificationDetails notificationDetails){}

    default void transactionScoring(INotificationDetails notificationDetails){}

    default void qrCodeStickerDetected(INotificationDetails notificationDetails){}

    default void alarmStatus(INotificationDetails notificationDetails){}

    default void alarmPinRejected(INotificationDetails notificationDetails){}

    default void alarmPinAccepted(INotificationDetails notificationDetails){}

    default void alarmTriggered(INotificationDetails notificationDetails){}

    default void transactionScoringSuspicious(INotificationDetails notificationDetails){}

    default void ipWhitelistRejected(INotificationDetails notificationDetails){}

    default void dispenserCassetteManipulation(INotificationDetails notificationDetails){}

    default void acceptorAcceptanceProblem(INotificationDetails notificationDetails){}

    default void cashbackCreated(INotificationDetails notificationDetails){}

    default void cashCollectionMissed(INotificationDetails notificationDetails){}

    default void dispenserInitFailed(INotificationDetails notificationDetails){}

    default void transactionAttemptsReached(INotificationDetails notificationDetails){}

    default void machineInactivity(INotificationDetails notificationDetails){}

    default void possibleFraud(INotificationDetails notificationDetails){}

    default void blacklistedAddressUsed(INotificationDetails notificationDetails){}

    default void watchedIdentityTransaction(INotificationDetails notificationDetails){}

    default void recyclerCashLow(INotificationDetails notificationDetails){}

    default void terminalRecoveredFromError(INotificationDetails notificationDetails){}

    default void transactionQueued(INotificationDetails notificationDetails){}

    default void ipWhitelistAutoUpdated(INotificationDetails notificationDetails){}

    default void certificateFingerprintMismatch(INotificationDetails notificationDetails){}

    default void cashboxEmptyError(INotificationDetails notificationDetails){}

    default void watchlistScanIdentityMatches(INotificationDetails notificationDetails){}

    default void lifetimeIdentityVolumeReached(INotificationDetails notificationDetails){}

    default void customerAgreedToMarketingOptIn(INotificationDetails notificationDetails){}

    default void walletCryptoBalanceHigh(INotificationDetails notificationDetails){}

    default void exchangeCryptoBalanceHigh(INotificationDetails notificationDetails){}

    default void travelRuleSubmissionFailed(INotificationDetails notificationDetails){}
}
