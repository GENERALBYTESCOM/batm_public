package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Provides details about created deposit transaction.
 */
public interface IDepositDetails {

    /**
     * Serial number of the GB Safe where the deposit was made.
     */
    String getSafeSerialNumber();

    /**
     * Status of the deposit transaction.
     * <ul>
     *   <li>0 - completed</li>
     *   <li>1 - error</li>
     * </ul>
     */
    int getStatus();

    /**
     * Server time of the deposit transaction.
     */
    Date getServerTime();

    /**
     * GB Safe time of the deposit transaction.
     */
    Date getSafeTime();

    /**
     * Deposit code of the related order transaction.
     */
    String getDepositCode();

    /**
     * Identity of the customer who made the deposit.
     */
    IIdentity getIdentity();

    /**
     * Amount of cash deposited.
     */
    BigDecimal getCashAmount();

    /**
     * Fiat currency of the cash deposited.
     */
    String getCashCurrency();

    /**
     * Remote transaction ID of the deposit.
     */
    String getRemoteTransactionId();

    /**
     * Local transaction ID of the deposit.
     */
    String getLocalTransactionId();

    /**
     * List of banknotes deposited in the transaction.
     */
    List<IBanknoteCounts> getBanknotes();
}
