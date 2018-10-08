package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.model.SimpleModuleVersionIdentifier
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Matcher
import java.util.regex.Pattern

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
        project.configurations.all { Configuration it ->
            if (!shouldSkip(it)) {
                resolutionStrategy {
                    eachDependency { DependencyResolveDetails details ->
                        final SimpleModuleVersionIdentifier from = new SimpleModuleVersionIdentifier(
                            details.requested.group, details.requested.name, details.requested.version)
                        final String toVersion = extension.substitutions.get(from)
                        if (toVersion != null && toVersion.length() > 0) {
                            details.useVersion(toVersion)
                        }
                    }
                    logger.debug("Resolution strategy substitutions set for configuration ${it}.")
                }
            }
        }
    }

    private boolean shouldSkip(Configuration cfg) {
        return extension.skipConfigurations.contains(cfg.name)
    }
}