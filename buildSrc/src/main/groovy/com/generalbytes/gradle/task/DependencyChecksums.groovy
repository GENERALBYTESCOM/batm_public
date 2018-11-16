package com.generalbytes.gradle.task

import com.generalbytes.gradle.DependencyVerificationHelper
import com.generalbytes.gradle.model.ChecksumAssertion
import com.generalbytes.gradle.plugin.DependencyVerificationPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.TaskAction

class DependencyChecksums extends DefaultTask {
    static final String TASK_NAME = 'dependencyChecksums'
    static final String CHECKSUMS_FILE = 'dependencyChecksums.txt'

    final SetProperty<Object> configurations = project.objects.setProperty(Object)
    final Property<Boolean> global = project.objects.property(Boolean)

    @SuppressWarnings('unused')
    void configuration(String configuration) {
        configurations.add(configuration)
    }

    @SuppressWarnings('unused')
    void configuration(Configuration configuration) {
        configurations.add(configuration)
    }

    @TaskAction
    @SuppressWarnings('unused')
    private void taskAction() {
        printChecksumsTaskOutput(this, System.out)
    }

    static void printChecksumsTaskOutput(DependencyChecksums checksumsTask, PrintStream printStream) {
        if (checksumsTask.global.get()) {
            printGlobalAssertions(DependencyVerificationHelper.globalAssertions(checksumsTask.project), printStream)
        } else {
            final Map<Configuration, SortedSet<ChecksumAssertion>> assertionsByConfiguration =
                DependencyVerificationHelper.assertionsByConfiguration(
                    checksumsTask.project,
                    checksumsTask.configurations.get()
                )
            printAssertions(assertionsByConfiguration, printStream)
        }
    }

    static void printGlobalAssertions(SortedSet<ChecksumAssertion> assertions, PrintStream printStream) {
        final boolean stdout = printStream == System.out
        final String indent = stdout ? '    ' : ''
        if (stdout) {
            printStream.println ""
            printStream.println "$DependencyVerificationPluginExtension.BLOCK_NAME {"
        }
        printStream.println "$indent// generated at ${currentTimestamp()}"
        assertions.each { printStream.println "${indent}${it.definition()}" }
        if (stdout) {
            printStream.println "}"
            printStream.println ""
        }
    }

    static void printAssertions(Map<Configuration, SortedSet<ChecksumAssertion>> assertionsByConfiguration,
                                PrintStream printStream) {
        final boolean stdout = printStream == System.out
        final String indent = stdout ? '    ' : ''
        final Set<ChecksumAssertion> printedAssertions = new HashSet<>()
        if (stdout) {
            printStream.println ""
            printStream.println "$DependencyVerificationPluginExtension.BLOCK_NAME {"
        }
        assertionsByConfiguration.each { Configuration configuration,
                                         SortedSet<ChecksumAssertion> assertions ->

            printStream.println ""
            printStream.print "$indent// $configuration (${currentTimestamp()});"
            printStream.println " showing only previously unlisted assertions"
            assertions.each { ChecksumAssertion assertion ->
                if (!printedAssertions.contains(assertion)) {
                    printStream.println "${indent}${assertion.definition()}"
                    printedAssertions.add(assertion)
                }
            }
        }
        if (stdout) {
            printStream.println "}"
            printStream.println ""
        }
    }

    static DependencyChecksums getChecksumsTask(Project project) {
        project.tasks.getByName(TASK_NAME) as DependencyChecksums
    }

    static String currentTimestamp() {
        new Date().format('yyyy-MM-dd\'T\'HH:mm:ss')
    }

}
