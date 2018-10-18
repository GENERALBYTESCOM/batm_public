package com.generalbytes.gradle.task

import com.generalbytes.gradle.DependencyVerificationHelper
import com.generalbytes.gradle.model.ChecksumAssertion
import com.generalbytes.gradle.plugin.DependencyVerificationPlugin
import com.generalbytes.gradle.plugin.DependencyVerificationPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.TaskAction

class DependencyChecksums extends DefaultTask {
    public static final String TASK_NAME = 'dependencyChecksums'

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
        if (global.get()) {
            SortedSet<ChecksumAssertion> assertions = new TreeSet<>()
            project.rootProject.allprojects { Project p ->
                if (p.pluginManager.hasPlugin(DependencyVerificationPlugin.ID) ) {
                    final DependencyChecksums checksumsTask = p.tasks.getByName(TASK_NAME)
                    DependencyVerificationHelper.buildAssertions(p, checksumsTask.configurations.get()).values().each {
                        assertions.addAll(it)
                    }
                }
            }
            println ""
            println "$DependencyVerificationPluginExtension.BLOCK_NAME {"
            println "    // generated at ${currentTimestamp()}"
            assertions.each { println "    ${it.definition()}" }
            println "}"
            println ""
        } else {
            printAssertions(DependencyVerificationHelper.buildAssertions(project, configurations.get()))
        }
    }

    private static void printAssertions(Map<Configuration, SortedSet<ChecksumAssertion>> assertionsByConfiguration) {
        final Set<ChecksumAssertion> printedAssertions = new HashSet<>()
        println ""
        println "$DependencyVerificationPluginExtension.BLOCK_NAME {"
        assertionsByConfiguration.each { Configuration configuration,
                                         SortedSet<ChecksumAssertion> assertions ->

            println ""
            print "    // $configuration (${currentTimestamp()});"
            println " showing only previously unlisted assertions"
            assertions.each { ChecksumAssertion assertion ->
                if (!printedAssertions.contains(assertion)) {
                    println "    ${assertion.definition()}"
                    printedAssertions.add(assertion)
                }
            }
        }
        println "}"
        println ""
    }

    private static String currentTimestamp() {
        new Date().format('yyyy-MM-dd\'T\'HH:mm:ss')
    }


}
