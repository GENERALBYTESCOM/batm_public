package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.model.SimpleModuleVersionIdentifier
import org.gradle.api.artifacts.Configuration

import java.util.regex.Matcher
import java.util.regex.Pattern

class DependencySubstitutionPluginExtension {
    private static final Pattern COMMENT_PATTERN = Pattern.compile('^\\p{Blank}*((#|//).*)?$')
    private static final Pattern SUBSTITUTION_PATTERN = Pattern.compile('^\\p{Blank}*substitute\\p{Blank}*from\\p{Blank}*:\\p{Blank}*\'([^\']*)\'\\p{Blank}*,\\p{Blank}*toVersion\\p{Blank}*:\\p{Blank}*\'([^\']*)\'\\p{Blank}*((#|//).*)?$')

    List<String> skipConfigurations = new LinkedList<>()
    Map<SimpleModuleVersionIdentifier, String> substitutions = new HashMap<>()

    void substitute(File file) {
        int lineNo = 0
        file.eachLine { line ->
            lineNo++
            Matcher substitutionMatcher = SUBSTITUTION_PATTERN.matcher(line)
            if (substitutionMatcher.matches()) {
                final String from = substitutionMatcher.group(1)
                final String toVersion = substitutionMatcher.group(2)
                substitute(from, toVersion)
            } else if (!line.matches(COMMENT_PATTERN)) {
                def msg = "Error on line $lineNo of file ${file.canonicalPath}: illegal line format."
                throw new IllegalStateException(msg)
            }
        }
    }

    void substitute(String from, String toVersion) {
        substitutions.put(new SimpleModuleVersionIdentifier(from), toVersion)
    }

    void substitute(Map attrs) {
        def from = attrs.from
        def toVersion = attrs.toVersion

        if (from == null) {
            def msg = "Missing required argument: 'from'."
            throw new IllegalArgumentException(msg)
        }
        if (toVersion == null) {
            def msg = "Missing required argument: 'toVersion'."
            throw new IllegalArgumentException(msg)
        }
        substitute(from, toVersion)
    }

    void skipConfiguration(Configuration cfg) {
        skipConfigurations.add(cfg.name)
    }

    void skipConfiguration(String cfgName) {
        skipConfigurations.add(cfgName)
    }
}
