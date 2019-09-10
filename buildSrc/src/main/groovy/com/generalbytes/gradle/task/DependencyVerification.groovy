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

import com.generalbytes.gradle.DependencyVerificationHelper
import com.generalbytes.gradle.exception.MismatchedChecksumsException
import com.generalbytes.gradle.exception.MissingChecksumAssertionsException
import com.generalbytes.gradle.model.ChecksumAssertion
import com.generalbytes.gradle.plugin.DependencyVerificationPlugin
import com.generalbytes.gradle.plugin.DependencyVerificationPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.logging.Logger
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.TaskAction

class DependencyVerification extends DefaultTask {
    public static final String TASK_NAME = 'dependencyVerification'

    final SetProperty<ChecksumAssertion> assertions = project.objects.setProperty(ChecksumAssertion)
    final SetProperty<Object> configurations = project.objects.setProperty(Object)
    final Property<Boolean> failOnChecksumError = project.objects.property(Boolean)
    final Property<Boolean> printUnusedAssertions = project.objects.property(Boolean)

    DependencyVerification() {
        printUnusedAssertions.set(true)
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
    void failOnChecksumError(boolean failOnChecksumError) {
        this.failOnChecksumError.set(failOnChecksumError)
    }

    @SuppressWarnings('unused')
    void verifyModule(String s) {
        assertions.add(new ChecksumAssertion(s))
    }

    @TaskAction
    @SuppressWarnings('unused')
    private void verify() {
        verifyChecksums(
            project,
            configurations.get(),
            assertions.get(),
            failOnChecksumError.get(),
            printUnusedAssertions.get()
        )
    }

    static void verifyChecksums(Project project, Set<Object> configurations, Set<ChecksumAssertion> assertions,
                                boolean failOnChecksumError, boolean printUnusedAssertions) {
        final List<Configuration> configurationsToCheck =
            DependencyVerificationHelper.toConfigurations(project, configurations).sort {
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

    private static Set<ChecksumAssertion> verifyChecksumsForConfiguration(
        Project project,
        Configuration configuration,
        Map<ModuleComponentIdentifier, ChecksumAssertion> assertionsByModule,
        boolean failOnChecksumError) {

        final Set<ChecksumAssertion> usedAssertions = new HashSet<>()
        final Set<ChecksumAssertion> missingAssertions = new HashSet<>()
        final Map<ChecksumAssertion, String> mismatchedChecksumsByAssertion = new HashMap<>()

        for (ChecksumAssertion computedChecksum :
            DependencyVerificationHelper.assertionsForConfiguration(project, configuration)) {
            try {
                final ChecksumAssertion userDefinedAssertion =
                    assertionsByModule.get(computedChecksum.artifactIdentifier)

                verifyChecksum(configuration, userDefinedAssertion, computedChecksum, project.logger)

                usedAssertions.add(userDefinedAssertion)
            } catch (MissingChecksumAssertionsException e) {
                missingAssertions.addAll(e.missingAssertions)
            } catch (MismatchedChecksumsException e) {
                mismatchedChecksumsByAssertion.putAll(e.mismatchedChecksumsByAssertion)
            }
        }
        if (missingAssertions.isEmpty() && mismatchedChecksumsByAssertion.isEmpty()) {
            project.logger.debug("All dependency checksums of ${configuration} have been successfully verified.")
        } else {
            final File checksumsFile = checksumsFile(project, true)
            PrintStream printStream = null
            try {
                printStream = new PrintStream(checksumsFile.newOutputStream())
                DependencyChecksums.printChecksumsTaskOutput(DependencyChecksums.getChecksumsTask(project), printStream)
            } finally {
                printStream?.close()
            }

            if (!missingAssertions.isEmpty()) {
                reportMissingAssertions(project, configuration, missingAssertions, failOnChecksumError)
            }
            if (!mismatchedChecksumsByAssertion.isEmpty()) {
                reportChecksumMismatches(project, configuration, mismatchedChecksumsByAssertion, failOnChecksumError)
            }
        }
        return usedAssertions
    }

    private static File checksumsFile(Project project, boolean createNew) {
        final File pluginBuildDir = new File(project.buildDir, DependencyVerificationPlugin.PLUGIN_BUILD_DIR_NAME)
        if (createNew) {
            pluginBuildDir.mkdirs()
        }
        final File checksumsFile = new File(pluginBuildDir, DependencyChecksums.CHECKSUMS_FILE)
        if (createNew) {
            if (checksumsFile.exists()) {
                checksumsFile.delete()
            }
            checksumsFile.createNewFile()
        }
        checksumsFile
    }

    private static void verifyChecksum(Configuration configuration,
                                       ChecksumAssertion userDefinedAssertion,
                                       ChecksumAssertion actualModuleChecksum,
                                       Logger logger) {

        if (actualModuleChecksum == userDefinedAssertion) {
            final String msg = ("Checksum match successfully verified for ${userDefinedAssertion.displayName}, "
                + "$configuration.")
            logger.info(msg)
            return
        }

        if (userDefinedAssertion == null) {
            final String msg = ("No integrity assertion for ${actualModuleChecksum.artifactIdentifier.displayName}"
                + " ($configuration).\nConsider adding '${actualModuleChecksum.definition()}' to the "
                + "'$DependencyVerificationPluginExtension.BLOCK_NAME' block."
            )

            throw new MissingChecksumAssertionsException(msg, actualModuleChecksum)
        }

        final String msg = ("Checksum mismatch for ${userDefinedAssertion.displayName}, $configuration (actual"
            + " checksum: '${actualModuleChecksum.checksum}').")
        throw new MismatchedChecksumsException(msg, userDefinedAssertion, actualModuleChecksum.checksum)
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

        final File checksumsFile = checksumsFile(project, false)
        sb.append('\n')
            .append("    Alternatively, you can take a look at saved assertions in file '$checksumsFile' file that's")
            .append(' available now. Its content corresponds to the output of ')
            .append(project.tasks.getByName(DependencyChecksums.TASK_NAME))
            .append(", as configured for $project.\n")
            .append('\n')
            .append('    You can customize the task configuration and run it yourself to')
            .append(' generate a custom listing. However, you might need to disable build failures on verification')
            .append(' problems for the task to run. To do this, include the following snippet in the main Gradle')
            .append(' script:\n')
            .append("        allprojects { afterEvaluate { if (pluginManager.hasPlugin('")
            .append("${DependencyVerificationPlugin.ID}')) { dependencyVerifications.failOnChecksumError = false } } }")
            .append('\n')

        if (failOnChecksumError) {
            throw new MissingChecksumAssertionsException(sb.toString(), missingAssertions)
        } else {
            project.logger.warn(sb.toString())
        }
    }

}
