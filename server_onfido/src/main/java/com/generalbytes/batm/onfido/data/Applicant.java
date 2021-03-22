package com.generalbytes.batm.onfido.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class Applicant {
    private String applicantId;
    private String sdkToken;
    private String serverUrl;
    private Instant dateAdded;
}
