package com.generalbytes.gradle

import com.generalbytes.gradle.exception.MismatchedChecksumsException
import com.generalbytes.gradle.exception.MissingChecksumAssertionsException
import com.generalbytes.gradle.model.ChecksumAssertion
import com.generalbytes.gradle.model.SimpleModuleVersionIdentifier
import com.generalbytes.gradle.plugin.DependencyVerificationPlugin
import com.generalbytes.gradle.plugin.DependencyVerificationPluginExtension
import com.generalbytes.gradle.task.DependencyChecksums
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.attributes.Attribute

import java.security.MessageDigest

class DependencyVerificationHelper {

    static void verifyChecksums(Project project, Set<Object> configurations, Set<ChecksumAssertion> assertions,
                                boolean failOnChecksumError, boolean printUnusedAssertions) {
        final List<Configuration> configurationsToCheck = toConfigurations(project, configurations).sort {
            Configuration a, Configuration b ->
                (a.name <=> b.name)
        }
        final Map<ModuleComponentIdentifier, ChecksumAssertion> assertionsByModule =
            assertions.collectEntries({ [(it.artifactIdentifier): it] })
        if (configurationsToCheck == null || configurationsToCheck.isEmpty()) {
            project.logger.warn("No configurations set for checksum verification.")
        } else {
            final Set<ChecksumAssertion> unusedAssertions = printUnusedAssertions ? new TreeSet<>(assertions) : []
            configurationsToCheck.each { Configuration configuration ->

                final Set<ChecksumAssertion> assertionsUsedForConfiguration =
                    verifyChecksumsForConfiguration(project, configuration, assertionsByModule, failOnChecksumError)

                if (printUnusedAssertions) {
                    unusedAssertions.removeAll(assertionsUsedForConfiguration)
                }
            }
            project.logger.info("Dependency verification for $project completed successfuly.")

            if (printUnusedAssertions) {
                printUnused(project, unusedAssertions)
            }
        }
    }

    private static void printUnused(Project project, Set<ChecksumAssertion> unusedAssertions) {
        if (unusedAssertions != null && !unusedAssertions.isEmpty()) {
            final StringBuilder sb = new StringBuilder()
            sb.append('Unused assertions:\n')
            unusedAssertions.each { ChecksumAssertion assertion ->
                sb.append("    ${assertion.displayName}\n")
            }
            project.logger.warn(sb.toString())
        }
    }

    private static Set<ChecksumAssertion> verifyChecksumsForConfiguration(
        Project project,
        Configuration configuration,
        Map<ModuleComponentIdentifier, ChecksumAssertion> assertionsByModule,
        boolean failOnChecksumError) {

        final Set<ChecksumAssertion> usedAssertions = new HashSet<>()
        final Set<ChecksumAssertion> missingAssertions = new HashSet<>()
        final Map<ChecksumAssertion, String> mismatchedChecksumsByAssertion = new HashMap<>()
        final ArtifactCollection incomingArtifactCollection = getIncomingArtifactCollection(project, configuration)
        incomingArtifactCollection.each {
            final ComponentIdentifier identifier = it.id.componentIdentifier
            if (identifier instanceof ModuleComponentIdentifier) {
                try {
                    final ChecksumAssertion assertionUsed = verifyModuleChecksum(
                        project,
                        configuration,
                        assertionsByModule,
                        /**
                         * inefficient, but no Gradle-internal objects (e.g. MavenUniqueSnapshotComponentIdentifier)
                         * have to be used
                         */
                        SimpleModuleVersionIdentifier.createWithClassifierHeuristics(
                            identifier.toString(),
                            it.file.name
                        ),
                        it.file
                    )


                    usedAssertions.add(assertionUsed)
                } catch (MissingChecksumAssertionsException e) {
                    missingAssertions.addAll(e.missingAssertions)
                } catch (MismatchedChecksumsException e) {
                    mismatchedChecksumsByAssertion.putAll(e.mismatchedChecksumsByAssertion)
                }
            } else if (identifier instanceof ProjectComponentIdentifier) {
                project.logger.info("Skipped checksum verification for project-local dependency $identifier.")
            } else if (identifier instanceof ComponentArtifactIdentifier) {
                project.logger.info("Skipped checksum verification for local file dependency $identifier.")
            } else {
                throw new IllegalStateException(
                    "Unexpected component identifier type (${identifier.class}) for identifier '$identifier'.")
            }

        }
        if (missingAssertions.isEmpty() && mismatchedChecksumsByAssertion.isEmpty()) {
            project.logger.debug("All dependency checksums of ${configuration} have been successfully verified.")
        } else {
            if (!missingAssertions.isEmpty()) {
                reportMissingAssertions(project, configuration, missingAssertions, failOnChecksumError)
            }
            if (!mismatchedChecksumsByAssertion.isEmpty()) {
                reportChecksumMismatches(project, configuration, mismatchedChecksumsByAssertion, failOnChecksumError)
            }
        }
        return usedAssertions
    }

    private static void reportChecksumMismatches(Project project,
                                                 Configuration configuration,
                                                 Map<ChecksumAssertion, String> mismatchedChecksumsByAssertion,
                                                 boolean failOnChecksumError) {
        final StringBuilder sb = new StringBuilder()
        sb.append("Mismatched checksum(s) for $configuration.").append('\n')
            .append('Consider verifying the following assertions:').append('\n')

        mismatchedChecksumsByAssertion.sort().each { ChecksumAssertion assertion, String actualChecksum ->
            sb.append('    ').append(assertion.displayName).append(", actual checksum: '$actualChecksum'")
                .append('\n')
        }
        if (failOnChecksumError) {
            throw new MismatchedChecksumsException(sb.toString(), mismatchedChecksumsByAssertion)
        } else {
            project.logger.warn(sb.toString())
        }
    }

