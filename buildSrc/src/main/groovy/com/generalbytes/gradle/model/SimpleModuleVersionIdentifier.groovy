package com.generalbytes.gradle.model

import groovy.transform.EqualsAndHashCode
import org.gradle.api.artifacts.ModuleVersionSelector
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

import java.util.regex.Matcher
import java.util.regex.Pattern

@EqualsAndHashCode
final class SimpleModuleVersionIdentifier implements ModuleComponentIdentifier {
    private static final PATTERN = Pattern.compile('^([^:]*):([^:]*):([^:]*(?:-SNAPSHOT:[^:]*)?)(?::([^:]*))?$')

    final SimpleModuleIdentifier moduleIdentifier
    final String version
    final String classifier

    SimpleModuleVersionIdentifier(String id) {
        final Matcher matcher = PATTERN.matcher(id)
        if (!matcher.matches()) {
            def msg = "Module version identifier '$id' has incorrect format."
            throw new IllegalArgumentException(msg)
        }
        this.moduleIdentifier = new SimpleModuleIdentifier(
            matcher.group(1),
            matcher.group(2)
        )
        this.version = matcher.group(3)
        if (this.version.isEmpty()) {
            def msg = "Module identifier '$id' has incorrect format."
            throw new IllegalArgumentException(msg)
        }
        this.classifier = matcher.group(4)
    }

    SimpleModuleVersionIdentifier(SimpleModuleIdentifier module, String version) {
        this(module, version, null)
    }

    SimpleModuleVersionIdentifier(SimpleModuleIdentifier module, String version, String classifier) {
        if (version == null || version.isEmpty()) {
            def msg = "Version can't be null nor empty."
            throw new IllegalArgumentException(msg)
        }
        this.moduleIdentifier = module
        this.version = version
        this.classifier = classifier
    }

    SimpleModuleVersionIdentifier(String group, String name, String version) {
        this(new SimpleModuleIdentifier(group, name), version)
    }

    SimpleModuleVersionIdentifier(String group, String name, String version, String classifier) {
        this(new SimpleModuleIdentifier(group, name), version, classifier)
    }

    SimpleModuleVersionIdentifier(ModuleComponentIdentifier moduleComponentIdentifier) {
        this(moduleComponentIdentifier.group, moduleComponentIdentifier.module, moduleComponentIdentifier.version)
    }

    SimpleModuleVersionIdentifier(ModuleVersionSelector moduleVersionSelector) {
        this(moduleVersionSelector.group, moduleVersionSelector.name, moduleVersionSelector.version)
    }

//    SimpleModuleVersionIdentifier(ModuleComponentIdentifier moduleComponentIdentifier) {
//        this(moduleComponentIdentifier.group, moduleComponentIdentifier.module, moduleComponentIdentifier.version)
//    }

    static SimpleModuleVersionIdentifier createWithClassifierHeuristics(String id, String fileName) {
        final Matcher matcher = PATTERN.matcher(id)
        if (!matcher.matches()) {
            final String msg = "Module identifier '$id' has incorrect format (fileName='$fileName')."
            throw new IllegalArgumentException(msg)
        }
        final String group = matcher.group(1)
        final String name = matcher.group(2)
        final String version = matcher.group(3)
        String classifier = matcher.group(4)


        final String fileBaseName = extractBaseName(fileName)
        final String nameVersion = "$name-$version-"

        if (classifier == null && fileBaseName.startsWith(nameVersion)) {
            classifier = fileBaseName.substring(nameVersion.length())
        }

        return new SimpleModuleVersionIdentifier(group, name, version, classifier)
    }


    @Override
    String getGroup() {
        return moduleIdentifier.group
    }

    @Override
    String getModule() {
        return moduleIdentifier.name
    }

    String getClassifier() {
        return classifier
    }

    static String extractBaseName(String fileName) {
        final int dotIndex = fileName.lastIndexOf('.')
        if (dotIndex >= 0) {
            return fileName.substring(0, dotIndex)
        } else {
            return fileName
        }
    }

    @Override
    String toString() {
        final String withoutClassifier = "${moduleIdentifier.group}:${moduleIdentifier.name}:$version"
        if (classifier != null) {
            return "$withoutClassifier:${classifier}"
        } else {
            return withoutClassifier
        }
    }

    @Override
    String getDisplayName() {
        "module: '$this'"
    }
}
