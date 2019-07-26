package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.model.DependencySubstitution
import com.generalbytes.gradle.model.SimpleModuleIdentifier
import com.generalbytes.gradle.model.VersionNumberEntry
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors

class DependencySubstitutionPluginExtension {
    private static final Pattern COMMENT_PATTERN = Pattern.compile('^\\p{Blank}*((#|//).*)?$')

    static final String BLOCK_NAME = 'dependencySubstitutions'

    ListProperty<String> skipConfigurations
    MapProperty<SimpleModuleIdentifier, DependencySubstitution> substitutions
    Property<Boolean> shared
    Property<MissingSubstitutionAction> missingSubstitutionAction

    DependencySubstitutionPluginExtension(Project project) {
        missingSubstitutionAction = project.objects.property(MissingSubstitutionAction)
        missingSubstitutionAction.set(MissingSubstitutionAction.IGNORE)

        shared = project.objects.property(Boolean)
        shared.set(false)

        substitutions = project.objects.mapProperty(SimpleModuleIdentifier, DependencySubstitution)
        skipConfigurations = project.objects.listProperty(String)
    }

    @SuppressWarnings('unused')
    void shared(boolean shared) {
        this.shared.set(shared)
    }

    @SuppressWarnings('unused')
    void missingSubstitutionAction(MissingSubstitutionAction missingSubstitutionAction) {
        this.missingSubstitutionAction.set(missingSubstitutionAction)
    }

    @SuppressWarnings('unused')
    void substitute(File file) {
        int lineNo = 0
        file.eachLine { String line ->
            lineNo++
            try {
                if (!line.matches(COMMENT_PATTERN)) { //skip comments
                    final DependencySubstitution dependencySubstitution = DependencySubstitution.from(line)
                    substitutions.put(dependencySubstitution.fromIdentifier, dependencySubstitution)
                }
            } catch (Exception e) {
                def msg = "Error parsing line $lineNo of file ${file.canonicalPath}: ${e.message}."
                throw new IllegalStateException(msg, e)
            }
        }
    }

    void substitute(String module, Set<String> versions, String toVersion) {
        DependencySubstitution dependencySubstitution = from(module, versions, toVersion)
        substitutions.put(dependencySubstitution.fromIdentifier, dependencySubstitution)
    }

    @SuppressWarnings('unused')
    void substitute(Map attrs) {
        final String module = attrs.module
        final Set<String> versions = attrs.versions
        final String toVersion = attrs.toVersion

        if (module == null) {
            def msg = "Missing required argument: 'module'."
            throw new IllegalArgumentException(msg)
        }
        if (versions == null) {
            def msg = "Missing required argument: 'versions'."
            throw new IllegalArgumentException(msg)
        }
        if (toVersion == null) {
            def msg = "Missing required argument: 'toVersion'."
            throw new IllegalArgumentException(msg)
        }
        substitute(module, versions, toVersion)
    }

    @SuppressWarnings('unused')
    void skipConfiguration(Configuration cfg) {
        skipConfigurations.add(cfg.name)
    }

    @SuppressWarnings('unused')
    void skipConfiguration(String cfgName) {
        skipConfigurations.add(cfgName)
    }
}
