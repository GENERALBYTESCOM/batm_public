package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.model.DependencySubstitution
import com.generalbytes.gradle.model.SimpleModuleIdentifier
import com.generalbytes.gradle.model.VersionNumberEntry
import org.gradle.api.artifacts.Configuration

import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors

class DependencySubstitutionPluginExtension {
    private static final Pattern COMMENT_PATTERN = Pattern.compile('^\\p{Blank}*((#|//).*)?$')
    private static final Pattern SUBSTITUTION_PATTERN = Pattern.compile('^\\p{Blank}*substitute\\p{Blank}*module\\p{Blank}*:\\p{Blank}*\'([^\']*)\'\\p{Blank}*,\\p{Blank}*versions\\p{Blank}*:\\p{Blank}*\\[\\p{Blank}*(\'[^\']*\'(?:\\p{Blank}*,\\p{Blank}*\'[^\']*\')*)\\p{Blank}*]\\p{Blank}*,\\p{Blank}*toVersion\\p{Blank}*:\\p{Blank}*\'([^\']*)\'\\p{Blank}*(?:(?:#|//).*)?$')

    List<String> skipConfigurations = new LinkedList<>()
    Map<SimpleModuleIdentifier, DependencySubstitution> substitutions = new HashMap<>()

    void substitute(File file) {
        int lineNo = 0
        file.eachLine { line ->
            lineNo++
            Matcher substitutionMatcher = SUBSTITUTION_PATTERN.matcher(line)
            if (substitutionMatcher.matches()) {
                final String module = substitutionMatcher.group(1)
                final String versionsString = substitutionMatcher.group(2)
                final String toVersion = substitutionMatcher.group(3)

                final List<String> versions = versionsString
                    .split("[, ]")
                    .toList()
                    .stream()
                    .filter({ !it.isEmpty() })
                    .map({ it.replace("'", '')})
                    .collect(Collectors.toList())
                try {
                    substitute(module, versions, toVersion)
                } catch(Exception e) {
                    def msg = "Error parsing line $lineNo of file ${file.canonicalPath}: ${e.message}."
                    throw new IllegalStateException(msg, e)
                }
            } else if (!line.matches(COMMENT_PATTERN)) {
                def msg = "Error on line $lineNo of file ${file.canonicalPath}: illegal line format."
                throw new IllegalStateException(msg)
            }
        }
    }

    void substitute(String module, List<String> versions, String toVersion) {
        final String[] groupName = module.split(':')
        if (groupName.length != 2) {
            throw new IllegalArgumentException("Invalid module '$module'. Correct format is 'group:name'.")
        }
        final DependencySubstitution dependencySubstitution = new DependencySubstitution(
            groupName[0],
            groupName[1],
            versions.stream().map({VersionNumberEntry.parse(it)}).collect(Collectors.toSet()),
            VersionNumberEntry.parse(toVersion)
        )
        substitutions.put(dependencySubstitution.fromIdentifier, dependencySubstitution)
    }

    void substitute(Map attrs) {
        final String module = attrs.module
        final List<String> versions = attrs.versions
        final String toVersion = attrs.toVersion

        if (module == null) {
            def msg = "Missing required argument: 'module'."
            throw new IllegalArgumentException(msg)
        }
        if (versions == null) {
            def msg = "Missing required argument: 'versions'."
            throw new IllegalArgumentException(msg)
        }
        if (toVersion == null) {
            def msg = "Missing required argument: 'toVersion'."
            throw new IllegalArgumentException(msg)
        }
        substitute(module, versions, toVersion)
    }

    void skipConfiguration(Configuration cfg) {
        skipConfigurations.add(cfg.name)
    }

    void skipConfiguration(String cfgName) {
        skipConfigurations.add(cfgName)
    }
}
