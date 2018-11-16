package com.generalbytes.gradle


import com.generalbytes.gradle.model.ChecksumAssertion
import com.generalbytes.gradle.model.SimpleModuleVersionIdentifier
import com.generalbytes.gradle.plugin.DependencyVerificationPlugin
import com.generalbytes.gradle.task.DependencyChecksums
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.attributes.Attribute
import org.gradle.api.invocation.Gradle

import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import java.util.stream.Collectors

class DependencyVerificationHelper {
    private static final Map<Gradle, ConcurrentMap<String, String>> CACHES_BY_GRADLE_INVOCATION =
        Collections.<Gradle, ConcurrentMap<String, String>> synchronizedMap(new WeakHashMap<>())

    static ConcurrentMap<String, String> getCache(Gradle gradle) {
        CACHES_BY_GRADLE_INVOCATION.entrySet()
        CACHES_BY_GRADLE_INVOCATION.computeIfAbsent(gradle, { new ConcurrentHashMap<>() })
    }

    static class ChecksumComputationTask extends RecursiveTask<Set<ChecksumAssertion>> {
        private final ConcurrentMap<String, String> checksumCache
        private static final boolean PARALLEL = true

        private final Map<ModuleComponentIdentifier, File> jobList

        ChecksumComputationTask(ModuleComponentIdentifier module,
                                File file,
                                ConcurrentMap<String, String> checksumCache) {
            this.checksumCache = checksumCache
            jobList = [(module): file]
        }

        ChecksumComputationTask(Map<ModuleComponentIdentifier, File> jobList,
                                ConcurrentMap<String, String> checksumCache) {
            if (jobList == null) {
                throw new IllegalArgumentException("Job list can't be null.")
            }
            this.checksumCache = checksumCache
            this.jobList = jobList
        }

        @Override
        protected Set<ChecksumAssertion> compute() {
            if (PARALLEL) {
                if (jobList.size() == 1) {
                    final Map.Entry<ModuleComponentIdentifier, File> entry = jobList.entrySet().iterator().next()
                    return [computeChecksum(entry.key, entry.value, checksumCache)] as Set
                } else {
                    return invokeAll(createSubtasks())
                        .parallelStream()
                        .flatMap({ it -> it.join().stream() })
                        .collect(Collectors.toSet())
                }
            } else {
                final Set<ChecksumAssertion> ret = new HashSet<>()
                for (Map.Entry<ModuleComponentIdentifier, File> entry : jobList.entrySet()) {
                    ret.add(computeChecksum(entry.key, entry.value, checksumCache))
                }
                return ret
            }
        }

        Collection<ChecksumComputationTask> createSubtasks() {
            final Collection<ChecksumComputationTask> ret = new HashSet<>()
            for (Map.Entry<ModuleComponentIdentifier, File> job : jobList.entrySet()) {
                ret.add(new ChecksumComputationTask(job.key, job.value, checksumCache))
            }
            return ret
        }

        static ChecksumAssertion computeChecksum(ModuleComponentIdentifier module,
                                                 File file,
                                                 Map<String, String> checksumCache) {
            /**
             * toString() serialization and following deserialization is inefficient, but no Gradle-internal objects
             * (eg. MavenUniqueSnapshotComponentIdentifier) have to be used
             */
            final SimpleModuleVersionIdentifier moduleId =
                SimpleModuleVersionIdentifier.createWithClassifierHeuristics(module.toString(), file.name)

            final String sha256 = (checksumCache != null
                ? checksumCache.computeIfAbsent(file.canonicalPath, { calculateSha256(file) })
                : calculateSha256(file))

            return new ChecksumAssertion(moduleId, sha256)
        }

        static String calculateSha256(File file) {
            MessageDigest md = MessageDigest.getInstance("SHA-256")
            file.eachByte 4096, { byte[] bytes, int size ->
                md.update(bytes, 0, size)
            }
            return md.digest().collect { String.format "%02x", it }.join()
        }
    }

    static Map<Configuration, SortedSet<ChecksumAssertion>> assertionsByConfiguration(Project project,
                                                                                      Set<Object> configurations) {
        final Map<Configuration, SortedSet<ChecksumAssertion>> assertionsByConfiguration = new TreeMap<>({
            Configuration a, Configuration b ->
                (a.name <=> b.name)
        })
        toConfigurations(project, configurations).each { Configuration configuration ->
                SortedSet<ChecksumAssertion> assertions =
                    new TreeSet<>(assertionsForConfiguration(project, configuration))
                assertionsByConfiguration.put(configuration, assertions)
        }
        assertionsByConfiguration
    }

    static Set<ChecksumAssertion> assertionsForConfiguration(Project project,
                                                             Configuration configuration) {
        final Map<ModuleComponentIdentifier, File> jobList = new HashMap()
        getIncomingArtifactCollection(project, configuration).each { ResolvedArtifactResult artifact ->
            final ComponentIdentifier identifier = artifact.id.componentIdentifier
            if (identifier instanceof ModuleComponentIdentifier) {
                jobList.put(identifier, artifact.file)
            } else if (identifier instanceof ProjectComponentIdentifier) {
                project.logger.info("Skipped processing assertion for project-local dependency $identifier.")
            } else if (identifier instanceof ComponentArtifactIdentifier) {
                project.logger.info("Skipped processing assertion for local file dependency $identifier.")
            } else {
                throw new IllegalStateException(
                    "Unexpected component identifier type (${identifier.class}) for identifier '$identifier'.")
            }
        }

        final Set<ChecksumAssertion> computedChecksums =
            ForkJoinPool
                .commonPool()
                .invoke(new ChecksumComputationTask(jobList, getCache(project.gradle))
            )
        return computedChecksums
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

    static SortedSet<ChecksumAssertion> globalAssertions(Project anyProject) {
        final SortedSet<ChecksumAssertion> assertions = new TreeSet<>()
        anyProject.rootProject.allprojects { Project p ->
            if (p.pluginManager.hasPlugin(DependencyVerificationPlugin.ID)) {
                final DependencyChecksums checksumsTask = DependencyChecksums.getChecksumsTask(p)
                final Set<Object> configurations = checksumsTask.configurations.get()
                assertionsByConfiguration(p, configurations).values().each {
                    assertions.addAll(it)
                }
            }
        }
        assertions
    }
}