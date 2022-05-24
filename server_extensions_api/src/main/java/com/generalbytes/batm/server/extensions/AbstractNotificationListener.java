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

public abstract class AbstractNotificationListener implements INotificationListener {

    @Override
    public void transactionSuccess(INotificationDetails notificationDetails) {
    }

    @Override
    public void transactionFailed(INotificationDetails notificationDetails) {
    }

    @Override
    public void walletCryptoBalanceLow(INotificationDetails notificationDetails) {
    }

    @Override
    public void exchangeCryptoBalanceLow(INotificationDetails notificationDetails) {
    }

    @Override
    public void cashHigh(INotificationDetails notificationDetails) {
    }

    @Override
    public void customerEnrolled(INotificationDetails notificationDetails) {
    }

    @Override
    public void terminalOffline(INotificationDetails notificationDetails) {
    }

    @Override
    public void terminalWentOnlineAfterOffline(INotificationDetails notificationDetails) {
    }

    @Override
    public void stackerManipulation(INotificationDetails notificationDetails) {
    }

    @Override
    public void terminalError(INotificationDetails notificationDetails) {
    }

    @Override
    public void transactionCashLimitReached(INotificationDetails notificationDetails) {
    }

    @Override
    public void cashLow(INotificationDetails notificationDetails) {
    }

    @Override
    public void cashCountHigh(INotificationDetails notificationDetails) {
    }

    @Override
    public void cashCountLow(INotificationDetails notificationDetails) {
    }

    @Override
    public void invalidPaymentReceived(INotificationDetails notificationDetails) {
    }

    @Override
    public void clearedBalance(INotificationDetails notificationDetails) {
    }

    @Override
    public void transactionSupplyLimitReached(INotificationDetails notificationDetails) {
    }

    @Override
    public void rateSourceNotAvailable(INotificationDetails notificationDetails) {
    }

    @Override
    public void countersShortCleared(INotificationDetails notificationDetails) {
    }

    @Override
    public void watchlistScanBan(INotificationDetails notificationDetails) {
    }

    @Override
    public void exchangeFiatBalanceLow(INotificationDetails notificationDetails) {
    }

    @Override
    public void stackerOutForPeriod(INotificationDetails notificationDetails) {
    }

    @Override
    public void doorSensor(INotificationDetails notificationDetails) {
    }

    @Override
    public void stackerOutOutsideOpeningHours(INotificationDetails notificationDetails) {
    }

    @Override
    public void transactionScoring(INotificationDetails notificationDetails) {
    }

    @Override
    public void qrCodeStickerDetected(INotificationDetails notificationDetails) {
    }

    @Override
    public void alarmStatus(INotificationDetails notificationDetails) {
    }

    @Override
    public void alarmPinRejected(INotificationDetails notificationDetails) {
    }

    @Override
    public void alarmPinAccepted(INotificationDetails notificationDetails) {
    }

    @Override
    public void alarmTriggered(INotificationDetails notificationDetails) {
    }

    @Override
    public void transactionScoringSuspicious(INotificationDetails notificationDetails) {
    }

    @Override
    public void ipWhitelistRejected(INotificationDetails notificationDetails) {
    }

    @Override
    public void dispenserCassetteManipulation(INotificationDetails notificationDetails) {
    }

    @Override
    public void acceptorAcceptanceProblem(INotificationDetails notificationDetails) {
    }

    @Override
    public void cashbackCreated(INotificationDetails notificationDetails) {
    }

    @Override
    public void cashCollectionMissed(INotificationDetails notificationDetails) {
    }

    @Override
    public void dispenserInitFailed(INotificationDetails notificationDetails) {
    }

    @Override
    public void transactionAttemptsReached(INotificationDetails notificationDetails) {
    }

    @Override
    public void machineInactivity(INotificationDetails notificationDetails) {
    }

    @Override
    public void possibleFraud(INotificationDetails notificationDetails) {
    }

    @Override
    public void blacklistedAddressUsed(INotificationDetails notificationDetails) {
    }

    @Override
    public void watchedIdentityTransaction(INotificationDetails notificationDetails) {
    }

    @Override
    public void recyclerCashLow(INotificationDetails notificationDetails) {
    }

    @Override
    public void terminalRecoveredFromError(INotificationDetails notificationDetails) {
    }

    @Override
    public void transactionQueued(INotificationDetails notificationDetails) {
    }

    @Override
    public void ipWhitelistAutoUpdated(INotificationDetails notificationDetails) {
    }

    @Override
    public void certificateFingerprintMismatch(INotificationDetails notificationDetails) {
    }

    @Override
    public void cashboxEmptyError(INotificationDetails notificationDetails) {
    }

    @Override
    public void watchlistScanIdentityMatches(INotificationDetails notificationDetails) {
    }

    @Override
    public void lifetimeIdentityVolumeReached(INotificationDetails notificationDetails) {
    }

    @Override
    public void customerAgreedToMarketingOptIn(INotificationDetails notificationDetails) {
    }

    @Override
    public void walletCryptoBalanceHigh(INotificationDetails notificationDetails) {
    }

    @Override
    public void exchangeCryptoBalanceHigh(INotificationDetails notificationDetails) {
    }

    @Override
    public void travelRuleSubmissionFailed(INotificationDetails notificationDetails) {
    }
}
