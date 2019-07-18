package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto;

public class Payment {
    /**
     * A bare-bones invoice for a payment within the Lightning Network. With the details of the invoice, the sender has all the data necessary to send a payment to the recipient
     */
    public String payment_request;

    /**
     * Number of satoshis to send
     */
    public String amt;

    /**
     * The maximum number of satoshis that will be paid as a fee of the payment. This value can be represented either as
     * a percentage of the amount being sent, or as a fixed amount of the maximum fee the user is willing the pay to send the payment
     */
    public FeeLimit fee_limit;

    public static class FeeLimit {
        /**
         * The fee limit expressed as a fixed amount of satoshis
         */
        public String fixed;
        /**
         * The fee limit expressed as a percentage of the payment amount
         */
        public String percent;
    }
}
