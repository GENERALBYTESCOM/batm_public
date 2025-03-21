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

package com.generalbytes.batm.server.extensions.communication;

/**
 * Provider for sending text messages.
 */
public interface ICommunicationProvider {

    /**
     * Name of the messaging provider. Internally used as an identifier within the system.
     */
    String getName();

    /**
     * Public name of the provider (e.g. SMS, WhatsApp). This name shown in the UI (e.g. on terminals).
     */
    default String getPublicName() {
        return "SMS";
    }

    /**
     * Send the sms.
     */
    ISmsResponse sendSms(String credentials, String phoneNumber, String messageText);

}
