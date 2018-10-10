package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.Util
import com.generalbytes.gradle.task.DependencyChecksums
import com.generalbytes.gradle.task.DependencyVerification
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
            project.strictDependencies.confine(it)
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

        [DependencyVerification.TASK_NAME, DependencyChecksums.TASK_NAME].each { String taskName ->
            if (Util.isAndroidProject(project)) {
                project.tasks.getByName(taskName).configuration('releaseRuntimeClasspath')
                project.tasks.getByName(taskName).configuration('debugRuntimeClasspath')
            } else if (pluginMgr.hasPlugin('org.gradle.java')) {
                project.tasks.getByName(taskName).configuration('runtime')
            }

            final Configuration buildScriptClasspathConfiguration =
                project.buildscript.configurations.getByName(project.buildscript.CLASSPATH_CONFIGURATION)

            project.tasks.getByName(taskName).configuration(buildScriptClasspathConfiguration)
        }

        project.tasks.getByName(DependencyVerification.TASK_NAME).strict = true
        logger.debug("Applied plugin 'com.generalbytes.gradle.dependency.verification'.")
    }
}
