package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response with ownership information about a customer blockchain address.
 *
 * @see <a href="https://devx.notabene.id/reference/addressownershipget">Notabene Documentation</a>
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotabeneAddressOwnershipInfoResponse {

    @JsonProperty("owner_vasp_did")
    private String ownerVaspDid;
    @JsonProperty("address_type")
    private NotabeneCryptoAddressType addressType;

}
