/*************************************************************************************
 * Copyright (C) 2014 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinkite;

public class AccountResponse {
    private Account account;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }


    public class Account {
        private String CK_acct_type;
        private String CK_refnum;
        private String CK_type;
        private String coin_type;
        private String created_at;
        private String name;
        private String quick_deposit;
        private Balance balance;

        public String getCK_acct_type() {
            return CK_acct_type;
        }

        public String getCK_refnum() {
            return CK_refnum;
        }

        public String getCK_type() {
            return CK_type;
        }

        public String getCoin_type() {
            return coin_type;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getName() {
            return name;
        }

        public String getQuick_deposit() {
            return quick_deposit;
        }

        public Balance getBalance() {
            return balance;
        }

        public class Balance {
            private String currency;
            private String string;

            public String getCurrency() {
                return currency;
            }

            public String getString() {
                return string;
            }
        }



    }


}
