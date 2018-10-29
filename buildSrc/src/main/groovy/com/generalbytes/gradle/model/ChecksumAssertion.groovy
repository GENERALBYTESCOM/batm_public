package com.generalbytes.gradle.model

import groovy.transform.EqualsAndHashCode
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

import java.util.regex.Matcher
import java.util.regex.Pattern

@EqualsAndHashCode
class ChecksumAssertion implements Comparable<ChecksumAssertion> {
    private static final PATTERN = Pattern.compile('^([^:]*):([^:]*):([^:]*(-SNAPSHOT:[^:]*)?)(:([^:]*))?:([^:]*)$')

    final ModuleComponentIdentifier artifactIdentifier
    final String checksum

    ChecksumAssertion(ModuleComponentIdentifier artifactIdentifier, String checksum) {
        this.artifactIdentifier = artifactIdentifier
        this.checksum = checksum
    }

    ChecksumAssertion(String artifactIdentifier, String checksum) {
        this.artifactIdentifier = new SimpleModuleVersionIdentifier(artifactIdentifier)
        this.checksum = checksum
    }


    ChecksumAssertion(String s) {
        /**
         * group:module:version:checksum or group:module:version:classifier:checksum
         * in case of using snapshot versions, the version string can become something like
         * '1.0.6-SNAPSHOT:20180927.101917-1' (i.e. can contain another colon)
         */
        final Matcher matcher = PATTERN.matcher(s)
        if (!matcher.matches()) {
            def msg = "Assertion definition '$s' has incorrect format."
            throw new IllegalArgumentException(msg)
        }
        this.artifactIdentifier = new SimpleModuleVersionIdentifier(
            matcher.group(1),
            matcher.group(2),
            matcher.group(3),
            matcher.group(6)
        )
        this.checksum = matcher.group(7)
    }

    @Override
    String toString() {
        "$artifactIdentifier:$checksum"
    }

    String getDisplayName() {
        "assertion: '$this'"
    }

    String definition() {
        "verifyModule '$this'"
    }

    @Override
    int compareTo(ChecksumAssertion o) {
        toString().compareTo(o.toString())
    }
}
