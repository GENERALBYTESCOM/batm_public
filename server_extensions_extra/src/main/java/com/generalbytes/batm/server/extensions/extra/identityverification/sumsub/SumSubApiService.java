package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.ISumSubApi;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantIdResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantInfoResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.CreateIdentityApplicantRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.CreateIdentityVerificationSessionResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectionInfoResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;

/**
 * This service provides an abstraction for interacting with the SumSub API.
 * It handles operations related to identity verifications, applicants, and inspections.
 */
@AllArgsConstructor
public class SumSubApiService {
    private final ISumSubApi api;
    private final String levelName;
    private final int linkExpiryInSeconds;

    public CreateIdentityVerificationSessionResponse createSession(String identityPublicId, String customerLanguage) throws IOException {
        return api.createSession(levelName, linkExpiryInSeconds, identityPublicId, customerLanguage);
    }

    public ApplicantIdResponse createApplicant(String identityPublicId) throws IOException {
        return api.createApplicant(new CreateIdentityApplicantRequest(identityPublicId), levelName);
    }

    public ApplicantInfoResponse getApplicantByExternalId(String identityPublicId) throws IOException {
        return api.getApplicantByExternalId(identityPublicId);
    }

    public InspectionInfoResponse getInspectionInfo(String inspectionId) throws IOException {
        return api.getInspectionInfo(inspectionId);
    }

    public String getStartingLevelName() {
        return levelName;
    }
}
