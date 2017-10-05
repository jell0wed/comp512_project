package middleware;

import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.RemoteResourceManagerFactory;
import middleware.resource_managers.RemoteResourceManagerImplementationTypes;
import middleware.resource_managers.ResourceManagerTypes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Created by jpoisson on 2017-09-28.
 */
public abstract class MiddlewareServer {
    private final RemoteResourceManagerFactory remoteRMFactory;

    private Collection<String> availableRMs;
    private Hashtable<ResourceManagerTypes, AbstractRemoteResourceManager> remoteResourceManagers;
    private MiddlewareInterface middlewareInterface;

    protected abstract void initializeServer();

    protected MiddlewareServer(RemoteResourceManagerImplementationTypes implType, String... availRMs) {
        remoteRMFactory = new RemoteResourceManagerFactory(implType);
        this.availableRMs = Arrays.asList(availRMs);
        this.middlewareInterface = new MiddlewareInterface(this);

        this.initializeRemoteResourceManagers();
        this.initializeServer();

    }

    private void initializeRemoteResourceManagers() {
        this.remoteResourceManagers = new Hashtable<>();

        LinkedList<String> rmStrs = new LinkedList<>();
        this.availableRMs.forEach(rmStrs::push);

        if(rmStrs.size() < 4) {
            throw new MiddlewareBaseException("Not enough RM servers.");
        }

        this.remoteResourceManagers.put(ResourceManagerTypes.CARS_ONLY, remoteRMFactory.createRemoteResourceManager(rmStrs.pop()));
        this.remoteResourceManagers.put(ResourceManagerTypes.FLIGHTS_ONLY, remoteRMFactory.createRemoteResourceManager(rmStrs.pop()));
        this.remoteResourceManagers.put(ResourceManagerTypes.ROOMS_ONLY, remoteRMFactory.createRemoteResourceManager(rmStrs.pop()));
        this.remoteResourceManagers.put(ResourceManagerTypes.OTHERS, remoteRMFactory.createRemoteResourceManager(rmStrs.pop()));
    }

    protected AbstractRemoteResourceManager getRemoteResourceManagerForType(ResourceManagerTypes type) {
        if (!this.remoteResourceManagers.containsKey(type)) {
            throw new MiddlewareBaseException("No remote resource manager for type " + type.toString());
        }

        return this.remoteResourceManagers.get(type);
    }

    protected Collection<AbstractRemoteResourceManager> getAllRemoteResourceManager() {
        return this.remoteResourceManagers.values();
    }

    public MiddlewareInterface getMiddlewareInterface() {
        return this.middlewareInterface;
    }
}
