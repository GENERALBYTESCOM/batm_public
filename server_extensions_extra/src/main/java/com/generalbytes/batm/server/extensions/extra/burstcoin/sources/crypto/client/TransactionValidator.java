/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.burstcoin.sources.crypto.client;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TransactionValidator {

    public static void validateUnsignedTransaction(byte[] unsignedTransactionBytes, long expectedRecipientId, long expectedAmountNQT, long expectedFeeNQT) throws IllegalArgumentException {
        validate(unsignedTransactionBytes.length == 176);

        ByteBuffer buffer = ByteBuffer.wrap(unsignedTransactionBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        byte type = buffer.get();
        byte subtype = buffer.get();
        byte version = (byte) ((subtype & 0xF0) >> 4);
        subtype = (byte) (subtype & 0x0F);
        int timestamp = buffer.getInt();
        short deadline = buffer.getShort();
        byte[] senderPublicKey = new byte[32];
        buffer.get(senderPublicKey);
        long recipientId = buffer.getLong();
        long amountNQT = buffer.getLong();
        long feeNQT = buffer.getLong();
        byte[] referencedTransactionFullHashBytes = new byte[32];
        buffer.get(referencedTransactionFullHashBytes);
        byte[] signature = new byte[64];
        buffer.get(signature);

        validate(type == (byte) 0);
        validate(subtype == (byte) 0);
        validate(version == (byte) 1);
        validate(timestamp > 0);
        validate(deadline > 0 && deadline <= 1440);
        validate(recipientId == expectedRecipientId);
        validate(amountNQT == expectedAmountNQT);
        validate(feeNQT == expectedFeeNQT);
    }

    private static void validate(boolean validationResult) {
        if(!validationResult) {
            throw new IllegalArgumentException();
        }
    }
}
