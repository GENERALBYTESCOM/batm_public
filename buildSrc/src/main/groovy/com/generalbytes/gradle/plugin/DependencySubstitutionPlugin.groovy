package com.generalbytes.gradle.plugin

import com.generalbytes.gradle.exception.MissingDependencySubstitutionsException
import com.generalbytes.gradle.model.DependencySubstitution
import com.generalbytes.gradle.model.DependencySubstitutionContext
import com.generalbytes.gradle.model.SimpleModuleIdentifier
import com.generalbytes.gradle.model.SimpleModuleVersionIdentifier
import com.generalbytes.gradle.model.SuggestedSubstitutionChanges
import com.generalbytes.gradle.model.VersionNumberEntry
import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.artifacts.result.DependencyResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.attributes.Attribute
import org.gradle.api.invocation.Gradle
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.stream.Collectors

class DependencySubstitutionPlugin implements Plugin<Project> {
    private static final String SUBSTITUTIONS_FILE = "dependencySubstitutions.txt"
    private static final String PLUGIN_BUILD_DIR_NAME = 'dependencySubstitution'

    private static final Map<Gradle, DependencySubstitutionContext> CONTEXTS_BY_GRADLE_INVOCATION =
        Collections.<Gradle, DependencySubstitutionContext> synchronizedMap(new WeakHashMap<>())
    static final String ID = 'com.generalbytes.gradle.dependency.substitution'

    private DependencySubstitutionPluginExtension extension
    private Logger logger = LoggerFactory.getLogger('DependencySubstitutionPlugin')
    private final DependencySubstitutionContext projectScopedContext = new DependencySubstitutionContext()

    static DependencySubstitutionContext getGlobalContext(Gradle gradle) {
        CONTEXTS_BY_GRADLE_INVOCATION.computeIfAbsent(gradle, { new DependencySubstitutionContext() })
    }

    @Override
    void apply(Project project) {
        extension = project.extensions.create(
            DependencySubstitutionPluginExtension.BLOCK_NAME,
            DependencySubstitutionPluginExtension,
            project
        )

        installPluginActions(project)
    }

    private installPluginActions(Project project) {
        project.afterEvaluate {
            if (extension.shared.get()) {
//                checkMatchingSharedSubstitutions(extension.substitutions.get(), project)
            }
            processConfigurations(project, extension.substitutions.get())
        }
    }

    private static checkMatchingSharedSubstitutions(
        Map<SimpleModuleIdentifier, DependencySubstitution> substitutions,
        Project project
    ) {
        SortedSet<Project> problems = new TreeSet<>(Comparator.<Project, String> comparing({ it.name }))
        project.rootProject.allprojects { Project projectToCheck ->
            if (projectToCheck.pluginManager.hasPlugin(ID)) {
                final DependencySubstitutionPluginExtension extension = getExtensionFromProject(projectToCheck)
                if (extension.shared.get() && substitutions != extension.substitutions.get()) {
                    problems.add(projectToCheck)
                }
            }
        }

        if (!problems.isEmpty()) {
            final StringBuilder sb = new StringBuilder()
            sb.append('The following projects declare using shared dependency substitution definitions but the ')
                .append('definitions differ from those of ').append(project).append(':\n')

            for (Project problematicProject : problems) {
                sb.append("    - $problematicProject\n")
            }
            throw new IllegalStateException(sb.toString())
        }
    }

    private processConfigurations(
        Project project,
        Map<SimpleModuleIdentifier, DependencySubstitution> substitutionsByModule
    ) {
        project.configurations.all { Configuration configuration ->
            if (!shouldSkip(extension, configuration)) {
                installDependencySubstitutions(configuration, substitutionsByModule)
                MissingSubstitutionAction action = extension.missingSubstitutionAction.get()
                if (action == MissingSubstitutionAction.REPORT || action == MissingSubstitutionAction.FAIL) {

                    final DependencySubstitutionContext context = getContext(project)
                    installMissingSubstitutionGenerator(configuration, substitutionsByModule, context)

                    final Set<Project> projects =
                        extension.shared.get() ? project.gradle.rootProject.allprojects : [project] as Set<Project>
                    installResolveAndPrintOnlyOnce(project, projects, context)
                }
            }
        }
    }

    private DependencySubstitutionContext getContext(Project project) {
        extension.shared.get() ? getGlobalContext(project.gradle) : projectScopedContext
    }

