package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Information about a transaction on the blockchain
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@ToString
public class NotabeneTransactionBlockchainInfo {

    /**
     * Transaction txHash.
     */
    private String txHash;
    /**
     * Originator crypto address.
     */
    private String origin;
    /**
     * Destination crypto address.
     */
    private String destination;

}
