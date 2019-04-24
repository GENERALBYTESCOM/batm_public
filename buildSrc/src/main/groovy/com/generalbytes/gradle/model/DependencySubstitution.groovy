package com.generalbytes.gradle.model

import java.util.stream.Collectors

class DependencySubstitution {
    final SimpleModuleIdentifier fromIdentifier
    final Set<VersionNumberEntry> versions
    final VersionNumberEntry toVersion

    DependencySubstitution(String group, String name, Set<VersionNumberEntry> versions, VersionNumberEntry toVersion) {
        if (group == null) {
            throw new IllegalArgumentException("Group can't be null.")
        }
        if (name == null) {
            throw new IllegalArgumentException("Name can't be null.")
        }
        if (versions == null) {
            throw new IllegalArgumentException("Versions can't be null.")
        }
        if (toVersion == null) {
            throw new IllegalArgumentException("ToVersion can't be null.")
        }

        this.fromIdentifier = new SimpleModuleIdentifier(group, name)
        this.versions = versions
        this.toVersion = toVersion
    }

    @Override
    String toString() {
        "dependency substitution: ${parameters()}"
    }

    private String parameters() {
        final String versionsString = versions
            .stream()
            .sorted(Comparator.comparing({ it.number }))
            .map({ "'${it.string}'" })
            .collect(Collectors.toList())
            .join(', ')
        "module: '${fromIdentifier}', versions: [$versionsString], toVersion: '${toVersion.string}'"
    }

    String getDefinition() {
        "substitute ${parameters()}"
    }
}