    private void installDependencySubstitutions(
        Configuration configuration,
        Map<SimpleModuleIdentifier, DependencySubstitution> substitutionsByModule
    ) {

        configuration.resolutionStrategy {
            eachDependency { DependencyResolveDetails details ->
                final SimpleModuleIdentifier from = new SimpleModuleIdentifier(details.requested.group,
                    details.requested.name)
                final DependencySubstitution substitution = substitutionsByModule.get(from)
                if (substitution != null
                    && substitution.versions.contains(VersionNumberEntry.parse(details.requested.version))
                ) {
                    details.useVersion(substitution.toVersion.string)
                    details.because("${this.getClass().simpleName}: ${substitution.definition}")
                }
            }
            logger.debug("Resolution strategy substitutions set for configuration ${configuration}.")
        }
    }

    private installMissingSubstitutionGenerator(
        Configuration configuration,
        Map<SimpleModuleIdentifier, DependencySubstitution> pluginSubstitutions,
        DependencySubstitutionContext context
    ) {
        configuration.incoming.afterResolve { ResolvableDependencies resolvableDependencies ->
            registerUnknownOrUsedSubstitutions(
                pluginSubstitutions,
                context.usedSubstitutions,
                context.unknownSubstitutions,
                resolvableDependencies
            )
        }
    }

    private installResolveAndPrintOnlyOnce(
        Project project,
        Set<Project> projects,
        DependencySubstitutionContext context
    ) {
        // install only once per context
        context.resolverInstalled.updateAndGet { boolean installed ->
            if (!installed) {
                doInstallResolveAndPrint(project, projects, context)
            }
            return Boolean.TRUE
        }
    }

    private doInstallResolveAndPrint(
        Project project, Set<Project> projects,
        DependencySubstitutionContext context
    ) {
        project.gradle.buildFinished { BuildResult buildResult ->
            if (buildResult.failure == null) {
                /**
                 * Proceed only if something something new has already been generated. There might be a missing
                 * substitution if we process the still unresolved configurations. If such a configuration won't get
                 * resolved, it's probably not used for the currently chosen build tasks.
                 */
                final Map<SimpleModuleIdentifier, DependencySubstitution> pluginSubstitutions =
                    extension.substitutions.get()


                if (
                    hasMissing(pluginSubstitutions, context.usedSubstitutions, context.unknownSubstitutions)
                    || hasRedundant(pluginSubstitutions, context.usedSubstitutions)
                ) {
                    for (Project projectToResolve : projects) {
                        if (projectToResolve.pluginManager.hasPlugin(ID)) {
                            if (false) {//TODO: fix resolve errors?
                                resolveUnresolved(projectToResolve)
                            }
                        }
                    }
                    boolean shared = context != projectScopedContext
                    final SuggestedSubstitutionChanges suggestedChanges = SuggestedSubstitutionChanges.from(
                        pluginSubstitutions,
                        context.unknownSubstitutions,
                        context.usedSubstitutions
                    )
                    generateSubstitutionFile(project, suggestedChanges, shared)

                    reportSuggestedConfigurationChanges(suggestedChanges, project, shared)
                }
            }
        }
    }

    boolean hasRedundant(
        Map<SimpleModuleIdentifier, DependencySubstitution> pluginSubstitutions,
        Map<SimpleModuleVersionIdentifier, VersionNumberEntry> usedSubstitutions
    ) {
        boolean substitutionRedundant = false
outer:          for (DependencySubstitution pluginSubstitution : pluginSubstitutions.values()) {
            for (VersionNumberEntry psVersion : pluginSubstitution.versions) {
                final SimpleModuleVersionIdentifier singlePluginSubstitution = new SimpleModuleVersionIdentifier(
                    pluginSubstitution.fromIdentifier,
                    psVersion.string
                )
                if (!usedSubstitutions.containsKey(singlePluginSubstitution)) {
                    substitutionRedundant = true
                    break outer
                }
            }
        }
        return substitutionRedundant
    }

