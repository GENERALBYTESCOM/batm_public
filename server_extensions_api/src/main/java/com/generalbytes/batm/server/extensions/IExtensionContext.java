/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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

import java.io.File;

public interface IExtensionContext {

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


    //Email related stuff
    public static class EmbeddedEmailImage {
        public String contentId;
        public File pathToImage;
    }

    /**
     * Sends plain-text email asynchronously
     * @param from
     * @param addresslistTo
     * @param subject
     * @param messageText
     */
    void sendMailAsync(final String from, final String addresslistTo, final String subject, final String messageText);

    /**
     * Sends plain-text email containing attachment asynchronously
     * @param from
     * @param addresslistTo
     * @param subject
     * @param messageText
     * @param attachmentFileName
     * @param attachmentContent
     * @param attachmentMimeType
     */
    void sendMailAsyncWithAttachment(final String from, final String addresslistTo, final String subject, final String messageText, final String attachmentFileName, final byte[] attachmentContent, final String attachmentMimeType);

    /**
     * Sends email containing html text
     * @param from
     * @param addresslistTo
     * @param subject
     * @param messageText
     * @param embeddedEmailImages
     */
    void sendHTMLMailAsync(final String from, final String addresslistTo, final String subject, final String messageText, final EmbeddedEmailImage... embeddedEmailImages);

    /**
     * Sends email containing html and attachments
     * @param from
     * @param addresslistTo
     * @param subject
     * @param messageText
     * @param attachmentFileName
     * @param attachmentContent
     * @param attachmentMimeType
     */
    void sendHTMLMailAsyncWithAttachment(final String from, final String addresslistTo, final String subject, final String messageText, final String attachmentFileName, final byte[] attachmentContent, final String attachmentMimeType);

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
}
