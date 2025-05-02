package com.generalbytes.batm.server.extensions.travelrule;

/**
 * Incoming transfer data from the Travel Rule provider containing information needed to automatically evaluate the transfer status.
 */
public interface ITravelRuleIncomingTransferData {

    /**
     * @return Destination crypto address.
     */
    String getDestinationAddress();

    /**
     * @return Object containing information about originator name.
     */
    ITravelRuleNaturalPersonName getOriginatorName();

    /**
     * @return Raw data received in an incoming transfer.
     */
    String getRawData();

}