    boolean hasMissing(
        Map<SimpleModuleIdentifier, DependencySubstitution> pluginSubstitutions,
        Map<SimpleModuleVersionIdentifier, VersionNumberEntry> usedSubstitutions,
        Map<SimpleModuleVersionIdentifier, VersionNumberEntry> unknownSubstitutions
    ) {
        boolean substitutionsMissing = !unknownSubstitutions.isEmpty()
        if (!substitutionsMissing) {
            for (SimpleModuleVersionIdentifier usedSubstitutionFrom : usedSubstitutions.keySet()) {
                final DependencySubstitution pluginSubstitution =
                    pluginSubstitutions.get(usedSubstitutionFrom.moduleIdentifier)
                if (!pluginSubstitution.versions.contains(
                    VersionNumberEntry.parse(usedSubstitutionFrom.version)
                )) {
                    substitutionsMissing = true
                    break
                }

            }
        }
        return substitutionsMissing
    }

    private void reportSuggestedConfigurationChanges(
        SuggestedSubstitutionChanges suggestedChanges,
        Project project,
        boolean shared
    ) {
        final StringBuilder sb = new StringBuilder()
        final String indent = "    "
        final String sharedString = shared ? 'shared' : ''
        sb.append('Suggested configuration changes for ').append(sharedString).append(' dependency substitutions for ')
            .append(project).append('.\n').append(indent)
            .append('Consider appending new (+),')
            .append(' modifying existing(!),')
            .append(" or completely removing (-) substitutions in the '")
            .append(DependencySubstitutionPluginExtension.BLOCK_NAME).append("' block or substitutions file:\n")

        boolean isMissingSubstitution = false
        SortedSet<DependencySubstitution> tmpSorted = new TreeSet<>()

        tmpSorted.addAll(suggestedChanges.addSuggestionsByModule.values())
        tmpSorted.each { DependencySubstitution substitution ->
            isMissingSubstitution = true
            sb.append(indent).append(indent).append('(+) ').append(substitution.definition).append('\n')
        }


        tmpSorted.clear()
        tmpSorted.addAll(suggestedChanges.modifySuggestionsByModule.values())
        tmpSorted.each { DependencySubstitution substitution ->
            if (
            !isMissingSubstitution
                && isVersionMissing(
                substitution,
                suggestedChanges.originalSubstitutions.get(substitution.fromIdentifier)
            )
            ) {
                isMissingSubstitution = true
            }
            sb.append(indent).append(indent).append('(!) ').append(substitution.definition).append('\n')
        }

        tmpSorted.clear()
        tmpSorted.addAll(suggestedChanges.removalSuggestionsByModule.values())
        tmpSorted.each { DependencySubstitution substitution ->
            sb.append(indent).append(indent).append('(-) ').append(substitution.definition).append('\n')
        }


        final File substitutionsFile = substitutionsFile(project, false)
        sb.append('\n')
            .append(indent).append('Alternatively, you can take a look at saved substitutions in file')
            .append(" '$substitutionsFile' file that's available now.\n")

        if (isMissingSubstitution && extension.missingSubstitutionAction.get() == MissingSubstitutionAction.FAIL) {
            throw new MissingDependencySubstitutionsException(sb.toString())
        } else {
            project.logger.warn(sb.toString())
        }
    }

    static boolean isVersionMissing(DependencySubstitution newSubstitution, DependencySubstitution oldSubstitution) {
        return (oldSubstitution.versions - newSubstitution.versions).isEmpty()
    }

    private static resolveUnresolved(Project project) {
        for (Configuration configuration : project.configurations) {
            if (configuration.state == Configuration.State.UNRESOLVED
                && configuration.canBeResolved
                && !shouldSkip(getExtensionFromProject(project), configuration)
            ) {
                configuration.incoming.artifactView { config ->
                    config.attributes.attribute(Attribute.of("artifactType", String.class), "jar")
                }.artifacts.artifacts
            }
        }
    }

