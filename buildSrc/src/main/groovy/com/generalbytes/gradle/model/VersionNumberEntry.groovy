package com.generalbytes.gradle.model

import groovy.transform.EqualsAndHashCode
import org.gradle.util.VersionNumber

@EqualsAndHashCode
final class VersionNumberEntry implements Comparable<VersionNumberEntry> {
    final VersionNumber number;
    final String string;

    VersionNumberEntry(String versionString) {
        if (versionString == null || versionString.isEmpty()) {
            throw new IllegalArgumentException("Invalid version: '$versionString'.")
        }
        this.string = versionString
        this.number = VersionNumber.parse(this.string)
    }

    static VersionNumberEntry parse(String s) {
        return new VersionNumberEntry(s)
    }

    @Override
    String toString() {
        return "$number"
    }

    @Override
    int compareTo(VersionNumberEntry o) {
        return this.number.compareTo(o.number)
    }
}
