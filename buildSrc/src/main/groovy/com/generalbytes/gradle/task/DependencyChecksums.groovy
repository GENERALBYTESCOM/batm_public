package com.generalbytes.gradle.task

import com.generalbytes.gradle.model.ChecksumAssertion
import com.generalbytes.gradle.plugin.DependencyVerificationPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.TaskAction

class DependencyChecksums extends DefaultTask {
    public static final String TASK_NAME = 'dependencyChecksums'

    final SetProperty<Object> configurations = project.objects.setProperty(Object)

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
        printAssertions(buildAssertions(project))
    }

    private Map<Configuration, SortedSet<ChecksumAssertion>> buildAssertions(Project project) {
        final Map<Configuration, SortedSet<ChecksumAssertion>> assertionsByConfiguration = new HashMap<>()
        DependencyVerification.toConfigurations(project, configurations.get()).each { Configuration configuration ->
            Set<ChecksumAssertion> assertions = assertionsForConfiguration(project, configuration)
            assertionsByConfiguration[configuration] = assertions

        }
        assertionsByConfiguration
    }

    protected SortedSet<ChecksumAssertion> assertionsForConfiguration(Project project, Configuration configuration) {
        final SortedSet<ChecksumAssertion> assertions = new TreeSet<>()
        DependencyVerification.getIncomingArtifactCollection(project, configuration).each {
            final ComponentIdentifier identifier = it.id.componentIdentifier
            if (identifier instanceof ModuleComponentIdentifier) {
                assertions.add(
                    new ChecksumAssertion(identifier, DependencyVerification.calculateSha256(it.file))
                )
            } else if (identifier instanceof ProjectComponentIdentifier) {
                logger.info("Skipped generating $DependencyVerification.TASK_NAME assertion for project-local dependency " +
                    "$identifier.")
            } else if (identifier instanceof ComponentArtifactIdentifier) {
                logger.info("Skipped generating $DependencyVerification.TASK_NAME assertion for local file dependency" +
                    " $identifier.")
            } else {
                throw new IllegalStateException("Unexpected component identifier type (${identifier.class}) for " +
                    "identifier '$identifier'.")
            }
        }
        assertions
    }

    private static void printAssertions(Map<Configuration, SortedSet<ChecksumAssertion>> assertionsByConfiguration) {
        final Set<ChecksumAssertion> printedAssertions = new HashSet<>()
        println ""
        println "$DependencyVerificationPluginExtension.BLOCK_NAME {"
        assertionsByConfiguration.each { Configuration configuration,
                                         SortedSet<ChecksumAssertion> assertions ->

            println ""
            print "    // $configuration (${new Date().format('yyyy-MM-dd\'T\'HH:mm:ss')});"
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


}
