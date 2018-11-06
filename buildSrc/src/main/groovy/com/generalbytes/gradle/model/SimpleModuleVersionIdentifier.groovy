package com.generalbytes.gradle.model

import groovy.transform.EqualsAndHashCode
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

import java.util.regex.Matcher
import java.util.regex.Pattern

@EqualsAndHashCode
class SimpleModuleVersionIdentifier implements ModuleComponentIdentifier {
    private static final PATTERN = Pattern.compile('^([^:]*):([^:]*):([^:]*(-SNAPSHOT:[^:]*)?)(:([^:]*))?$')

    final SimpleModuleIdentifier moduleIdentifier
    final String version

    SimpleModuleVersionIdentifier(String id) {
        final Matcher matcher = PATTERN.matcher(id)
        if (!matcher.matches()) {
            def msg = "Module identifier '$id' has incorrect format."
            throw new IllegalArgumentException(msg)
        }
        this.moduleIdentifier = new SimpleModuleIdentifier(
            matcher.group(1),
            matcher.group(2),
            matcher.group(6)
        )
        this.version = matcher.group(3)
    }

    SimpleModuleVersionIdentifier(String group, String name, String version) {
        this(group, name, version, null)
    }

    SimpleModuleVersionIdentifier(String group, String name, String version, String classifier) {
        this.version = version
        this.moduleIdentifier = new SimpleModuleIdentifier(group, name, classifier)
    }

    SimpleModuleVersionIdentifier(ModuleComponentIdentifier moduleComponentIdentifier) {
        this(moduleComponentIdentifier.group, moduleComponentIdentifier.module, moduleComponentIdentifier.version)
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
        String classifier = matcher.group(6)


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
        return moduleIdentifier.classifier
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
        if (moduleIdentifier.classifier != null) {
            return "$withoutClassifier:${moduleIdentifier.classifier}"
        } else {
            return withoutClassifier
        }
    }

    @Override
    String getDisplayName() {
        "module: '$this'"
    }
}
