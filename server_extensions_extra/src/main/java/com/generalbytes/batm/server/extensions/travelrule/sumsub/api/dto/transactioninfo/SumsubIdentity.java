package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class containing info about Sumsub identity.
 * Used in {@link SumsubApplicant} and {@link SumsubCounterparty}.
 */
@Getter
@Setter
public abstract class SumsubIdentity {
    private String type;
    private String externalUserId;
    private String firstName;
    private String lastName;
    private String fullName;
    private SumsubInstitutionInfo institutionInfo;
}
