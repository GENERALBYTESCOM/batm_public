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
package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto;

import java.util.Arrays;

public class BlockIOInput {
    private int input_no;
    private int signatures_needed;
    private String data_to_sign; //in hex
    private BlockIOSigner[] signers;

    public BlockIOInput() {
    }

    public int getInput_no() {
        return input_no;
    }

    public void setInput_no(int input_no) {
        this.input_no = input_no;
    }

    public int getSignatures_needed() {
        return signatures_needed;
    }

    public void setSignatures_needed(int signatures_needed) {
        this.signatures_needed = signatures_needed;
    }

    public String getData_to_sign() {
        return data_to_sign;
    }

    public void setData_to_sign(String data_to_sign) {
        this.data_to_sign = data_to_sign;
    }

    public BlockIOSigner[] getSigners() {
        return signers;
    }

    public void setSigners(BlockIOSigner[] signers) {
        this.signers = signers;
    }

    @Override
    public String toString() {
        return "BlockIOInput{" +
            "input_no=" + input_no +
            ", signatures_needed=" + signatures_needed +
            ", data_to_sign='" + data_to_sign + '\'' +
            ", signers=" + Arrays.toString(signers) +
            '}';
    }
}
