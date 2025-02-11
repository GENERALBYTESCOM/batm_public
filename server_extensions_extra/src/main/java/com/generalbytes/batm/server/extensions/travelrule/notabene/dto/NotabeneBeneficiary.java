package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * The beneficiary is defined as the natural or legal person or legal arrangement
 * who is identified by the originator as the receiver of the requested VA transfer.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class NotabeneBeneficiary {

    /**
     * The natural or legal person or legal arrangement who is identified by the originator
     * as the receiver of the requested VA transfer.
     *
     * @see NotabenePerson
     */
    private List<NotabenePerson> beneficiaryPersons;
    /**
     * Identifier of an account that is used to process the transfer.
     * The value for this element is case-sensitive.
     */
    private List<String> accountNumber;

}
