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

package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import java.util.Arrays;

public class BlockIOResponseWithdrawalToBeSigned {
    private String status;
    private BlockIOData data;

    public BlockIOResponseWithdrawalToBeSigned() {
    }

    public class BlockIOData {
        private String reference_id;
        private boolean more_signatures_needed;
        private BlockIOInput[] inputs;
        private BlockIOEncryptedPassPhrase encrypted_passphrase;

        public BlockIOEncryptedPassPhrase getEncrypted_passphrase() {
            return encrypted_passphrase;
        }

        public void setEncrypted_passphrase(BlockIOEncryptedPassPhrase encrypted_passphrase) {
            this.encrypted_passphrase = encrypted_passphrase;
        }

        public void setMore_signatures_needed(boolean more_signatures_needed) {
            this.more_signatures_needed = more_signatures_needed;
        }

        public BlockIOInput[] getInputs() {
            return inputs;
        }

        public void setInputs(BlockIOInput[] inputs) {
            this.inputs = inputs;
        }

        public String getReference_id() {
            return reference_id;
        }

        public void setReference_id(String reference_id) {
            this.reference_id = reference_id;
        }

        public boolean isMore_signatures_needed() {
            return more_signatures_needed;
        }

        @Override
        public String toString() {
            return "BlockIOData{" +
                "reference_id='" + reference_id + '\'' +
                ", more_signatures_needed=" + more_signatures_needed +
                ", inputs=" + Arrays.toString(inputs) +
                ", encrypted_passphrase=" + encrypted_passphrase +
                '}';
        }



    }


    public String getStatus() {
        return status;
    }

    public BlockIOData getData() {
        return data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(BlockIOData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BlockIOResponseWithdrawalToBeSigned{" +
            "status='" + status + '\'' +
            ", data=" + data +
            '}';
    }
}
