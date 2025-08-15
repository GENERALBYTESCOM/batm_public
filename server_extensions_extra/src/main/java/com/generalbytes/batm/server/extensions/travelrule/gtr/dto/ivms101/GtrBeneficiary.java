package com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * An object representing the Beneficiary object in {@link GtrIvms101}.
 */
@Getter
@Setter
public class GtrBeneficiary {
    private List<GtrPerson> beneficiaryPersons;
}
