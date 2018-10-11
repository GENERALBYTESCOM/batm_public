package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.model.ChecksumAssertion
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

class DependencyVerificationPluginExtension {
    static final String BLOCK_NAME = 'dependencyVerifications'

    SetProperty<ChecksumAssertion> assertions
    SetProperty<Object> configurations
    Property<Boolean> strict
    Property<Boolean> skip

    DependencyVerificationPluginExtension(Project project) {
        configurations = project.objects.setProperty(Object)
        assertions = project.objects.setProperty(ChecksumAssertion)
        strict = project.objects.property(Boolean)
        skip = project.objects.property(Boolean)
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
    void strict(boolean strict) {
        this.strict.set(strict)
    }

    @SuppressWarnings('unused')
    void skip(boolean skip) {
        this.skip.set(skip)
    }

    @SuppressWarnings('unused')
    void verifyModule(String s) {
        assertions.add(new ChecksumAssertion(s))
    }
}
