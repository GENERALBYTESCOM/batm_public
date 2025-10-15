package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Object containing info about Sumsub institution.
 * Used in {@link SumsubIdentity}.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubInstitutionInfo {
    /**
     * VASP DID. When used in {@link SumsubCounterparty}: if {@code null}, Sumsub will try to find the VASP based on the provided address.
     * If it doesn't find it, it will evaluate the wallet as UNHOSTED.
     *
     * @see <a href="https://docs.sumsub.com/reference/submit-transaction-for-non-existing-applicant">Sumsub documentation</a>
     */
    private String internalId;
    /**
     * VASP name.
     */
    private String name;
}
