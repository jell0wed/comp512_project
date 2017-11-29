package middleware.impl.rmi;

import ResImpl.Trace;
import ResInterface.ResourceManager;
import middleware.MiddlewareServer;
import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.RemoteResourceManagerImplementationTypes;
import utils.RMIStringUtils;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.regex.Matcher;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class MiddlewareBackupRMIServer extends MiddlewareServer {
    private static final String rmiServer = "localhost";
    private static final String rmiMiddlewareKey = "rmMiddlewareBackup";
    private static final int rmiPort = 1099;

    public MiddlewareBackupRMIServer(String... availableRMs) {
        super(RemoteResourceManagerImplementationTypes.RMI, availableRMs);
    }

    @Override
    protected void initializeServer() {
        try {
            ResourceManager rm = (ResourceManager) UnicastRemoteObject.exportObject(this.getMiddlewareInterface(), 0);
            Registry rmiRegistry = LocateRegistry.getRegistry(rmiPort);
            rmiRegistry.rebind(rmiMiddlewareKey, rm);

            Trace.info(String.format("Started src.middleware server instance on //%s:%d/%s", rmiServer, rmiPort, rmiMiddlewareKey));
            this.startAsBackup("//localhost:1099/rmMiddleware");
        } catch (RemoteException e) {
            throw new MiddlewareBaseException("Unable to initialize the src.middleware RMI Server properly.", e);
        }

        /*if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }*/
    }

    private void startAsBackup(String connStr) {
        // try to connect to remote middleware through rmi
        Matcher rmAddressMatch = RMIStringUtils.RMI_URL_PATTERN.matcher(connStr);
        if(!rmAddressMatch.matches()) {
            throw new RuntimeException("Invalid connection string");
        }

        String hostname = rmAddressMatch.group(1);
        Integer port = Integer.parseInt(rmAddressMatch.group(2));
        String key = rmAddressMatch.group(3);

        try {
            Registry registry = LocateRegistry.getRegistry(hostname, port);
            ResourceManager mainMiddleware = (ResourceManager) registry.lookup(key);
            mainMiddleware.registerAsMiddlewareBackup("//"+rmiServer+":"+String.valueOf(rmiPort)+"/"+rmiMiddlewareKey);
            Trace.info("Registered itself as a backup");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        MiddlewareBackupRMIServer middleware = new MiddlewareBackupRMIServer(
            "//localhost:1099/rmCar",
                "//localhost:1099/rmFlight",
                "//localhost:1099/rmRoom",
                "//localhost:1099/rmOther"
        );
    }
}
