package middleware;

import ResInterface.ResourceManager;
import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.RemoteResourceManagerFactory;
import middleware.resource_managers.ResourceManagerTypes;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class MiddlewareServer {
    private static final RemoteResourceManagerFactory remoteRMFactory = new RemoteResourceManagerFactory();
    private static final String rmiServer = "localhost";
    private static final String rmiMiddlewareKey = "Group33Middleware";
    private static final int rmiPort = 8080;

    private List<String> availableRMs;
    private Hashtable<ResourceManagerTypes, AbstractRemoteResourceManager> remoteResourceManagers;
    private MiddlewareInterface middlewareInterface;

    public MiddlewareServer(String... availableRMIs) {
        this.availableRMs = Arrays.asList(availableRMIs);

        this.initializeRemoteResourceManagers();
        this.initializeRMIServer();
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

    private void initializeRMIServer() {
        this.middlewareInterface = new MiddlewareInterface(this);
        try {
            ResourceManager rm = (ResourceManager) UnicastRemoteObject.exportObject(this.middlewareInterface, 0);
            Registry rmiRegistry = LocateRegistry.getRegistry(rmiPort);
            rmiRegistry.rebind(rmiMiddlewareKey, rm);

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
            }
        } catch (RemoteException e) {
            throw new MiddlewareBaseException("Unable to initialize the middleware RMI Server properly.", e);
        }
    }

    public AbstractRemoteResourceManager getRemoteResourceManagerForType(ResourceManagerTypes type) {
        if (!this.remoteResourceManagers.containsKey(type)) {
            throw new MiddlewareBaseException("No remote resource manager for type " + type.toString());
        }

        return this.remoteResourceManagers.get(type);
    }


    public static void main(String[] args) {

    }
}
