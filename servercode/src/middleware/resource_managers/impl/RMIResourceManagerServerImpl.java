package middleware.resource_managers.impl;

import ResImpl.Trace;
import ResInterface.ResourceManager;
import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.RemoteResourceManagerImplementationTypes;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;
import utils.RMIStringUtils;

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
    private String registryHostname;
    private Integer registryPort;
    private String resourceManagerKey;

    private Registry RMIRegistry;
    private ResourceManager proxyRMIInterface;

    public RMIResourceManagerServerImpl(String rmAddress) {
        super(RemoteResourceManagerImplementationTypes.RMI);

        this.parseRMAddress(rmAddress);
        this.initializeRMIInterface();
    }

    private void initializeRMIInterface() {
        try {
            this.RMIRegistry = LocateRegistry.getRegistry(this.registryHostname, this.registryPort);
            this.proxyRMIInterface = (ResourceManager) this.RMIRegistry.lookup(this.resourceManagerKey);
            Trace.info(String.format("Connected to RMI Instance //%s:%d/%s",
                    this.registryHostname,
                    this.registryPort,
                    this.resourceManagerKey));
        } catch (RemoteException | NotBoundException e) {
            throw new MiddlewareBaseException("Unable to initialize the RMI interface.", e);
        }
    }

    private void parseRMAddress(String rmAddress) {
        Matcher rmAddressMatch = RMIStringUtils.RMI_URL_PATTERN.matcher(rmAddress);
        if(!rmAddressMatch.matches()) {
            throw new MiddlewareBaseException("Malformed RMI RMAddress " + rmAddress);
        }

        this.registryHostname = rmAddressMatch.group(1);
        this.registryPort = Integer.parseInt(rmAddressMatch.group(2));
        this.resourceManagerKey = rmAddressMatch.group(3);
    }



    @Override
    public ResourceManager getResourceManager() {
        return this.proxyRMIInterface;
    }

    private static void gc() {
        try {
            // trigger GC
            byte[][] tooLarge = new byte[Integer.MAX_VALUE][Integer.MAX_VALUE];
        } catch (OutOfMemoryError ex) {
            System.gc(); // ignore
        }
    }



    public static void main(String[] args) {
        ReferenceMap map = new ReferenceMap(AbstractReferenceMap.ReferenceStrength.WEAK, AbstractReferenceMap.ReferenceStrength.WEAK);
        map.put(new Integer(1), new Integer(1));

        gc();

        Integer i = new Integer(1);
        System.out.print("Contains elem after gc = " + map.containsKey(i));


    }
}
