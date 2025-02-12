package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class NotabeneNationalIdentification {

    private String nationalIdentifier;
    private NotabeneNationalIdentifierType nationalIdentifierType;
    /**
     * Two alphabetic characters representing an ISO-3166 Alpha-2 country,
     * including the code ‘XX’ to represent an indicator for unknown States,
     * other entities or organisations
     */
    private String countryOfIssue;
    private String registrationAuthority;

}
