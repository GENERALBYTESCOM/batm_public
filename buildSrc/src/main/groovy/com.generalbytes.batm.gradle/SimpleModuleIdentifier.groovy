package com.generalbytes.batm.gradle

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.gradle.api.artifacts.ModuleIdentifier

@EqualsAndHashCode
@ToString
class SimpleModuleIdentifier implements ModuleIdentifier{
    final String group
    final String name

    SimpleModuleIdentifier(String group, String name) {
        this.group = group
        this.name = name
    }
}
