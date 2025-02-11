package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request to update an existing transfer.
 *
 * @see <a href="https://devx.notabene.id/reference/txupdate-1">Notabene Documentation</a>
 * @see NotabeneTransferInfo
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class NotabeneTransferUpdateRequest {

    /**
     * Identifier of the transfer. Returned by Notabene on create.
     */
    private String id;
    /**
     * The blockchain transaction hash.
     */
    private String txHash;

}
