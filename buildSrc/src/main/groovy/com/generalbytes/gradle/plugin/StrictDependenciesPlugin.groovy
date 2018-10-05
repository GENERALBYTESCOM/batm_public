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

    private void confine(Project project, String configurationName) {
        try {
            confine(project.configurations[configurationName])
        } catch (UnknownConfigurationException ignored) {
            logger.info("Plugin-specified confined configuration $configurationName not found, skipping...")
        }
    }

    private void confine(Configuration configuration) {
        configuration.transitive = false
        logger.debug("Disabled transitivity for configuration ${configuration}.")
    }

    @Override
    void apply(Project project) {
        extension = project.extensions.create('strictDependencies', StrictDependenciesPluginExtension)

        project.afterEvaluate {
            installConfinementConfigured(project)
            installConflictFail(project)
        }
    }

    private List<Object> installConfinementConfigured(Project project) {
        extension.confineConfigurations.each {
            switch (it) {
                case String:
                    confine(project, (String) it)
                    break
                case Configuration:
                    confine((Configuration) it)
                    break
                default:
                    throw new InvalidUserDataException("Illegal specification for confined configuration: $it (${it.class})")
            }
        }
    }

    private void installConflictFail(Project project) {
        if (extension.conflictFail) {
            project.configurations.all {
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

class StrictDependenciesPluginExtension {
    private Logger logger = LoggerFactory.getLogger('StrictDependenciesPluginExtension')
    List<Object> confineConfigurations = new LinkedList<>()
    List<String> skipConfigurations = new LinkedList<>()
    boolean conflictFail = true

    void confine(String cfgName) {
        confineConfigurations.add(cfgName)
    }

    void confine(Configuration cfg) {
        confineConfigurations.add(cfg)
    }

    void skip(Configuration cfg) {
        skipConfigurations.add(cfg.name)
    }

    void skip(String cfgName) {
        skipConfigurations.add(cfgName)
    }
}


