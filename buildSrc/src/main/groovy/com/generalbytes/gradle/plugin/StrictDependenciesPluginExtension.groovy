package com.generalbytes.gradle.plugin

import org.gradle.api.artifacts.Configuration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
