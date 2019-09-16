package com.generalbytes.gradle.model

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class DependencySubstitutionContext {
    final ConcurrentMap<SimpleModuleVersionIdentifier, VersionNumberEntry> unknownSubstitutions =
        new ConcurrentHashMap<>()

    final ConcurrentMap<SimpleModuleVersionIdentifier, VersionNumberEntry> usedSubstitutions = new ConcurrentHashMap<>()

    final AtomicReference<Boolean> resolverInstalled = new AtomicReference<>(false)
}
