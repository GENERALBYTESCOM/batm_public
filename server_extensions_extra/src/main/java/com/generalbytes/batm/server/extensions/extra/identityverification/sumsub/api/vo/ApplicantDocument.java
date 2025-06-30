package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import lombok.Getter;

import java.time.LocalDate;

// https://docs.sumsub.com/reference/get-applicant-data-via-externaluserid#iddocs-element-fields
@Getter
public class ApplicantDocument extends DocumentDefinition {
    private String firstName;
    private String lastName;
    private LocalDate issuedDate;
    private LocalDate validUntil;
    private String number;
    private LocalDate dob;
}
