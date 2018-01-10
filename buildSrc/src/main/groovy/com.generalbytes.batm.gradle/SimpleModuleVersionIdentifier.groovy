package com.generalbytes.batm.gradle

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Matcher
import java.util.regex.Pattern

@EqualsAndHashCode
@ToString
class SimpleModuleVersionIdentifier implements ModuleVersionIdentifier {
    private Logger logger = LoggerFactory.getLogger(DependencySubstitutionPluginExtension.class.simpleName)
    final SimpleModuleIdentifier module
    final String version

    SimpleModuleVersionIdentifier(String id) {
        final Matcher matcher = Pattern.compile('^([^:]*):([^:]*):([^:]*)$').matcher(id)
        if (!matcher.matches()) {
            def msg = "Module identifier '$id' has incorrect format."
//            logger.error(msg)
            throw new IllegalArgumentException(msg)
        }
        this.module = new SimpleModuleIdentifier(matcher.group(1), matcher.group(2))
        this.version = matcher.group(3)
    }

    SimpleModuleVersionIdentifier(String group, String name, String version) {
        this.version = version
        this.module = new SimpleModuleIdentifier(group, name)
    }

    @Override
    String getGroup() {
        return module.group
    }

    @Override
    String getName() {
        return module.name
    }
}
