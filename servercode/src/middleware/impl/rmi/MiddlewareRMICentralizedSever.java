package middleware.impl.rmi;

import ResImpl.Trace;
import ResInterface.ResourceManager;
import middleware.MiddlewareServer;
import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.RemoteResourceManagerImplementationTypes;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MiddlewareRMICentralizedSever extends MiddlewareServer {
    private static final String rmiServer = "localhost";
    private static final String rmiMiddlewareKey = "rmMiddleware";
    private static final int rmiPort = 1099;

    public MiddlewareRMICentralizedSever() {
        super(RemoteResourceManagerImplementationTypes.RMI,
                "//localhost:1099/rmCentralized",
                "//localhost:1099/rmCentralized",
                "//localhost:1099/rmCentralized",
                "//localhost:1099/rmCentralized");
    }

    @Override
    protected void initializeServer() {
        try {
            ResourceManager rm = (ResourceManager) UnicastRemoteObject.exportObject(this.getMiddlewareInterface(), 0);
            Registry rmiRegistry = LocateRegistry.getRegistry(rmiPort);
            rmiRegistry.rebind(rmiMiddlewareKey, rm);

            Trace.info(String.format("Started src.middleware server instance on //%s:%d/%s", rmiServer, rmiPort, rmiMiddlewareKey));
        } catch (RemoteException e) {
            throw new MiddlewareBaseException("Unable to initialize the src.middleware RMI Server properly.", e);
        }

        /*if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }*/
    }

    public static void main(String[] args) {
        new MiddlewareRMICentralizedSever();
    }
}
