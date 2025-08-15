package com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * An object representing the IVMS101 object in {@link GtrIvms101Payload}.
 */
@Getter
@Setter
public class GtrIvms101 {
    @JsonProperty("Originator")
    private GtrOriginator originator;
    @JsonProperty("Beneficiary")
    private GtrBeneficiary beneficiary;
}
