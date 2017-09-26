package middleware;

import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.RemoteResourceManagerFactory;
import middleware.resource_managers.ResourceManagerTypes;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class MiddlewareServer {
    private static final RemoteResourceManagerFactory remoteRMFactory = new RemoteResourceManagerFactory();

    private List<String> availableRMs;
    private Hashtable<ResourceManagerTypes, AbstractRemoteResourceManager> remoteResourceManagers;

    public MiddlewareServer(String... availableRMIs) {
        this.availableRMs = Arrays.asList(availableRMIs);

        this.initializeRemoteResourceManagers();
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


    public static void main(String[] args) {

    }
}
