package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * The particulars of a location at which a person may be communicated with.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class NotabeneGeographicAddress {

    /**
     * Identifies the nature of the address.
     */
    private NotabeneGeographicAddressType addressType;
    private String department;
    private String subDepartment;
    private String streetName;
    private String buildingNumber;
    private String buildingName;
    private String floor;
    private String postBox;
    private String room;
    private String postCode;
    private String townName;
    private String townLocationName;
    private String districtName;
    private String countrySubDivision;
    /**
     * Information that locates and identifies a specific address,
     * as defined by postal services, presented in free format text.
     */
    private List<String> addressLine;
    /**
     * Two alphabetic characters representing an ISO-3166 Alpha-2 country,
     * including the code ‘XX’ to represent an indicator for unknown States,
     * other entities or organisations
     */
    private String country;

}
