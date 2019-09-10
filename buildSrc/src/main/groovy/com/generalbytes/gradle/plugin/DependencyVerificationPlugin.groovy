package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.task.DependencyVerification
import com.generalbytes.gradle.task.DependencyChecksums
import org.gradle.api.Plugin
import org.gradle.api.Project

class DependencyVerificationPlugin implements Plugin<Project> {
    static final String ID = 'com.generalbytes.gradle.dependency.verification'
    static final String PLUGIN_BUILD_DIR_NAME = 'dependencyVerification'

    private DependencyVerificationPluginExtension extension

    void apply(Project project) {
        extension = project.extensions.create(
            DependencyVerificationPluginExtension.BLOCK_NAME,
            DependencyVerificationPluginExtension,
            project
        )

//        project.tasks.create(DependencyVerification.TASK_NAME, DependencyVerification) { DependencyVerification task ->
//            task.group = 'verification'
//            task.description = 'Verifies dependency checksums.'
//
//            task.assertions.set(extension.assertions)
//            task.configurations.set(extension.configurations)
//            task.failOnChecksumError.set(extension.failOnChecksumError)
//            task.printUnusedAssertions.set(extension.printUnusedAssertions)
//        }

        project.tasks.create(DependencyChecksums.TASK_NAME, DependencyChecksums) { DependencyChecksums task ->
            task.group = 'help'
            task.description = 'Prints dependency checksums.'

            task.configurations.set(extension.configurations)
        }

        project.gradle.projectsEvaluated {
            final DependencyVerificationPluginExtension extension = project.dependencyVerifications
            DependencyVerification.verifyChecksums(
                project,
                extension.configurations.get(),
                extension.assertions.get(),
                extension.failOnChecksumError.get(),
                extension.printUnusedAssertions.get(),
            )
        }
    }
}