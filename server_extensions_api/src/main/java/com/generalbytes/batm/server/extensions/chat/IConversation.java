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
package com.generalbytes.batm.server.extensions.chat;

import java.io.InputStream;

public interface IConversation {
    /**
     * Returns id of conversation. Used when sending message back to user or group.
     * @return
     */
    String getId();

    /**
     * Returns sender's user name
     * @return
     */
    String getSenderUsername();

    /**
     * Returns sender's user Id.
     * @return
     */
    String getSenderUserId();

    /**
     * Returns this bot username
     * @return
     */
    String getMyUsername();

    /**
     * Sends text back to this conversation.
     * @param text
     */
    void sendText(String text);

    /**
     * Sends text to arbitrary conversation id. For example to different user or group.
     * @param conversationId
     * @param text
     */
    void sendText(String conversationId, String text);

    /**
     * Sends photo back to this conversation.
     * @param photoName
     * @param inputStream
     */
    void sendPhoto(String photoName, InputStream inputStream);

    /**
     * Sends photo to arbitrary conversation id. For example to different user or group.
     * @param conversationId
     * @param photoName
     * @param inputStream
     */
    void sendPhoto(String conversationId, String photoName, InputStream inputStream);

    /**
     * Sends document/file to this conversation..
     * @param documentName
     * @param inputStream
     */
    void sendDocument(String documentName, InputStream inputStream);

    /**
     * Sends document/file to arbitrary conversation id. For example to different user or group.
     * @param conversationId
     * @param documentName
     * @param inputStream
     */
    void sendDocument(String conversationId, String documentName, InputStream inputStream);
}
