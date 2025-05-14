package com.generalbytes.batm.server.extensions.travelrule;

/**
 * This event is responsible for holding information about an incoming transfer.
 *
 * <p>This is passed to {@link ITravelRuleTransferListener#onIncomingTransferReceived(ITravelRuleIncomingTransferEvent)}
 * whenever an incoming transfer received.</p>
 */
public interface ITravelRuleIncomingTransferEvent {

    /**
     * Data about the provider with which the VASP is registered. (mandatory)
     *
     * @return {@link ITravelRuleProviderIdentification} containing data about Travel Rule provider.
     * @see #getOriginatorVasp()
     */
    ITravelRuleProviderIdentification getTravelRuleProvider();

    /**
     * Unique identifier of the incoming transfer. (mandatory)
     *
     * @return Unique identifier of the transfer.
     */
    String getId();

    /**
     * Information about the originator VASP that requires transfer verification. (mandatory)
     *
     * @return {@link ITravelRuleVasp} containing data about originator VASP.
     */
    ITravelRuleVasp getOriginatorVasp();

    /**
     * Name data about the originator. (mandatory)
     *
     * @return {@link ITravelRuleNaturalPersonName} containing name data about originator.
     */
    ITravelRuleNaturalPersonName getOriginatorName();

    /**
     * Crypto address to verify if it belongs to the beneficiary VASP. (mandatory)
     *
     * @return Crypto address to verify.
     */
    String getDestinationAddress();

    /**
     * A request in a raw format based on which the operator can evaluate the transfer if something fails. (mandatory)
     *
     * @return Request in a raw format.
     */
    String getRawData();

}
