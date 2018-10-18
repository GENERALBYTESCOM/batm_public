package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.Util
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.PluginManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GBGradlePlugin implements Plugin<Project> {
    private Logger logger = LoggerFactory.getLogger('GBGradlePlugin')

    @Override
    void apply(Project project) {
        applyPluginDependencyStrict(project)
        applyPluginDependencySubstitution(project)
        applyPluginDependencyVerification(project)
    }

    private void applyPluginDependencyStrict(Project project) {
        final PluginManager pluginMgr = project.pluginManager
        pluginMgr.apply(StrictDependenciesPlugin.class)
        [
            'compile',
            'compileOnly',
            'compileClasspath',
            'implementation',
            'providedCompile',

            'testCompile',
            'testCompileOnly',
            'testCompileClasspath',
            'testImplementation',

            'debugCompileClasspath',
            'debugAndroidTestCompileClasspath',
            'debugUnitTestCompileClasspath',
            'releaseCompileClasspath',
            'releaseAndroidTestCompileClasspath',
            'releaseUnitTestCompileClasspath'
        ].each {
            project.strictDependencies.nontransitive(it)
        }
        logger.debug("Applied plugin 'com.generalbytes.gradle.dependency.strict'.")
    }

    private void applyPluginDependencySubstitution(Project project) {
        final PluginManager pluginMgr = project.pluginManager
        pluginMgr.apply(DependencySubstitutionPlugin.class)
        logger.debug("Applied plugin 'com.generalbytes.gradle.dependency.substitution'.")
    }

    private void applyPluginDependencyVerification(Project project) {
        final PluginManager pluginMgr = project.pluginManager
        pluginMgr.apply(DependencyVerificationPlugin.class)


        if (Util.isAndroidProject(project)) {
            project.dependencyVerifications.configuration('releaseRuntimeClasspath')
            project.dependencyVerifications.configuration('debugRuntimeClasspath')
        } else if (pluginMgr.hasPlugin('org.gradle.java')) {
            project.dependencyVerifications.configuration('runtime')
        }

        final Configuration buildScriptClasspathConfiguration =
            project.buildscript.configurations.getByName(project.buildscript.CLASSPATH_CONFIGURATION)

        project.dependencyVerifications.configuration(buildScriptClasspathConfiguration)

        project.dependencyVerifications.failOnChecksumError = true
        logger.debug("Applied plugin '${DependencyVerificationPlugin.ID}'.")
    }
}
