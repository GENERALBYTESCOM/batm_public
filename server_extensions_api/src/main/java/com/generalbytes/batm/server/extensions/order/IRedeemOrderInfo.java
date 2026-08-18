package com.generalbytes.batm.server.extensions.order;

import com.generalbytes.batm.server.extensions.ITransactionDetails;

/**
 * Information about the BUY transaction created by redeeming an order.
 *
 * @see com.generalbytes.batm.server.extensions.IExtensionContext#redeemOrder(IRedeemOrderRequest)
 */
public interface IRedeemOrderInfo {

    /** Identifier (RID) of the BUY transaction created by the redemption. */
    String getTransactionId();

    /** Transaction status. See {@link ITransactionDetails} BUY status constants. */
    int getStatus();
}
