package com.generalbytes.gradle

import org.gradle.api.Project

class Util {
    static boolean isAndroidProject(Project project) {
        return hasPlugin(project, 'com.android.library') || hasPlugin(project, 'com.android.application')
    }

    static boolean hasPlugin(Project project, String plugin) {
        return project.getPluginManager().hasPlugin(plugin)
    }
}
