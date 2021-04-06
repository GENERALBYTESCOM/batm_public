package com.generalbytes.verification.service;

import com.generalbytes.verification.controller.RegisterNewApplicantReq;
import com.generalbytes.verification.data.Applicant;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class ApplicantService {

    private static final Logger log = LoggerFactory.getLogger(ApplicantService.class);

    private final Cache<String, Applicant> CACHE =  CacheBuilder.newBuilder()
        .expireAfterWrite(90 * 60L, TimeUnit.SECONDS) // every SDK token is valid only for 90 minutes
        .build();

    public void addApplicant(RegisterNewApplicantReq req) {
        log.info("Adding applicant {} to cache", req.getApplicantId());
        CACHE.put(req.getApplicantId(), new Applicant(req.getApplicantId(), req.getSdkToken(), req.getServerUrl(), Instant.now()));
    }

    public Applicant get(String applicantId) {
        return CACHE.getIfPresent(applicantId);
    }
}
