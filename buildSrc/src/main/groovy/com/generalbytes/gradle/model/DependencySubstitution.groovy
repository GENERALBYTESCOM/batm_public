package com.generalbytes.gradle.model

import groovy.transform.EqualsAndHashCode

import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors

@EqualsAndHashCode
class DependencySubstitution implements Comparable<DependencySubstitution> {
    private static final Pattern SUBSTITUTION_PATTERN = Pattern.compile('^\\p{Blank}*substitute\\p{Blank}*module\\p{Blank}*:\\p{Blank}*\'([^\']*)\'\\p{Blank}*,\\p{Blank}*versions\\p{Blank}*:\\p{Blank}*\\[\\p{Blank}*(\'[^\']*\'(?:\\p{Blank}*,\\p{Blank}*\'[^\']*\')*)\\p{Blank}*]\\p{Blank}*,\\p{Blank}*toVersion\\p{Blank}*:\\p{Blank}*\'([^\']*)\'\\p{Blank}*(?:(?:#|//).*)?$')

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

    boolean matches(VersionNumberEntry from, VersionNumberEntry to) {
        versions.contains(from) && toVersion == to
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

    DependencySubstitution createSimilar(VersionNumberEntry requestedFromVersion, boolean keepNewestWinsInfo) {
        final Set<VersionNumberEntry> versions = [requestedFromVersion]
        if (keepNewestWinsInfo && !isNewestWins()) {
            versions.add(versionsMax())
        }
        return new DependencySubstitution(fromIdentifier, versions, toVersion)
    }

    static DependencySubstitution from(String str) {
        final Matcher substitutionMatcher = SUBSTITUTION_PATTERN.matcher(str)
        if (substitutionMatcher.matches()) {
            final String module = substitutionMatcher.group(1)
            final String versionsString = substitutionMatcher.group(2)
            final String toVersion = substitutionMatcher.group(3)

            final Set<String> versions = versionsString
                .split("[, ]")
                .toList()
                .stream()
                .filter({ !it.isEmpty() })
                .map({ it.replace("'", '') })
                .collect(Collectors.toSet())

            return from(module, versions, toVersion)
        } else {
            throw new IllegalStateException("Illegal line format.")
        }
    }

    static DependencySubstitution from(String module, Set<String> versions, String toVersion) {
        final String[] groupName = module.split(':')
        if (groupName.length != 2) {
            throw new IllegalArgumentException("Invalid module '$module'. Correct format is 'group:name'.")
        }
        final Set<String> localVersions = new HashSet<>(versions)
        if (localVersions.isEmpty()) {
            throw new IllegalArgumentException("Invalid versions specification (empty).")
        }
        final DependencySubstitution dependencySubstitution = new DependencySubstitution(
            groupName[0],
            groupName[1],
            localVersions.stream().map({ VersionNumberEntry.parse(it) }).collect(Collectors.toSet()),
            VersionNumberEntry.parse(toVersion)
        )
        dependencySubstitution
    }

}
