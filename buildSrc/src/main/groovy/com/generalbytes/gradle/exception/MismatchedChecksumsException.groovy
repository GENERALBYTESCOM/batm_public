package com.generalbytes.gradle.exception

import com.generalbytes.gradle.model.ChecksumAssertion

class MismatchedChecksumsException extends IllegalStateException {
    private final Map<ChecksumAssertion, String> mismatchedChecksumsByAssertion = new HashMap<>()

    MismatchedChecksumsException(String msg, Map<ChecksumAssertion, String> mismatchedChecksumsByAssertion) {
        super(msg)
        this.mismatchedChecksumsByAssertion.putAll(mismatchedChecksumsByAssertion)
    }

    MismatchedChecksumsException(String msg, ChecksumAssertion assertion, String actualChecksum) {
        super(msg)
        this.mismatchedChecksumsByAssertion.put(assertion, actualChecksum)
    }

    Map<ChecksumAssertion, String> getMismatchedChecksumsByAssertion() {
        return mismatchedChecksumsByAssertion.asImmutable()
    }
}