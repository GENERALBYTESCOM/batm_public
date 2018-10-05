/**
 * Based on Gradle 'witness' plugin by Open Whisper Systems.
 *
 * Copyright (c) 2014 Open Whisper Systems
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.Util
import com.generalbytes.gradle.model.ChecksumAssertion
import com.generalbytes.gradle.model.SimpleModuleIdentifier
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.attributes.Attribute
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.security.MessageDigest

class DependencyVerificationPlugin implements Plugin<Project> {
    private Logger logger = LoggerFactory.getLogger('DependencyVerificationPlugin')

    static String calculateSha256(File file) {
        MessageDigest md = MessageDigest.getInstance("SHA-256")
        file.eachByte 4096, { byte[] bytes, int size ->
            md.update(bytes, 0, size)
        }
        return md.digest().collect { String.format "%02x", it }.join()
    }

    void apply(Project project) {
        project.extensions.create("dependencyVerification", DependencyVerificationPluginExtension)
        installChecksumVerification(project)
        installChecksumsTask(project)
    }

    private installChecksumVerification(Project project) {
        project.afterEvaluate {
            try {
                verifyChecksums(project)
            } catch (Exception e) {
                logger.error("Error verifying checksums for $project.")
                throw e
            }
        }
    }

    private void verifyChecksums(Project project) {
        final Set<Configuration> configurations = getConfigurations(project)

        if (configurations == null || configurations.isEmpty()) {
            throw new IllegalStateException("No configurations set for 'dependency-verification' plugin ($project).")
        } else {
            final Set<ChecksumAssertion> unusedAssertions =
                new HashSet<>(project.dependencyVerification.assertionsByModule.values())
            configurations.each { Configuration configuration ->
                final Set<ChecksumAssertion> assertionsUsedForConfiguration = verifyChecksumsForConfiguration(project,
                    configuration)
                unusedAssertions.removeAll(assertionsUsedForConfiguration)
            }
            unusedAssertions.each { ChecksumAssertion assertion ->
                logger.warn("Unused ${assertion.displayName()}.")
            }
        }
    }

    private Set<ChecksumAssertion> verifyChecksumsForConfiguration(Project project, Configuration configuration) {
        final Set<ChecksumAssertion> usedAssertions = new HashSet<>()
        final Set<ChecksumAssertion> missingAssertions = new HashSet<>()
        final ArtifactCollection incomingArtifactCollection = getIncomingArtifactCollection(project, configuration)
        incomingArtifactCollection.each {
            final ComponentIdentifier identifier = it.id.componentIdentifier
            if (identifier instanceof ModuleComponentIdentifier) {
                try {
                    final ChecksumAssertion assertionUsed = verifyModuleChecksum(
                        project,
                        configuration,
                        new SimpleModuleIdentifier(identifier.moduleIdentifier),
                        it.file
                    )

                    usedAssertions.add(assertionUsed)
                } catch (MissingChecksumAssertionsException e) {
                    missingAssertions.addAll(e.missingAssertions)
                }
            } else if (identifier instanceof ProjectComponentIdentifier) {
                logger.info("Skipped checksum verification for project-local dependency $identifier.")
            } else if (identifier instanceof ComponentArtifactIdentifier) {
                logger.info("Skipped checksum verification for local file dependency $identifier.")
            } else {
                throw new IllegalStateException("Unexpected component identifier type (${identifier.class}) for " +
                    "identifier '$identifier'.")
            }

        }
        if (missingAssertions.isEmpty()) {
            project.logger.quiet("All dependency checksums of ${configuration} have been successfully verified.")
        } else {
            final StringBuilder sb = new StringBuilder()
            sb.append("Missing integrity assertion for $configuration.").append('\n')
                .append('Consider adding the following to the list of assertion definitions:').append('\n')

            missingAssertions.sort().each { ChecksumAssertion missingAssertion ->
                sb.append(missingAssertion.definition()).append('\n')
            }

            if (project.dependencyVerification.strict) {
                throw new MissingChecksumAssertionsException(sb.toString(), missingAssertions)
            } else {
                logger.warn(sb.toString())
            }
        }
        return usedAssertions
    }

    private static ArtifactCollection getIncomingArtifactCollection(Project project, Configuration configuration) {
        if (Util.isAndroidProject(project)) {
            configuration.incoming.artifactView { config ->
                config.attributes { container ->
                    container.attribute(Attribute.of("artifactType", String.class), "android-classes")
                }
            }.artifacts
        } else {
            configuration.incoming.artifacts
        }
    }

    private ChecksumAssertion verifyModuleChecksum(Project project, Configuration configuration,
                                                   SimpleModuleIdentifier module, File file) {
        final ChecksumAssertion userDefinedAssertion = project.dependencyVerification.assertionsByModule.get(module)
        final def dependencyFileChecksum = calculateSha256(file)
        if (userDefinedAssertion == null) {
            final ChecksumAssertion missingAssertion = new ChecksumAssertion(module, dependencyFileChecksum)
            final def msg = "No integrity assertion for ${module.displayName()} ($configuration).\n" +
                "Consider adding '${missingAssertion.definition()}' to the list of assertion definitions."

            throw new MissingChecksumAssertionsException(msg, missingAssertion)
        } else {

            if (userDefinedAssertion.checksum != dependencyFileChecksum) {
                throw new InvalidUserDataException("Checksum mismatch for ${userDefinedAssertion.displayName()}, "
                    + "$configuration (actual checksum: '$dependencyFileChecksum').")
            } else {
                logger.info("Checksum match successfully verified for ${userDefinedAssertion.displayName()}, "
                    + "$configuration.")
                return userDefinedAssertion
            }

        }
    }

    private static boolean isProjectDependency(Project project, ModuleIdentifier moduleIdentifier) {
        project.group == moduleIdentifier.group
    }

    private void installChecksumsTask(Project project) {
        project.task('checksums') {
            doLast {
                printAssertions(buildAssertions(project))
            }
        }
    }

    private Map<Configuration, SortedSet<ChecksumAssertion>> buildAssertions(Project project) {
        final Map<Configuration, SortedSet<ChecksumAssertion>> assertionsByConfiguration = new HashMap<>()
        getConfigurations(project).each { Configuration configuration ->
            Set<ChecksumAssertion> assertions = assertionsForConfiguration(project, configuration)
            assertionsByConfiguration[configuration] = assertions

        }
        assertionsByConfiguration
    }

    private SortedSet<ChecksumAssertion> assertionsForConfiguration(Project project, Configuration configuration) {
        final SortedSet<ChecksumAssertion> assertions = new TreeSet<>()
        getIncomingArtifactCollection(project, configuration).each {
            final ComponentIdentifier identifier = it.id.componentIdentifier
            if (identifier instanceof ModuleComponentIdentifier) {
                assertions.add(new ChecksumAssertion(identifier.moduleIdentifier, calculateSha256(it.file)))
            } else if (identifier instanceof ProjectComponentIdentifier) {
                logger.info("Skipped generating dependencyVerification assertion for project-local dependency " +
                    "$identifier.")
            } else if (identifier instanceof ComponentArtifactIdentifier) {
                logger.info("Skipped generating dependencyVerification assertion for local file dependency " +
                    "$identifier.")
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
        println "dependencyVerification {"
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

    private Set<Configuration> getConfigurations(Project project) {
        final Set<Configuration> ret = new HashSet<>()
        for (Object cfg : project.dependencyVerification.configurations) {
            switch (cfg) {
                case String:
                    try {
                        ret.add(project.configurations[cfg as String])
                    } catch (UnknownConfigurationException e) {
                        logger.debug("Unknown configuration '$cfg' on $project.")
                        throw e
                    }
                    break
                case Configuration:
                    ret.add(cfg as Configuration)
                    break
                default:
                    throw new InvalidUserDataException("Illegal configuration specification ($cfg,  class: " +
                        "${cfg?.class}) for $project.")
            }

        }
        return ret
    }
}

class DependencyVerificationPluginExtension {
    private Map<ModuleIdentifier, ChecksumAssertion> assertionsByModule = new HashMap<>()
    Set configurations = []
    boolean strict = false

    void configuration(String configuration) {
        configurations.add(configuration)
    }

    void configuration(Configuration configuration) {
        configurations.add(configuration)
    }

    void verifyModule(String s) {
        ChecksumAssertion assertion = new ChecksumAssertion(s)
        assertionsByModule[assertion.artifactIdentifier] = assertion
    }

    Map<ModuleIdentifier, ChecksumAssertion> getAssertionsByModule() {
        return assertionsByModule.<ModuleIdentifier, ChecksumAssertion> asImmutable()
    }

    Object getConfiguration() {
        return configuration
    }
}

class MissingChecksumAssertionsException extends IllegalStateException {
    private final Set<ChecksumAssertion> missingAssertions = new HashSet<>()

    MissingChecksumAssertionsException(String msg, ChecksumAssertion missingAssertion) {
        super(msg)
        missingAssertions.add(missingAssertion)
    }

    MissingChecksumAssertionsException(String msg, Set<ChecksumAssertion> assertion) {
        super(msg)
        missingAssertions.addAll(assertion)
    }

    Set<ChecksumAssertion> getMissingAssertions() {
        return missingAssertions.asImmutable()
    }
}