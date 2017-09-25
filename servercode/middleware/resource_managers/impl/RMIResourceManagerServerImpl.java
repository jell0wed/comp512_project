package middleware.resource_managers.impl;

import ResInterface.ResourceManager;
import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.RemoteResourceManagerTypes;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class RMIResourceManagerServerImpl extends AbstractRemoteResourceManager {
    private String registryHostname;
    private Integer registryPort;
    private String resourceManagerKey;

    private Registry RMIRegistry;
    private ResourceManager proxyRMIInterface;

    public RMIResourceManagerServerImpl() {
        super(RemoteResourceManagerTypes.RMI);

        this.initializeRMIInterface();
    }

    private void initializeRMIInterface() {
        try {
            this.RMIRegistry = LocateRegistry.getRegistry(this.registryHostname, this.registryPort);
            this.proxyRMIInterface = (ResourceManager) this.RMIRegistry.lookup(this.resourceManagerKey);
        } catch (RemoteException | NotBoundException e) {
            throw new MiddlewareBaseException("Unable to initialize the RMI interface.", e);
        }
    }

    @Override
    public ResourceManager getResourceManager() {
        return this.proxyRMIInterface;
    }
}
