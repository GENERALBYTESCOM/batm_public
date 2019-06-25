package com.generalbytes.gradle.model

class SuggestedSubstitutionChanges {
    Map<SimpleModuleIdentifier, DependencySubstitution> addSuggestionsByModule
    Map<SimpleModuleIdentifier, DependencySubstitution> modifySuggestionsByModule
    Map<SimpleModuleIdentifier, DependencySubstitution> removalSuggestionsByModule
    Map<SimpleModuleIdentifier, DependencySubstitution> originalSubstitutions

    private SuggestedSubstitutionChanges(
        Map<SimpleModuleIdentifier, DependencySubstitution> addSuggestionsByModule,
        Map<SimpleModuleIdentifier, DependencySubstitution> modifySuggestionsByModule,
        Map<SimpleModuleIdentifier, DependencySubstitution> removalSuggestionsByModule,
        Map<SimpleModuleIdentifier, DependencySubstitution> originalSubstitutions
    ) {
        Objects.requireNonNull(addSuggestionsByModule, "addSuggestions can't be null.")
        Objects.requireNonNull(modifySuggestionsByModule, "modifySuggestions can't be null.")
        Objects.requireNonNull(removalSuggestionsByModule, "removalSuggestions can't be null.")
        Objects.requireNonNull(originalSubstitutions, "originalSubstitutions can't be null.")

        this.addSuggestionsByModule = addSuggestionsByModule.asImmutable()
        this.modifySuggestionsByModule = modifySuggestionsByModule.asImmutable()
        this.removalSuggestionsByModule = removalSuggestionsByModule.asImmutable()
        this.originalSubstitutions = originalSubstitutions.asImmutable()
    }

    static SuggestedSubstitutionChanges from(
        Map<SimpleModuleIdentifier, DependencySubstitution> originalSubstitutions,
        Map<SimpleModuleVersionIdentifier, VersionNumberEntry> unknownSubstitutions,
        Map<SimpleModuleVersionIdentifier, VersionNumberEntry> usedSubstitutions
    ) {

        final Map<SimpleModuleIdentifier, DependencySubstitution> modifySuggestionsByModule =
            computeModifySuggestions(
                originalSubstitutions,
                usedSubstitutions,
                unknownSubstitutions
            )

        final Map<SimpleModuleIdentifier, DependencySubstitution> addSuggestionsByModule =
            computeAddSuggestions(
                originalSubstitutions,
                unknownSubstitutions
            )

        final Map<SimpleModuleIdentifier, DependencySubstitution> removalSuggestionsByModule =
            computeRemovalSuggestions(
                originalSubstitutions,
                usedSubstitutions
            )

        return new SuggestedSubstitutionChanges(
            addSuggestionsByModule,
            modifySuggestionsByModule,
            removalSuggestionsByModule,
            originalSubstitutions
        )

    }

    static Map<SimpleModuleIdentifier, DependencySubstitution> computeModifySuggestions(
        Map<SimpleModuleIdentifier, DependencySubstitution> originalSubstitutions,
        Map<SimpleModuleVersionIdentifier, VersionNumberEntry> usedSubstitutions,
        Map<SimpleModuleVersionIdentifier, VersionNumberEntry> unknownSubstitutions
    ) {

        final Map<SimpleModuleIdentifier, DependencySubstitution> mergedUsedSubstitutionsByModule = new HashMap<>()
        usedSubstitutions.each { from, toVersion ->
            final DependencySubstitution originalSubstitution = originalSubstitutions.get(from.moduleIdentifier)
            if (!originalSubstitution.toVersion.equals(toVersion)) {
                throw new IllegalArgumentException("Original substitution target version doesn't match substitution.")
            }

            final DependencySubstitution modifySuggestion = mergedUsedSubstitutionsByModule.get(from.moduleIdentifier)
            if (modifySuggestion != null && !modifySuggestion.toVersion.equals(toVersion)) {
                throw new IllegalArgumentException("Modification suggestion target version doesn't match substitution.")
            }

            mergedUsedSubstitutionsByModule.compute(
                from.moduleIdentifier,
                { key, baseSubstitution ->
                    final VersionNumberEntry fromVersion = VersionNumberEntry.parse(from.version)
                    if (baseSubstitution == null) {
                        return stripPluginSubstitution(originalSubstitution, fromVersion)
                    } else if (baseSubstitution.versions.contains(fromVersion)) {
                        return baseSubstitution
                    } else {
                        return new DependencySubstitution(
                            baseSubstitution.fromIdentifier,
                            baseSubstitution.versions + fromVersion,
                            baseSubstitution.toVersion
                        )
                    }

                }
            )

        }

        unknownSubstitutions.each { from, toVersion ->
            final DependencySubstitution originalSubstitution = originalSubstitutions.get(from.moduleIdentifier)

            if (originalSubstitution != null) {
                mergedUsedSubstitutionsByModule.compute(
                    from.moduleIdentifier,
                    { key, baseSubstitution ->
                        final VersionNumberEntry fromVersion = VersionNumberEntry.parse(from.version)
                        if (baseSubstitution == null) {
                            // if we got here, the module has no USED substitutions, but still has a substitution
                            // specified
                            return stripPluginSubstitution(originalSubstitution, fromVersion)
                        } else {
                            return substitutionForConflict(
                                from,
                                new SimpleModuleVersionIdentifier(from.moduleIdentifier, toVersion.string),
                                baseSubstitution
                            )
                        }
                    }
                )
            }

        }
        return mergedUsedSubstitutionsByModule - originalSubstitutions
    }

