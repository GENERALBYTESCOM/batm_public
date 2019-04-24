package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.model.DependencySubstitution
import com.generalbytes.gradle.model.SimpleModuleIdentifier
import com.generalbytes.gradle.model.VersionNumberEntry
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.artifacts.result.DependencyResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class DependencySubstitutionPlugin implements Plugin<Project> {
    private DependencySubstitutionPluginExtension extension
    private Logger logger = LoggerFactory.getLogger('DependencySubstitutionPlugin')

    @Override
    void apply(Project project) {
        extension = project.extensions.create('dependencySubstitutions', DependencySubstitutionPluginExtension)
        project.afterEvaluate {
            installDependencySubstitutions(project)
        }
    }

    private installDependencySubstitutions(Project project) {
        project.configurations.all { Configuration configuration ->
            if (!shouldSkip(configuration)) {
                resolutionStrategy {
                    eachDependency { DependencyResolveDetails details ->
                        final SimpleModuleIdentifier from = new SimpleModuleIdentifier(details.requested.group,
                            details.requested.name)
                        final DependencySubstitution substitution = extension.substitutions.get(from)
                        if (substitution != null
                            && substitution.versions.contains(VersionNumberEntry.parse(details.requested.version))
                        ) {
                            details.useVersion(substitution.toVersion.string)
                            details.because("${this.getClass().simpleName}: ${substitution.definition}")
                        }
                    }
                    logger.debug("Resolution strategy substitutions set for configuration ${configuration}.")
                }
                configuration.incoming.afterResolve { ResolvableDependencies resolvableDependencies ->
                    checkModuleResolution(resolvableDependencies)
                }
            }
        }
    }

    private void checkModuleResolution(ResolvableDependencies resolvableDependencies) {
        resolvableDependencies.resolutionResult.allDependencies.each {
            DependencyResult result ->

        }
    }

    private boolean shouldSkip(Configuration cfg) {
        return extension.skipConfigurations.contains(cfg.name)
    }
}