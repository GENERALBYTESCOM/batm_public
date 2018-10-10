package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.model.ChecksumAssertion
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.SetProperty

class DependencyVerificationPluginExtension {
    static final String BLOCK_NAME = 'dependencyVerifications'

    SetProperty<ChecksumAssertion> assertions
    SetProperty<Object> configurations

    DependencyVerificationPluginExtension(Project project) {
        configurations = project.objects.setProperty(Object)
        assertions = project.objects.setProperty(ChecksumAssertion)
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
    void verifyModule(String s) {
        assertions.add(new ChecksumAssertion(s))
    }
}
