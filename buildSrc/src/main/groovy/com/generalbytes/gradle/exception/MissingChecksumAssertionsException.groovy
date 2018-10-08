package com.generalbytes.gradle.exception

import com.generalbytes.gradle.model.ChecksumAssertion

class MissingChecksumAssertionsException extends IllegalStateException {
    private final Set<ChecksumAssertion> missingAssertions = new HashSet<>()

    MissingChecksumAssertionsException(String msg, ChecksumAssertion missingAssertion) {
        super(msg)
        missingAssertions.add(missingAssertion)
    }

    MissingChecksumAssertionsException(String msg, Set<ChecksumAssertion> assertion) {
        super(msg)
        missingAssertions.addAll(assertion)
    }

    Set<ChecksumAssertion> getMissingAssertions() {
        return missingAssertions.asImmutable()
    }
}