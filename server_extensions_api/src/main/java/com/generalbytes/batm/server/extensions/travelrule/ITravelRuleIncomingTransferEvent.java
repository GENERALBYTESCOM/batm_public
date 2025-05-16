package com.generalbytes.batm.server.extensions.travelrule;

/**
 * This event is responsible for holding information about an incoming transfer.
 *
 * <p>This is passed to {@link ITravelRuleTransferListener#onIncomingTransferReceived(ITravelRuleIncomingTransferEvent)}
 * whenever an incoming transfer received.</p>
 */
public interface ITravelRuleIncomingTransferEvent {

    /**
     * Unique identifier of the incoming transfer. (mandatory)
     *
     * @return Unique identifier of the transfer.
     */
    String getId();

    /**
     * Retrieves the DID of the VASP for which the incoming transfer is intended.
     *
     * @return The DID of the beneficiary VASP.
     */
    String getBeneficiaryVaspDid();

    /**
     * Information about the originator VASP that requires transfer verification. (mandatory)
     *
     * @return {@link ITravelRuleVasp} containing DID of originator VASP, name is not set will return {@code null}
     */
    ITravelRuleVasp getOriginatorVasp();

    /**
     * Name data about the originator. (mandatory)
     *
     * @return {@link ITravelRuleNaturalPersonName} containing name data about originator.
     */
    ITravelRuleNaturalPersonName getOriginatorName();

    /**
     * Name data about the beneficiary. (mandatory)
     *
     * @return {@link ITravelRuleNaturalPersonName} containing name data about originator.
     */
    ITravelRuleNaturalPersonName getBeneficiaryName();

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
