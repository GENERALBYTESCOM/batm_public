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
    final String classifier

    SimpleModuleIdentifier(String group, String name) {
        this(group, name, null)
    }

    SimpleModuleIdentifier(String group, String name, String classifier) {
        this.group = group
        this.name = name
        this.classifier = classifier
    }

    SimpleModuleIdentifier(ModuleIdentifier other) {
        this(other.group, other.name)
    }


    @Override
    String toString() {
        return "${group}:${name}"
    }

    String displayName() {
        "module: '$this'"
    }
}
