package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import lombok.Getter;

/**
 * Object containing basic data about VASP.
 * Used in {@link GtrVaspListResponse}.
 */
@Getter
public class GtrVaspBasicInfo {
    private String vaspCode;
    private String vaspName;
    private String publicKey;
    private String companyName;
    private String allianceName;
    private String registrationAuthorityCountry;
}
