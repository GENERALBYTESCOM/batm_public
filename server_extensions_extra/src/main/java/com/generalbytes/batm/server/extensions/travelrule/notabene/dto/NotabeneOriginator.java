package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * The originator is defined as the account holder who allows the VA transfer from that account or,
 * where there is no account, the natural or legal person that places the order with the originating VASP
 * to perform the VA transfer.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class NotabeneOriginator {

    /**
     * The account holder who allows the VA transfer from that account or, where there is no account,
     * the natural or legal person that places the order with the originating VASP to perform the VA transfer.
     *
     * @see NotabenePerson
     */
    private List<NotabenePerson> originatorPersons;
    /**
     * Identifier of an account that is used to process the transfer.
     * The value for this element is case-sensitive.
     */
    private List<String> accountNumber;

}
