package com.generalbytes.gradle.plugin

import org.gradle.api.artifacts.Configuration

class StrictDependenciesPluginExtension {
    List<Object> nontransitiveConfigurations = new LinkedList<>()
    List<String> skipConfigurations = new LinkedList<>()
    boolean conflictFail = true

    void nontransitive(String cfgName) {
        nontransitiveConfigurations.add(cfgName)
    }

    void nontransitive(Configuration cfg) {
        nontransitiveConfigurations.add(cfg)
    }

    void skip(Configuration cfg) {
        skipConfigurations.add(cfg.name)
    }

    void skip(String cfgName) {
        skipConfigurations.add(cfgName)
    }
}
