package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The SumSubInstanceModule is a singleton class responsible for managing service instances
 * across the SumSub extension. The class acts as a central module to register and retrieve
 * various service implementations within a multithreaded environment.
 *
 * <p>Key Features:
 * - Singleton implementation to ensure a single instance throughout the application.
 * - Thread-safe registration and retrieval of services via a ConcurrentHashMap.
 * - Provides access to specific services, such as context objects or the SumSubWebhookParser.
 */
@Slf4j
public class SumSubInstanceModule {

    // Singleton eager initialization
    private static final SumSubInstanceModule INSTANCE = new SumSubInstanceModule();

    private SumSubInstanceModule() {
        // Private constructor to prevent instantiation.
    }

    public static SumSubInstanceModule getInstance() {
        return INSTANCE;
    }

    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    /**
     * Registers a service instance to the SumSubInstanceModule. This method allows associating a specific service
     * implementation with its respective class type, enabling the retrieval of the service at runtime.
     *
     * @param serviceClass the class of the service being registered; used as the key in the service map.
     * @param service      the instance of the service to be registered; must be an instance of the specified class type.
     */
    public void addService(Class<?> serviceClass, Object service) {
        if (!serviceClass.isInstance(service)) {
            log.warn("Cannot add service {} to the Sumsub module, service is not of type {}", serviceClass.getName(), serviceClass.getName());
            return;
        }

        services.put(serviceClass, service);
    }

    /**
     * Retrieves the registered instance of {@link IExtensionContext} from the registered services.
     *
     * @return the IExtensionContext instance registered within the SumSubInstanceModule
     * @throws IllegalStateException if the IExtensionContext has not been registered
     */
    public IExtensionContext getCtx() {
        return getService(IExtensionContext.class);
    }

    /**
     * Retrieves the registered instance of {@link SumSubWebhookParser} from the registered services.
     *
     * @return the SumSubWebhookParser instance registered in the SumSubInstanceModule
     * @throws IllegalStateException if the SumSubWebhookParser has not been registered
     */
    public SumSubWebhookParser getSubWebhookParser() {
        return getService(SumSubWebhookParser.class);
    }

    /**
     * Retrieves a registered service instance by its class type from the service registry.
     * This method ensures type-safe access to services previously registered in the service map.
     *
     * @param <T>          the type of the service to be retrieved
     * @param serviceClass the class of the service to retrieve; serves as the key for accessing the service
     * @return the instance of the requested service, cast to the specified type
     * @throws IllegalStateException if the requested service is not found in the registry
     */
    public <T> T getService(Class<T> serviceClass) {
        Object service = services.get(serviceClass);

        if (service == null) {
            throw new IllegalStateException("Service " + serviceClass.getName() + " not initialized yet");
        }
        return serviceClass.cast(service);
    }

}
