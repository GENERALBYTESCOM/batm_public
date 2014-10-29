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

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoinkiteSendResponse {
    private SendArguments args;
    private String next_step;
    private Result result;



    public String getNext_step() {
        return next_step;
    }

    public void setNext_step(String next_step) {
        this.next_step = next_step;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public SendArguments getArgs() {
        return args;
    }

    public void setArgs(SendArguments args) {
        this.args = args;
    }

    public class SendArguments {

    }

    public class Result {
        @JsonProperty("CK_refnum")
        private String CK_refnum;
        @JsonProperty("CK_req_type")
        private String CK_req_type;
        private String account;
        private String coin_type;
        private String created_at;
        private String desc;
        private String destination;
        private String detail_page;
        private boolean include_pin;
        private boolean is_completed;
        private String memo;
        private String send_authcode;

        public String getCK_refnum() {
            return CK_refnum;
        }

        public void setCK_refnum(String CK_refnum) {
            this.CK_refnum = CK_refnum;
        }

        public String getCK_req_type() {
            return CK_req_type;
        }

        public void setCK_req_type(String CK_req_type) {
            this.CK_req_type = CK_req_type;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getCoin_type() {
            return coin_type;
        }

        public void setCoin_type(String coin_type) {
            this.coin_type = coin_type;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getDetail_page() {
            return detail_page;
        }

        public void setDetail_page(String detail_page) {
            this.detail_page = detail_page;
        }

        public boolean isInclude_pin() {
            return include_pin;
        }

        public void setInclude_pin(boolean include_pin) {
            this.include_pin = include_pin;
        }

        public boolean isIs_completed() {
            return is_completed;
        }

        public void setIs_completed(boolean is_completed) {
            this.is_completed = is_completed;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public String getSend_authcode() {
            return send_authcode;
        }

        public void setSend_authcode(String send_authcode) {
            this.send_authcode = send_authcode;
        }
    }


}
