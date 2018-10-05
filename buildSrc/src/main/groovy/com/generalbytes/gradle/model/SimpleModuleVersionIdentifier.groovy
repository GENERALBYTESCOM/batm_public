package com.generalbytes.gradle.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.gradle.api.artifacts.ModuleVersionIdentifier

import java.util.regex.Matcher
import java.util.regex.Pattern

@EqualsAndHashCode
@ToString
class SimpleModuleVersionIdentifier implements ModuleVersionIdentifier {
    final SimpleModuleIdentifier module
    final String version

    SimpleModuleVersionIdentifier(String id) {
        final Matcher matcher = Pattern.compile('^([^:]*):([^:]*):([^:]*)$').matcher(id)
        if (!matcher.matches()) {
            def msg = "Module identifier '$id' has incorrect format."
            throw new IllegalArgumentException(msg)
        }
        this.module = new SimpleModuleIdentifier(matcher.group(1), matcher.group(2))
        this.version = matcher.group(3)
    }

    SimpleModuleVersionIdentifier(String group, String name, String version) {
        this.version = version
        this.module = new SimpleModuleIdentifier(group, name)
    }

    @Override
    String getGroup() {
        return module.group
    }

    @Override
    String getName() {
        return module.name
    }

    String toGradleString() {
        return "${module.group}:${module.name}:$version"
    }
}
