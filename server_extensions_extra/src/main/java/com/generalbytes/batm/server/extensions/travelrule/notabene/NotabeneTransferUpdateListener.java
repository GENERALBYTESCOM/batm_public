package com.generalbytes.batm.server.extensions.travelrule.notabene;


import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;

/**
 * Functional interface for listening to updates about Notabene transfers.
 *
 * <p>Implementations of this interface can be registered with a {@link NotabeneTransferPublisher} to
 * receive notifications whenever a transfer update event occurs.</p>
 *
 * @see NotabeneTransferPublisher
 * @see NotabeneTransferInfo
 */
@FunctionalInterface
public interface NotabeneTransferUpdateListener {

    /**
     * Invoked when a Notabene transfer update event occurs.
     *
     * @param updatedTransferInfo A {@link NotabeneTransferInfo} containing details about the updated transfer.
     */
    void onTransferUpdate(NotabeneTransferInfo updatedTransferInfo);

}
