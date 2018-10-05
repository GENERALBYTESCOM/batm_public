package com.generalbytes.gradle.model

import groovy.transform.EqualsAndHashCode
import org.gradle.api.artifacts.ModuleIdentifier

import java.util.regex.Matcher
import java.util.regex.Pattern

@EqualsAndHashCode
class ChecksumAssertion implements Comparable<ChecksumAssertion> {
    final ModuleIdentifier artifactIdentifier
    final String checksum

    ChecksumAssertion(ModuleIdentifier artifactIdentifier, String checksum) {
        this.artifactIdentifier = artifactIdentifier
        this.checksum = checksum
    }

    ChecksumAssertion(String artifactIdentifier, String checksum) {
        this.artifactIdentifier = new SimpleModuleIdentifier(artifactIdentifier)
        this.checksum = checksum
    }



    ChecksumAssertion(String s) {
        final Matcher matcher = Pattern.compile('^([^:]*):([^:]*):([^:]*)$').matcher(s)
        if (!matcher.matches()) {
            def msg = "Module identifier '$s' has incorrect format."
            throw new IllegalArgumentException(msg)
        }
        this.artifactIdentifier = new SimpleModuleIdentifier(matcher.group(1), matcher.group(2))
        this.checksum = matcher.group(3)
    }

    @Override
    String toString() {
        "$artifactIdentifier:$checksum"
    }

    String displayName() {
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
