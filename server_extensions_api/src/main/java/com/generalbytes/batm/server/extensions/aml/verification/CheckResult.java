package com.generalbytes.batm.server.extensions.aml.verification;

import java.util.HashSet;
import java.util.Set;

/**
 * Identity verification result returned by a provider.
 * This enum values are provider agnostic but most of them originally taken from Onfido
 */

public enum CheckResult {
    // do not rename existing enum constants

    CLEAR,
    SUSPECTED,
    REJECTED,
    CAUTION,

    /**
     * Resubmission is requested by the provider.
     * The verification process is not completed, the client need to go through the verification once more. The same link should be used.
     */
    RESUBMISSION_REQUESTED,

    /**
     * The end user has not started or started but not completed the verification.
     */
    EXPIRED,

    REJECTED_AGE_VALIDATION,
    REJECTED_IMAGE_INTEGRITY,

    CAUTION_VISUAL_CONSISTENCY,
    CAUTION_IMAGE_INTEGRITY,
    CAUTION_DATA_COMPARISON,
    CAUTION_FACIAL_COMPARISON,
    CAUTION_DATA_VALIDATION,
    CAUTION_DATA_CONSISTENCY,

    SUSPECTED_COMPROMISED_DOCUMENT,
    SUSPECTED_POLICE_RECORD,
    SUSPECTED_DATA_CONSISTENCY,
    SUSPECTED_VISUAL_CONSISTENCY,
    SUSPECTED_DATA_VALIDATION,
    SUSPECTED_FACE_COMPARISON,

    UNKNOWN;


    private static final Set<CheckResult> CAUTION_VALUES= new HashSet<>();
    private static final Set<CheckResult> SUSPECTED_VALUES = new HashSet<>();
    private static final Set<CheckResult> REJECTED_VALUES = new HashSet<>();
    static {
        CAUTION_VALUES.add(CAUTION_VISUAL_CONSISTENCY);
        CAUTION_VALUES.add(CAUTION_IMAGE_INTEGRITY);
        CAUTION_VALUES.add(CAUTION_DATA_COMPARISON);
        CAUTION_VALUES.add(CAUTION_FACIAL_COMPARISON);
        CAUTION_VALUES.add(CAUTION_DATA_VALIDATION);
        CAUTION_VALUES.add(CAUTION_DATA_CONSISTENCY);
        CAUTION_VALUES.add(CAUTION);

        SUSPECTED_VALUES.add(SUSPECTED_COMPROMISED_DOCUMENT);
        SUSPECTED_VALUES.add(SUSPECTED_POLICE_RECORD);
        SUSPECTED_VALUES.add(SUSPECTED_DATA_CONSISTENCY);
        SUSPECTED_VALUES.add(SUSPECTED_VISUAL_CONSISTENCY);
        SUSPECTED_VALUES.add(SUSPECTED_DATA_VALIDATION);
        SUSPECTED_VALUES.add(SUSPECTED_FACE_COMPARISON);
        SUSPECTED_VALUES.add(SUSPECTED);

        REJECTED_VALUES.add(REJECTED);
        REJECTED_VALUES.add(REJECTED_IMAGE_INTEGRITY);
        REJECTED_VALUES.add(REJECTED_AGE_VALIDATION);
    }

    public boolean isCaution() {
        return CAUTION_VALUES.contains(this);
    }

    public boolean isSuspected() {
        return SUSPECTED_VALUES.contains(this);
    }

    public boolean isRejected() {
        return REJECTED_VALUES.contains(this);
    }
}
