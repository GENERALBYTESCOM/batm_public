package com.generalbytes.batm.server.extensions.travelrule;

/**
 * An object containing data with the results of evaluating an incoming transfer.
 *
 * <p>This is returned in {@link ITravelRuleTransferListener#onIncomingTransferReceived(ITravelRuleIncomingTransferEvent)}
 * after the incoming transfer is processed by the server.</p>
 */
public interface ITravelRuleIncomingTransferResult {

    /**
     * Resulting status of processing an incoming transaction.
     *
     * @return Status of processing an incoming transaction.
     */
    TravelRuleProviderTransferStatus getTransferStatus();

}
