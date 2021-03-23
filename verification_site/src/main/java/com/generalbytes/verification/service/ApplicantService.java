package com.generalbytes.verification.service;

import com.generalbytes.verification.controller.RegisterNewApplicantReq;
import com.generalbytes.verification.data.Applicant;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class ApplicantService {

    private static final Long TIMEOUT_DURATION_SECONDS = 90 * 60L; // every SDK token is valid only for 90 minutes
    private final ConcurrentHashMap<String, Applicant> CACHE = new ConcurrentHashMap<>();

    public void addApplicant(RegisterNewApplicantReq req) {
        evict();
        CACHE.put(req.getApplicantId(), new Applicant(req.getApplicantId(), req.getSdkToken(), req.getServerUrl(), Instant.now()));
    }

    public Applicant get(String applicantId) {
        evict();
        return CACHE.get(applicantId);
    }

    private void evict() {
        CACHE.values().forEach(val -> {
            if (val.getDateAdded().plus(TIMEOUT_DURATION_SECONDS, SECONDS).isBefore(Instant.now())) {
                CACHE.remove(val.getApplicantId());
            }
        });
    }
}
