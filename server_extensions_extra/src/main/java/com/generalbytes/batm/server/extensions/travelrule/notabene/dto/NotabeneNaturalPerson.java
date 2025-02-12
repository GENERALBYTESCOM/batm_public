package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Represents a natural (non-legal) person.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class NotabeneNaturalPerson {

    private List<NotabenePersonName> name;
    private List<NotabeneGeographicAddress> geographicAddress;
    private NotabeneNationalIdentification nationalIdentification;
    private String customerIdentification;
    private NotabeneDateAndPlaceOfBirth dateAndPlaceOfBirth;
    /**
     * Two alphabetic characters representing an ISO-3166 Alpha-2 country,
     * including the code ‘XX’ to represent an indicator for unknown States,
     * other entities or organisations
     */
    private String countryOfResidence;

}
