package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.model.ChecksumAssertion
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

import java.util.regex.Matcher
import java.util.regex.Pattern

class DependencyVerificationPluginExtension {
    static final String BLOCK_NAME = 'dependencyVerifications'

    SetProperty<ChecksumAssertion> assertions
    SetProperty<Object> configurations
    Property<Boolean> failOnChecksumError
    Property<Boolean> printUnusedAssertions

    DependencyVerificationPluginExtension(Project project) {
        configurations = project.objects.setProperty(Object)
        assertions = project.objects.setProperty(ChecksumAssertion)
        failOnChecksumError = project.objects.property(Boolean)
        printUnusedAssertions = project.objects.property(Boolean)
        printUnusedAssertions.set(true)
    }

    @SuppressWarnings('unused')
    void configuration(String configuration) {
        configurations.add(configuration)
    }

    @SuppressWarnings('unused')
    void configuration(Configuration configuration) {
        configurations.add(configuration)
    }

    @SuppressWarnings('unused')
    void failOnChecksumError(boolean failOnChecksumError) {
        this.failOnChecksumError.set(failOnChecksumError)
    }

    @SuppressWarnings('unused')
    void printUnusedAssertions(boolean printUnusedAssertions) {
        this.printUnusedAssertions.set(printUnusedAssertions)
    }

    @SuppressWarnings('unused')
    void verifyModule(String s) {
        assertions.add(new ChecksumAssertion(s))
    }

    @SuppressWarnings('unused')
    void checksums(File file) {
        final Pattern commentPattern = Pattern.compile('(?m)^\\p{Blank}*((#|//).*)?$')
        final Pattern assertionPattern = Pattern.compile(
            '(?m)^\\p{Blank}*verifyModule\\p{Blank}*\'([^\']*)\'\\p{Blank}*((#|//).*)?$')
        int lineNo = 0
        file.eachLine { line ->
            lineNo++
            Matcher assertionMatcher = assertionPattern.matcher(line)
            if (assertionMatcher.matches()) {
                final String assertion = assertionMatcher.group(1)
                verifyModule(assertion)
            } else if (!line.matches(commentPattern)) {
                def msg = "Error on line $lineNo of file ${file.canonicalPath}: illegal line format."
                throw new IllegalStateException(msg)
            }
        }
    }

}
