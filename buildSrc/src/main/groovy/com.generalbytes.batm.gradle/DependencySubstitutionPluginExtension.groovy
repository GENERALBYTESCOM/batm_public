package com.generalbytes.batm.gradle

import org.gradle.api.artifacts.Configuration

import java.util.regex.Matcher
import java.util.regex.Pattern

class DependencySubstitutionPluginExtension {
    List<Object> confinedConfigurations = new LinkedList<>()
    Map<SimpleModuleVersionIdentifier, String> substitutions = new HashMap<>()
    boolean conflictFail = true

    void substitute(File file) {
        final Pattern commentPattern = Pattern.compile('(?m)^\\p{Blank}*#.*$')
        final Pattern substitutionPattern = Pattern.compile('(?m)^substitute\\p{Blank}*from\\p{Blank}*:\\p{Blank}*\\\'([^\\\']*)\\\'\\p{Blank}*,\\p{Blank}*toVersion\\p{Blank}*:\\p{Blank}*\\\'([^\\\']*)\\\'\\p{Blank}*$')
        int lineNo = 0
        file.eachLine { line ->
            lineNo++
            Matcher substitutionMatcher = substitutionPattern.matcher(line)
            if (substitutionMatcher.matches()) {
                final String from = substitutionMatcher.group(1)
                final String toVersion = substitutionMatcher.group(2)
                substitute(from, toVersion)
            } else if (!line.matches(commentPattern)) {
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

    void confine(String cfgName) {
        confinedConfigurations.add(cfgName)
    }

    void confine(Configuration cfg) {
        confinedConfigurations.add(cfg)
    }
}