    private void registerUnknownOrUsedSubstitutions(
        Map<SimpleModuleIdentifier, DependencySubstitution> pluginSubstitutions,
        Map<SimpleModuleVersionIdentifier, VersionNumberEntry> usedSubstitutions,
        Map<SimpleModuleVersionIdentifier, VersionNumberEntry> unknownSubstitutions,
        ResolvableDependencies resolvableDependencies
    ) {
        resolvableDependencies.resolutionResult.allDependencies.each { DependencyResult result ->
            if (result instanceof ResolvedDependencyResult) {
                if (!(result.requested instanceof ModuleComponentSelector)) {
                    logger.debug("Can't handle requested components other than modules (${result.requested}).")
                } else {
                    final SimpleModuleVersionIdentifier requested = (result.requested as ModuleComponentSelector).with {
                        new SimpleModuleVersionIdentifier(it.group, it.module, it.version)
                    }
                    final SimpleModuleVersionIdentifier selected = result.selected.moduleVersion.with {
                        new SimpleModuleVersionIdentifier(it.group, it.name, it.version)
                    }

                    final DependencySubstitution pluginSubstitution = pluginSubstitutions.get(requested.moduleIdentifier)
                    if (isSelectedByPluginRule(result, pluginSubstitution)) {
                        usedSubstitutions.put(requested, VersionNumberEntry.parse(selected.version))
                    } else if (result.selected.selectionReason.isConflictResolution()
                        && requested.version != selected.version
                    ) {
                        unknownSubstitutions.put(requested, VersionNumberEntry.parse(selected.version))
                    }
                }
            }
        }
    }

    static boolean isSelectedByPluginRule(ResolvedDependencyResult result, DependencySubstitution pluginSubstitution) {
        final SimpleModuleVersionIdentifier requested = (result.requested as ModuleComponentSelector).with {
            new SimpleModuleVersionIdentifier(it.group, it.module, it.version)
        }
        final SimpleModuleVersionIdentifier selected = result.selected.moduleVersion.with {
            new SimpleModuleVersionIdentifier(it.group, it.name, it.version)
        }

        // check modules match (we only support module version substitutions, not module-for-module substitutions)
        if (requested.moduleIdentifier == selected.moduleIdentifier) {

            return (result.selected.selectionReason.isSelectedByRule()
                && pluginSubstitution != null
                && pluginSubstitution.versions.contains(VersionNumberEntry.parse(requested.version))
                && pluginSubstitution.toVersion == VersionNumberEntry.parse(selected.version)
            )
        }


        return false
    }

    private static DependencySubstitutionPluginExtension getExtensionFromProject(Project project) {
        (project.extensions.getByName(DependencySubstitutionPluginExtension.BLOCK_NAME)
            as DependencySubstitutionPluginExtension)
    }

    private static void generateSubstitutionFile(
        Project project,
        SuggestedSubstitutionChanges suggestedSubstitutionChanges,
        boolean shared
    ) {
        final File substitutionsFile = substitutionsFile(project, true)

        PrintStream printStream = null
        try {
            printStream = new PrintStream(substitutionsFile.newOutputStream())

            printStream.println "// generated at ${currentTimestamp()}"
            if (shared) {
                printStream.println "// shared substitutions ($project)"
            } else {
                printStream.println "// substitutions for $project"
            }

            final SortedSet<DependencySubstitution> substitutions = new TreeSet<>()
            substitutions.addAll(suggestedSubstitutionChanges.addSuggestionsByModule.values())
            substitutions.addAll(suggestedSubstitutionChanges.modifySuggestionsByModule.values())
            final Set<DependencySubstitution> unchangedSunstitutions =
                suggestedSubstitutionChanges
                    .originalSubstitutions
                    .values()
                    .stream()
                    .filter({ (
                        !suggestedSubstitutionChanges.removalSuggestionsByModule.containsKey(it.fromIdentifier)
                            && !suggestedSubstitutionChanges.modifySuggestionsByModule.containsKey(it.fromIdentifier)
                    )})
                    .collect(Collectors.toSet())
            substitutions.addAll(unchangedSunstitutions)
            if (!substitutions.isEmpty()) {
                substitutions.each {
                    printStream.println it.definition
                }
            }
        } finally {
            printStream?.close()
        }
    }

    private static boolean shouldSkip(DependencySubstitutionPluginExtension extension, Configuration cfg) {
        return extension.skipConfigurations.get().contains(cfg.name)
    }

    private static File substitutionsFile(Project project, boolean createNew) {
        final File pluginBuildDir = new File(project.buildDir, PLUGIN_BUILD_DIR_NAME)
        if (createNew) {
            pluginBuildDir.mkdirs()
        }
        final File checksumsFile = new File(pluginBuildDir, SUBSTITUTIONS_FILE)
        if (createNew) {
            if (checksumsFile.exists()) {
                checksumsFile.delete()
            }
            checksumsFile.createNewFile()
        }
        checksumsFile
    }

    static String currentTimestamp() {
        new Date().format('yyyy-MM-dd\'T\'HH:mm:ss')
    }
}