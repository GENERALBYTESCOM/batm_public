package com.generalbytes.verification.controller;

import com.generalbytes.verification.data.Applicant;
import com.generalbytes.verification.service.ApplicantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping(value = "/verification")
public class VerificationController {

    private final ApplicantService applicantService;
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(VerificationController.class);

    public VerificationController(ApplicantService applicantService, RestTemplate restTemplate) {
        this.applicantService = applicantService;
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = "/register", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity register(@RequestBody RegisterNewApplicantReq req) {
        applicantService.addApplicant(req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/{applicant}")
    public ResponseEntity getSdkToken(@PathVariable("applicant") String applicantId) {
        Applicant applicant = applicantService.get(applicantId);
        if (applicant != null) {
            return ResponseEntity.ok(applicant.getSdkToken());
        }
        return ResponseEntity.status(NOT_FOUND).build();
    }

    @GetMapping("/submit/{applicant}")
    public ResponseEntity submitCheck(@PathVariable("applicant") String applicantId) {
        Applicant applicant = applicantService.get(applicantId);
        if (applicant != null) {
            return restTemplate.getForEntity(applicant.getServerUrl() + "/serverapi/apiv1/identity-check/submit/" + applicantId, String.class);
        } else {
            log.error("Applicant {} not found.", applicantId);
            return ResponseEntity.status(NOT_FOUND).build();
        }
    }
}