    private static void reportMissingAssertions(Project project,
                                                Configuration configuration,
                                                Set<ChecksumAssertion> missingAssertions,
                                                boolean failOnChecksumError) {

        final StringBuilder sb = new StringBuilder()
        sb.append("Missing integrity assertion(s) for $configuration.").append('\n')
            .append("    Consider adding the following to the '${DependencyVerificationPluginExtension.BLOCK_NAME}'")
            .append(" block or checksum file:\n")

        missingAssertions.sort().each { ChecksumAssertion missingAssertion ->
            sb.append('        ').append(missingAssertion.definition()).append('\n')
        }

        sb.append('\n')
            .append('    Alternatively, you can run ')
            .append(project.tasks.getByName(DependencyChecksums.TASK_NAME))
            .append(' to generate a complete listing. You might need to disable build failures on verification')
            .append(' problems for the task to run.\n')
            .append("    To do this, include the following snippet in the main Gradle script:\n")
            .append("        allprojects { afterEvaluate { if (pluginManager.hasPlugin('")
            .append("${DependencyVerificationPlugin.ID}')) { dependencyVerifications.failOnChecksumError = false } } }")
            .append('\n')

        if (failOnChecksumError) {
            throw new MissingChecksumAssertionsException(sb.toString(), missingAssertions)
        } else {
            project.logger.warn(sb.toString())
        }
    }

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

    private static ChecksumAssertion verifyModuleChecksum(
        Project project,
        Configuration configuration,
        Map<ModuleComponentIdentifier, ChecksumAssertion> assertionsByModule,
        SimpleModuleVersionIdentifier module,
        File file) {

        final ChecksumAssertion userDefinedAssertion = assertionsByModule.get(module)
        final def dependencyFileChecksum = calculateSha256(file)
        if (userDefinedAssertion == null) {
            final ChecksumAssertion missingAssertion = new ChecksumAssertion(module, dependencyFileChecksum)
            final String msg = ("No integrity assertion for ${module.displayName} ($configuration).\n"
                + "Consider adding '${missingAssertion.definition()}' to the "
                + "'$DependencyVerificationPluginExtension.BLOCK_NAME' block."
            )

            throw new MissingChecksumAssertionsException(msg, missingAssertion)
        } else {

            if (userDefinedAssertion.checksum != dependencyFileChecksum) {
                final String msg = ("Checksum mismatch for ${userDefinedAssertion.displayName}, $configuration (actual"
                    + " checksum: '$dependencyFileChecksum').")
                throw new MismatchedChecksumsException(msg, userDefinedAssertion, dependencyFileChecksum)
            } else {
                final String msg = ("Checksum match successfully verified for ${userDefinedAssertion.displayName}, "
                    + "$configuration.")
                project.logger.info(msg)
                return userDefinedAssertion
            }

        }
    }

    static String calculateSha256(File file) {
        MessageDigest md = MessageDigest.getInstance("SHA-256")
        file.eachByte 4096, { byte[] bytes, int size ->
            md.update(bytes, 0, size)
        }
        return md.digest().collect { String.format "%02x", it }.join()
    }

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
                    throw new InvalidUserDataException(
                        "Illegal configuration specification ($cfg,  class: ${cfg?.class}) for $project.")
            }

        }
        return ret
    }

    static Map<Configuration, SortedSet<ChecksumAssertion>> buildAssertions(Project project,
                                                                            Set<Object> configurations) {
        final Map<Configuration, SortedSet<ChecksumAssertion>> assertionsByConfiguration = new TreeMap<>({
            Configuration a, Configuration b ->
                (a.name <=> b.name)
        })
        toConfigurations(project, configurations).each {
            Configuration configuration ->
                SortedSet<ChecksumAssertion> assertions = assertionsForConfiguration(project, configuration)
                assertionsByConfiguration[configuration] = assertions
        }
        assertionsByConfiguration
    }

    static SortedSet<ChecksumAssertion> assertionsForConfiguration(Project project, Configuration configuration) {
        final SortedSet<ChecksumAssertion> assertions = new TreeSet<>()
        getIncomingArtifactCollection(project, configuration).each {
            final ComponentIdentifier identifier = it.id.componentIdentifier
            if (identifier instanceof ModuleComponentIdentifier) {
                assertions.add(
                    new ChecksumAssertion(
                        SimpleModuleVersionIdentifier.createWithClassifierHeuristics(
                            identifier.toString(),
                            it.file.name
                        ),
                        calculateSha256(it.file)
                    )
                )
            } else if (identifier instanceof ProjectComponentIdentifier) {
                project.logger.info("Skipped generating $DependencyVerificationPluginExtension.BLOCK_NAME assertion " +
                    "for project-local dependency $identifier.")
            } else if (identifier instanceof ComponentArtifactIdentifier) {
                project.logger.info("Skipped generating $DependencyVerificationPluginExtension.BLOCK_NAME assertion " +
                    "for local file dependency $identifier.")
            } else {
                throw new IllegalStateException("Unexpected component identifier type (${identifier.class}) for " +
                    "identifier '$identifier'.")
            }
        }
        assertions
    }
}
