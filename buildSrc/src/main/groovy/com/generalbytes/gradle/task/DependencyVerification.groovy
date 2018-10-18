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
import com.generalbytes.gradle.model.ChecksumAssertion
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
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
    private void verifyChecksums() {
        DependencyVerificationHelper.verifyChecksums(
            project,
            configurations.get(),
            assertions.get(),
            failOnChecksumError.get(),
            printUnusedAssertions.get()
        )
    }
}
