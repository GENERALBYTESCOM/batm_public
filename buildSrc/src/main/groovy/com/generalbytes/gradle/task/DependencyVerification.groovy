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
package com.generalbytes.gradle.task

import com.generalbytes.gradle.Util
import com.generalbytes.gradle.exception.MismatchedChecksumsException
import com.generalbytes.gradle.exception.MissingChecksumAssertionsException
import com.generalbytes.gradle.model.ChecksumAssertion
import com.generalbytes.gradle.model.SimpleModuleIdentifier
import groovy.transform.PackageScope
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.attributes.Attribute
import org.gradle.api.tasks.TaskAction

import java.security.MessageDigest

class DependencyVerification extends DefaultTask {
    public static final String TASK_NAME = 'dependencyVerification'

    Map<ModuleIdentifier, ChecksumAssertion> assertionsByModule = new HashMap<>()
    Set<Object> configurations = []
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

    @TaskAction
    private void verifyChecksums() {
        final Set<Configuration> configurationsToCheck = toConfigurations(project, configurations)

        if (configurationsToCheck == null || configurationsToCheck.isEmpty()) {
            throw new IllegalStateException("No configurations set for checksum verification.")
        } else {
            final Set<ChecksumAssertion> unusedAssertions = new HashSet<>(assertionsByModule.values())
            configurationsToCheck.each { Configuration configuration ->
                final Set<ChecksumAssertion> assertionsUsedForConfiguration =
                    verifyChecksumsForConfiguration(configuration)
                unusedAssertions.removeAll(assertionsUsedForConfiguration)
            }
            unusedAssertions.each { ChecksumAssertion assertion ->
                logger.warn("Unused ${assertion.displayName()}.")
            }
        }
    }

    protected Set<ChecksumAssertion> verifyChecksumsForConfiguration(Configuration configuration) {
        final Set<ChecksumAssertion> usedAssertions = new HashSet<>()
        final Set<ChecksumAssertion> missingAssertions = new HashSet<>()
        final Map<ChecksumAssertion, String> mismatchedChecksumsByAssertion = new HashMap<>()
        final ArtifactCollection incomingArtifactCollection = getIncomingArtifactCollection(project, configuration)
        incomingArtifactCollection.each {
            final ComponentIdentifier identifier = it.id.componentIdentifier
            if (identifier instanceof ModuleComponentIdentifier) {
                try {
                    final ChecksumAssertion assertionUsed = verifyModuleChecksum(
                        configuration,
                        new SimpleModuleIdentifier(identifier.moduleIdentifier),
                        it.file
                    )

                    usedAssertions.add(assertionUsed)
                } catch (MissingChecksumAssertionsException e) {
                    missingAssertions.addAll(e.missingAssertions)
                } catch (MismatchedChecksumsException e) {
                    mismatchedChecksumsByAssertion.putAll(e.mismatchedChecksumsByAssertion)
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
        if (missingAssertions.isEmpty() && mismatchedChecksumsByAssertion.isEmpty()) {
            logger.quiet("All dependency checksums of ${configuration} have been successfully verified.")
        } else {
            if (!missingAssertions.isEmpty()) {
                reportMissingAssertions(configuration, missingAssertions, strict)
            }
            if (!mismatchedChecksumsByAssertion.isEmpty()) {
                reportChecksumMismatches(configuration, mismatchedChecksumsByAssertion)
            }
        }
        return usedAssertions
    }

    private static void reportChecksumMismatches(Configuration configuration,
                                                 Map<ChecksumAssertion, String> mismatchedChecksumsByAssertion) {
        final StringBuilder sb = new StringBuilder()
        sb.append("Mismatched checksum(s) for $configuration.").append('\n')
            .append('Consider verifying the following assertions:').append('\n')

        mismatchedChecksumsByAssertion.sort().each { ChecksumAssertion assertion, String actualChecksum ->
            sb.append('    ').append(assertion.displayName()).append(", actual checksum: '$actualChecksum'")
                .append('\n')
        }
        throw new MismatchedChecksumsException(sb.toString(), mismatchedChecksumsByAssertion)
    }

    private void reportMissingAssertions(Configuration configuration,
                                         Set<ChecksumAssertion> missingAssertions,
                                         boolean strict) {

        final StringBuilder sb = new StringBuilder()
        sb.append("Missing integrity assertion(s) for $configuration.").append('\n')
            .append("Consider adding the following to the '$TASK_NAME' block:").append('\n')

        missingAssertions.sort().each { ChecksumAssertion missingAssertion ->
            sb.append('    ').append(missingAssertion.definition()).append('\n')
        }

        if (strict) {
            throw new MissingChecksumAssertionsException(sb.toString(), missingAssertions)
        } else {
            logger.warn(sb.toString())
        }
    }

    @PackageScope
    static ArtifactCollection getIncomingArtifactCollection(Project project, Configuration configuration) {
        if (Util.isAndroidProject(project)) {
            configuration.incoming.artifactView { config ->
                config.attributes { container ->
//                    container.attribute(Attribute.of("artifactType", String.class), "android-classes")
                    container.attribute(Attribute.of("artifactType", String.class), "jar")
                }
            }.artifacts
        } else {
            configuration.incoming.artifacts
        }
    }

    protected ChecksumAssertion verifyModuleChecksum(Configuration configuration,
                                                   SimpleModuleIdentifier module, File file) {
        final ChecksumAssertion userDefinedAssertion = assertionsByModule.get(module)
        final def dependencyFileChecksum = calculateSha256(file)
        if (userDefinedAssertion == null) {
            final ChecksumAssertion missingAssertion = new ChecksumAssertion(module, dependencyFileChecksum)
            final def msg = "No integrity assertion for ${module.displayName()} ($configuration).\n" +
                "Consider adding '${missingAssertion.definition()}' to the '$TASK_NAME' block."

            throw new MissingChecksumAssertionsException(msg, missingAssertion)
        } else {

            if (userDefinedAssertion.checksum != dependencyFileChecksum) {
                throw new MismatchedChecksumsException(
                    "Checksum mismatch for ${userDefinedAssertion.displayName()}, $configuration (actual checksum: " +
                        "'$dependencyFileChecksum').",
                    userDefinedAssertion,
                    dependencyFileChecksum
                )
            } else {
                logger.info("Checksum match successfully verified for ${userDefinedAssertion.displayName()}, "
                    + "$configuration.")
                return userDefinedAssertion
            }

        }
    }

    @PackageScope
    static String calculateSha256(File file) {
        MessageDigest md = MessageDigest.getInstance("SHA-256")
        file.eachByte 4096, { byte[] bytes, int size ->
            md.update(bytes, 0, size)
        }
        return md.digest().collect { String.format "%02x", it }.join()
    }

    @PackageScope
    static Set<Configuration> toConfigurations(Project project, Set<Object> configurationObjects) {
        final Set<Configuration> ret = new HashSet<>()
        for (Object cfg : configurationObjects) {
            switch (cfg) {
                case String:
                    ret.add(project.configurations[cfg as String])
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
