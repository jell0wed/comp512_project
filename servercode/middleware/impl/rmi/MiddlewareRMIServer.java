package middleware.impl.rmi;

import ResImpl.Trace;
import ResInterface.ResourceManager;
import middleware.MiddlewareInterface;
import middleware.MiddlewareServer;
import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.RemoteResourceManagerFactory;
import middleware.resource_managers.ResourceManagerTypes;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class MiddlewareRMIServer extends MiddlewareServer {
    private static final RemoteResourceManagerFactory remoteRMFactory = new RemoteResourceManagerFactory();
    private static final String rmiServer = "localhost";
    private static final String rmiMiddlewareKey = "rmMiddleware";
    private static final int rmiPort = 1099;

    public MiddlewareRMIServer(String... availableRMs) {
        super(availableRMs);
    }

    @Override
    protected void initializeServer() {
        System.setProperty("java.security.policy", "file:///Users/jpoisson/Desktop/comp512/servercode/java.policy");

        try {
            ResourceManager rm = (ResourceManager) UnicastRemoteObject.exportObject(this.getMiddlewareInterface(), 0);
            Registry rmiRegistry = LocateRegistry.getRegistry(rmiPort);
            rmiRegistry.rebind(rmiMiddlewareKey, rm);

            Trace.info(String.format("Started middleware server instance on //%s:%d/%s", rmiServer, rmiPort, rmiMiddlewareKey));
        } catch (RemoteException e) {
            throw new MiddlewareBaseException("Unable to initialize the middleware RMI Server properly.", e);
        }

        /*if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }*/
    }

    public static void main(String[] args) {
        MiddlewareRMIServer middleware = new MiddlewareRMIServer(
            "//localhost:1099/rmCar",
                "//localhost:1099/rmFlight",
                "//localhost:1099/rmRoom",
                "//localhost:1099/rmOther"
        );
    }
}
