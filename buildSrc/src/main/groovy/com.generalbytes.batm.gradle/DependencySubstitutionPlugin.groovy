package com.generalbytes.batm.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.artifacts.UnknownConfigurationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DependencySubstitutionPlugin implements Plugin<Project> {
    private DependencySubstitutionPluginExtension extension
    private List<String> initialConfinedConfigurations = new LinkedList<>()
    private Logger logger = LoggerFactory.getLogger('gb-gradle')

    static void confine(Project project, String configurationName) {
        confine(project.getConfigurations().getByName(configurationName))
    }

    static void confine(Configuration configuration) {
        configuration.transitive = false
    }

    void apply(Project project) {
        extension = project.extensions.create('dependencySubstitutions', DependencySubstitutionPluginExtension)
        initialConfinedConfigurations.addAll([
            'compile',
            'compileOnly',
            'compileClasspath',
            'implementation',
            'providedCompile',

            'testCompile',
            'testCompileOnly',
            'testCompileClasspath',
            'testImplementation'
        ])

        project.afterEvaluate {
            configure(project)
        }
    }

    void configure(Project project) {
        initialConfinedConfigurations.each {
            try {
                confine(project.configurations[it])
            } catch (UnknownConfigurationException ignored) {
                logger.info("Plugin-specified confined configuration $it not found, skipping...")
            }
        }
        extension.confinedConfigurations.each {
            switch (it) {
                case String:
                    confine(project, (String) it)
                    break
                case Configuration:
                    confine((Configuration) it)
                    break
                default:
                    throw new IllegalStateException("Illegal specification for confined configuration: $it (${it.class})")
            }
        }

        if (extension.conflictFail) {
            project.configurations.all {
                resolutionStrategy {
                    failOnVersionConflict()
                }
            }
        }

        project.configurations.all {
            resolutionStrategy {
                eachDependency { DependencyResolveDetails details ->
                    final SimpleModuleVersionIdentifier from = new SimpleModuleVersionIdentifier(details.requested.group, details.requested.name, details.requested.version)
                    final String toVersion = extension.substitutions.get(from)
                    if (toVersion != null && toVersion.length() > 0) {
                        details.useVersion(toVersion)
                    }
                }
            }
        }
    }
}