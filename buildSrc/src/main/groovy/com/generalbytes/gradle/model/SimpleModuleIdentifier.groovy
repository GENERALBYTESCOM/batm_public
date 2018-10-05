package com.generalbytes.gradle.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.gradle.api.artifacts.ModuleIdentifier

import java.util.regex.Matcher
import java.util.regex.Pattern

@EqualsAndHashCode
class SimpleModuleIdentifier implements ModuleIdentifier {
    final String group
    final String name

    SimpleModuleIdentifier(String group, String name) {
        this.group = group
        this.name = name
    }

    SimpleModuleIdentifier(ModuleIdentifier other) {
        this(other.group, other.name)
    }

    SimpleModuleIdentifier(String id) {
        final Matcher matcher = Pattern.compile('^([^:]*):([^:]*)$').matcher(id)
        if (!matcher.matches()) {
            def msg = "Module identifier '$id' has incorrect format."
            throw new IllegalArgumentException(msg)
        }
        this.group = matcher.group(1)
        this.name = matcher.group(2)
    }


    @Override
    String toString() {
        return "${group}:${name}"
    }

    String displayName() {
        "module: '$this'"
    }
}
