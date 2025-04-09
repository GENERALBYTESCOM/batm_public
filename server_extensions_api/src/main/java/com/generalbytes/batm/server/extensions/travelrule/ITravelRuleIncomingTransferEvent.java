package com.generalbytes.batm.server.extensions.travelrule;

/**
 * This event is responsible for holding information about an incoming transfer.
 *
 * <p>This is passed to {@link ITravelRuleTransferListener#onIncomingTransferReceived(ITravelRuleIncomingTransferEvent)}
 * whenever an incoming transfer received.</p>
 */
public interface ITravelRuleIncomingTransferEvent {

    /**
     * Information about the originator VASP that requires transfer verification.
     *
     * @return {@link ITravelRuleVasp}
     */
    ITravelRuleVasp getOriginatorVasp();

    /**
     * Crypto address to verify if it belongs to the beneficiary VASP.
     *
     * @return Crypto address to verify.
     */
    String getDestinationAddress();

    /**
     * A request in a raw format based on which the operator can evaluate the transfer if something fails.
     *
     * @return Request in a raw format.
     */
    String getRawData();

}
