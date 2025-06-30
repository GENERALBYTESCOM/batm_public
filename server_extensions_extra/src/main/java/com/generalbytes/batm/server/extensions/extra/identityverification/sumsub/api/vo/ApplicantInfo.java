package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ApplicantInfo extends JsonObject {
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dob;
    private String country;
    private String phone;
    private List<ApplicantDocument> idDocs;
    private List<ApplicantAddress> addresses;
}
