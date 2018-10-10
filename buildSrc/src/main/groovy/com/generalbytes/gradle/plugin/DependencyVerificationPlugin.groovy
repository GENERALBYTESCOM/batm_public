package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.task.DependencyChecksums
import com.generalbytes.gradle.task.DependencyVerification
import org.gradle.api.Plugin
import org.gradle.api.Project

class DependencyVerificationPlugin implements Plugin<Project> {
    private DependencyVerificationPluginExtension extension

    void apply(Project project) {
        extension = project.extensions.create(
            DependencyVerificationPluginExtension.BLOCK_NAME,
            DependencyVerificationPluginExtension,
            project
        )

        project.tasks.create(DependencyVerification.TASK_NAME, DependencyVerification) { DependencyVerification task ->
            task.assertions.set(extension.assertions)
            task.configurations.set(extension.configurations)
        }

        project.tasks.create(DependencyChecksums.TASK_NAME, DependencyChecksums).configurations.set(
            extension.configurations)

        project.tasks.getByName('check').dependsOn(DependencyVerification.TASK_NAME)
    }
}