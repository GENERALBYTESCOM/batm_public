package com.generalbytes.gradle.plugin

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.UnknownConfigurationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StrictDependenciesPlugin implements Plugin<Project> {
    private StrictDependenciesPluginExtension extension
    private Logger logger = LoggerFactory.getLogger('StrictDependenciesPlugin')

    private void makeNontransitive(Project project, String configurationName) {
        try {
            makeNontransitive(project.configurations[configurationName])
        } catch (UnknownConfigurationException ignored) {
            logger.info("Plugin-specified nontransitive configuration $configurationName not found, skipping...")
        }
    }

    private void makeNontransitive(Configuration configuration) {
        configuration.transitive = false
        logger.debug("Disabled transitivity for configuration ${configuration}.")
    }

    @Override
    void apply(Project project) {
        extension = project.extensions.create('strictDependencies', StrictDependenciesPluginExtension)

        project.afterEvaluate {
            installNontransitivity(project)
            installConflictFail(project)
        }
    }

    private List<Object> installNontransitivity(Project project) {
        extension.nontransitiveConfigurations.each {
            switch (it) {
                case String:
                    makeNontransitive(project, (String) it)
                    break
                case Configuration:
                    makeNontransitive((Configuration) it)
                    break
                default:
                    throw new InvalidUserDataException(
                        "Illegal specification for nontransitive configuration: $it (${it.class})")
            }
        }
    }

    private void installConflictFail(Project project) {
        if (extension.conflictFail) {
            project.configurations.all { Configuration it ->
                if (!shouldSkip(it)) {
                    resolutionStrategy {
                        failOnVersionConflict()
                        logger.debug("failOnVersionConflict() resolution strategy set for configuration ${it}.")
                    }
                }
            }
        }
    }

    private boolean shouldSkip(Configuration cfg) {
        return extension.skipConfigurations.contains(cfg.name)
    }
}



