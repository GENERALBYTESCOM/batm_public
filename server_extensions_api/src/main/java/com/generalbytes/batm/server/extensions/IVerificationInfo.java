package com.generalbytes.batm.server.extensions;

public interface IVerificationInfo {

    boolean isSuccess();
    String getErrorMessage();
    String getApplicantId();
    String getVerificationWebUrl();
}
