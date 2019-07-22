package com.generalbytes.gradle.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.gradle.api.artifacts.ModuleIdentifier

import java.util.regex.Matcher
import java.util.regex.Pattern

@EqualsAndHashCode
final class SimpleModuleIdentifier implements ModuleIdentifier {
    final String group
    final String name

    SimpleModuleIdentifier(String group, String name) {
        this.group = group
        this.name = name
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
