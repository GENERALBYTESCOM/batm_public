package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApplicantAddress extends JsonObject {
    private String subStreet;
    private String subStreetEn;
    private String street;
    private String streetEn;
    private String state;
    private String stateEn;
    private String town;
    private String townEn;
    private String postCode;
    private String country;
    private String formattedAddress;
}
