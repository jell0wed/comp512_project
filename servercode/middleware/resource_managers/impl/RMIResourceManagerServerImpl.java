package middleware.resource_managers.impl;

import ResImpl.Trace;
import ResInterface.BackendResourceManager;
import ResInterface.ResourceManager;
import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.RemoteResourceManagerImplementationTypes;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class RMIResourceManagerServerImpl extends AbstractRemoteResourceManager {
    private static final Pattern RMI_URL_PATTERN = Pattern.compile("^\\/\\/([^:]+):([0-9]+)\\/(.*)$");

    private String registryHostname;
    private Integer registryPort;
    private String resourceManagerKey;

    private Registry RMIRegistry;
    private BackendResourceManager proxyRMIInterface;

    public RMIResourceManagerServerImpl(String rmAddress) {
        super(RemoteResourceManagerImplementationTypes.RMI);

        this.parseRMAddress(rmAddress);
        this.initializeRMIInterface();
    }

    private void parseRMAddress(String rmAddress) {
        Matcher rmAddressMatch = RMI_URL_PATTERN.matcher(rmAddress);
        if(!rmAddressMatch.matches()) {
            throw new MiddlewareBaseException("Malformed RMI RMAddress " + rmAddress);
        }

        this.registryHostname = rmAddressMatch.group(1);
        this.registryPort = Integer.parseInt(rmAddressMatch.group(2));
        this.resourceManagerKey = rmAddressMatch.group(3);
    }

    private void initializeRMIInterface() {
        try {
            this.RMIRegistry = LocateRegistry.getRegistry(this.registryHostname, this.registryPort);
            this.proxyRMIInterface = (BackendResourceManager) this.RMIRegistry.lookup(this.resourceManagerKey);
            Trace.info(String.format("Connected to RMI Instance //%s:%d/%s", this.registryHostname, this.registryPort, this.resourceManagerKey));
        } catch (RemoteException | NotBoundException e) {
            throw new MiddlewareBaseException("Unable to initialize the RMI interface.", e);
        }
    }

    @Override
    public BackendResourceManager getResourceManager() {
        return this.proxyRMIInterface;
    }
}
