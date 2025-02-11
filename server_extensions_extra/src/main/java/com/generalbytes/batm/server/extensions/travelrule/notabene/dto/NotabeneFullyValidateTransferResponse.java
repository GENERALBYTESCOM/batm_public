package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Response from fully validating a transfer.
 *
 * @see <a href="https://devx.notabene.id/reference/txvalidatefull-1">Notabene Documentation</a>
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class NotabeneFullyValidateTransferResponse {

    /**
     * Is the transfer valid?
     */
    private boolean isValid;
    /**
     * Type of the transfer.
     */
    private NotabeneTransferType type;
    /**
     * Type of the Beneficiary's Blockchain Address.
     */
    private NotabeneCryptoAddressType beneficiaryAddressType;
    /**
     * Name of the Beneficiary's VASP.
     */
    @JsonProperty("beneficiaryVASPname")
    private String beneficiaryVaspName;
    private List<String> errors;
    private List<String> warnings;

}
