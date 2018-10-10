package com.generalbytes.gradle.model

import groovy.transform.EqualsAndHashCode
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

import java.util.regex.Matcher
import java.util.regex.Pattern

@EqualsAndHashCode
class SimpleModuleVersionIdentifier implements ModuleComponentIdentifier {
    private static final PATTERN = Pattern.compile('^([^:]*):([^:]*):([^:]*(-SNAPSHOT:[^:]*)?)$')

    final SimpleModuleIdentifier moduleIdentifier
    final String version

    SimpleModuleVersionIdentifier(String id) {
        final Matcher matcher = PATTERN.matcher(id)
        if (!matcher.matches()) {
            def msg = "Module identifier '$id' has incorrect format."
            throw new IllegalArgumentException(msg)
        }
        this.moduleIdentifier = new SimpleModuleIdentifier(matcher.group(1), matcher.group(2))
        this.version = matcher.group(3)
    }

    SimpleModuleVersionIdentifier(String group, String name, String version) {
        this.version = version
        this.moduleIdentifier = new SimpleModuleIdentifier(group, name)
    }

    SimpleModuleVersionIdentifier(ModuleComponentIdentifier moduleComponentIdentifier) {
        this(moduleComponentIdentifier.group, moduleComponentIdentifier.module, moduleComponentIdentifier.version)
    }

//    SimpleModuleVersionIdentifier(ModuleComponentIdentifier moduleComponentIdentifier) {
//        this(moduleComponentIdentifier.group, moduleComponentIdentifier.module, moduleComponentIdentifier.version)
//    }

    @Override
    String getGroup() {
        return moduleIdentifier.group
    }

    @Override
    String getModule() {
        return moduleIdentifier.name
    }

    @Override
    String toString() {
        return "${moduleIdentifier.group}:${moduleIdentifier.name}:$version"
    }

    @Override
    String getDisplayName() {
        "module: '$this'"
    }
}
