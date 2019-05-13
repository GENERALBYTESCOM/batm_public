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

public class BlockIOResponseAddresses {
    private String status;
    private BlockIOData data;

    public BlockIOResponseAddresses() {
    }

    public class BlockIOData {
        private String network;
        private BlockIOAddress[] addresses;

        public BlockIOData() {
        }

        public String getNetwork() {
            return network;
        }

        public BlockIOAddress[] getAddresses() {
            return addresses;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public void setAddresses(BlockIOAddress[] addresses) {
            this.addresses = addresses;
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
}
