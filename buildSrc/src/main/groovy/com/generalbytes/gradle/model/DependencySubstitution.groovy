package com.generalbytes.gradle.model

import groovy.transform.EqualsAndHashCode

import java.util.stream.Collectors

@EqualsAndHashCode
class DependencySubstitution implements Comparable<DependencySubstitution> {
    final SimpleModuleIdentifier fromIdentifier
    final Set<VersionNumberEntry> versions
    final VersionNumberEntry toVersion

    DependencySubstitution(
        SimpleModuleIdentifier fromIdentifier,
        Set<VersionNumberEntry> versions,
        VersionNumberEntry toVersion
    ) {
        if (fromIdentifier == null) {
            throw new IllegalArgumentException("fromIdentifier can't be null.")
        }
        if (fromIdentifier.group == null) {
            throw new IllegalArgumentException("fromIdentifier group can't be null.")
        }
        if (fromIdentifier.name == null) {
            throw new IllegalArgumentException("fromIdentifier name can't be null.")
        }
        if (toVersion == null) {
            throw new IllegalArgumentException("toVersion can't be null.")
        }

        this.fromIdentifier = fromIdentifier
        this.toVersion = toVersion
        this.versions = versions.asImmutable()

        if (this.versions == null) {
            throw new IllegalArgumentException("Versions can't be null.")
        }
        if (this.versions.isEmpty()) {
            throw new IllegalArgumentException("Versions can't be empty.")
        }
    }


    DependencySubstitution(String group, String name, Set<VersionNumberEntry> versions, VersionNumberEntry toVersion) {
        this(new SimpleModuleIdentifier(group, name), versions, toVersion)
    }

    boolean isNewestWins() {
        return versionsMax() <= toVersion
    }

    VersionNumberEntry versionsMax() {
        versions.stream().max(Comparator.naturalOrder()).get()
    }

    String displayName() {
        "dependency substitution: $this"
    }

    @Override
    String toString() {
        final String versionsString = versions
            .stream()
            .sorted(Comparator.naturalOrder())
            .map({ "'${it.string}'" })
            .collect(Collectors.toList())
            .join(', ')
        "module: '${fromIdentifier}', versions: [$versionsString], toVersion: '${toVersion.string}'"
    }

    String getDefinition() {
        "substitute $this"
    }

    @Override
    int compareTo(DependencySubstitution o) {
        toString().compareTo(o.toString())
    }
}
