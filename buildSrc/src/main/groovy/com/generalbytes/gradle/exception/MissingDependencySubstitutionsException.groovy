package com.generalbytes.gradle.exception

import com.generalbytes.gradle.model.DependencySubstitution

class MissingDependencySubstitutionsException extends RuntimeException {

    MissingDependencySubstitutionsException(String msg) {
        super(msg)
    }

    Set<DependencySubstitution> getMissingSubstitutions() {
        return missingSubstitutions.asImmutable()
    }
}