    static Map<SimpleModuleIdentifier, DependencySubstitution> computeAddSuggestions(
        Map<SimpleModuleIdentifier, DependencySubstitution> originalSubstitutions,
        Map<SimpleModuleVersionIdentifier, VersionNumberEntry> unknownSubstitution
    ) {
        final Map<SimpleModuleIdentifier, DependencySubstitution> addSuggestionsByModule = new HashMap<>()

        unknownSubstitution.each { from, toVersion ->
            final DependencySubstitution originalSubstitution = originalSubstitutions.get(from.moduleIdentifier)
            if (originalSubstitution == null) {
                final SimpleModuleVersionIdentifier selected =
                    new SimpleModuleVersionIdentifier(from.moduleIdentifier, toVersion.string)
                addSuggestionsByModule.compute(
                    from.moduleIdentifier,
                    { key, baseSubstitution ->
                        return substitutionForConflict(from, selected, baseSubstitution)
                    }
                )

            }
        }

        return addSuggestionsByModule
    }

    static Map<SimpleModuleIdentifier, DependencySubstitution> computeRemovalSuggestions(
        Map<SimpleModuleIdentifier, DependencySubstitution> originalSubstitutions,
        Map<SimpleModuleVersionIdentifier, VersionNumberEntry> usedSubstitutions
    ) {
        final Map<SimpleModuleIdentifier, DependencySubstitution> removalSuggestionsByModule = new HashMap<>()

        originalSubstitutions.each { from, originalSubstitution ->
            boolean shouldRemove =
                originalSubstitution.versions
                    .stream()
                    .noneMatch({
                        usedSubstitutions.containsKey(new SimpleModuleVersionIdentifier(from, it.string))
                    })

            if (shouldRemove) {
                removalSuggestionsByModule.put(from, originalSubstitution)
            }
        }

        return removalSuggestionsByModule

    }

    private static DependencySubstitution substitutionForConflict(
        SimpleModuleVersionIdentifier requested,
        SimpleModuleVersionIdentifier selected,
        DependencySubstitution baseSubstitution
    ) {
        if (baseSubstitution == null) {
            return createSimpleSubstitution(requested, selected)
        } else {
            final VersionNumberEntry requestedVersion = VersionNumberEntry.parse(requested.version)
            final VersionNumberEntry selectedVersion = VersionNumberEntry.parse(selected.version)
            final Set<VersionNumberEntry> unknownVersions = getUnknownVersions(
                baseSubstitution,
                [requestedVersion, selectedVersion]
            )

            if (unknownVersions.isEmpty()) {
                return baseSubstitution
            }

            final Set<VersionNumberEntry> allVersions = new HashSet<>(baseSubstitution.versions)
            allVersions.add(baseSubstitution.toVersion)
            allVersions.add(requestedVersion)
            allVersions.add(selectedVersion)

            final VersionNumberEntry newToVersion = baseSubstitution.isNewestWins()
                ? allVersions.stream().max(Comparator.naturalOrder()).get()
                : baseSubstitution.toVersion

            return new DependencySubstitution(
                requested.group,
                requested.module,
                allVersions - newToVersion,
                newToVersion
            )
        }
    }

    private static Set<VersionNumberEntry> getUnknownVersions(
        DependencySubstitution substitution,
        Collection<VersionNumberEntry> versions
    ) {
        final Set<VersionNumberEntry> ret = new HashSet<>()
        for (VersionNumberEntry version : versions) {
            if (!substitution.versions.contains(version) && !substitution.toVersion.equals(version)) {
                ret.add(version)
            }
        }
        return ret
    }

    private static DependencySubstitution createSimpleSubstitution(
        SimpleModuleVersionIdentifier from,
        SimpleModuleVersionIdentifier to
    ) {
        new DependencySubstitution(
            from.group,
            from.module,
            [VersionNumberEntry.parse(from.version)] as Set,
            VersionNumberEntry.parse(to.version)
        )
    }

    static DependencySubstitution stripPluginSubstitution(
        DependencySubstitution pluginSubstitution,
        VersionNumberEntry requestedVersion
    ) {
        final Set<VersionNumberEntry> versions = [requestedVersion]
        if (!pluginSubstitution.isNewestWins()) {
            versions.add(pluginSubstitution.versionsMax())
        }
        return new DependencySubstitution(
            pluginSubstitution.fromIdentifier,
            versions,
            pluginSubstitution.toVersion
        )
    }

}
