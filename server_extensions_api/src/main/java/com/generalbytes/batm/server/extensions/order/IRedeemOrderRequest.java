package com.generalbytes.batm.server.extensions.order;

import com.generalbytes.batm.server.extensions.IExtensionContext;

/**
 * Parameters for redeeming a previously created order.
 * @see IExtensionContext#redeemOrder(IRedeemOrderRequest)
 */
public interface IRedeemOrderRequest {

    /**
     * Required. The {@link IOrderInfo#getTransactionId()} returned by
     * {@link IExtensionContext#createOrder(ICreateOrderRequest)}.
     */
    String getOrderTransactionId();
}
