package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.task.DependencyChecksums
import com.generalbytes.gradle.task.DependencyVerification
import org.gradle.api.Plugin
import org.gradle.api.Project

class DependencyVerificationPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.tasks.create(DependencyVerification.TASK_NAME, DependencyVerification.class)
        project.tasks.getByName('check').dependsOn(DependencyVerification.TASK_NAME)

        project.tasks.create(DependencyChecksums.TASK_NAME, DependencyChecksums.class)
    }
}