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
package com.generalbytes.batm.server.extensions.communication;

import java.util.Locale;

/**
 * Alternative messaging provider (e.g., WhatsApp), extending {@link ICommunicationProvider}
 * for compatibility with GSM SMS-based communication.
 */
public interface IMessagingProvider extends ICommunicationProvider {

    /**
     * Gets the localized call-to-action (CTA) button text.
     *
     * @param locale the locale for the text
     * @return the CTA button text (e.g., "Use WhatsApp instead")
     */
    String getCtaButtonText(Locale locale);

    /**
     * Gets the resource path of the CTA button icon.
     *
     * @return the icon path (e.g., "/Icons/whatsapp.png")
     */
    String getCtaButtonIconPath();
}