package rmi.utils;

import ResImpl.Trace;
import ResInterface.ResourceManager;

import javax.annotation.Resource;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

public class RMITestUtils {
    public static ResourceManager initializeRMI() {
        // get a reference to the rmiregistry
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry("localhost", 1099);
            ResourceManager rm = (ResourceManager) registry.lookup("rmMiddleware");

            // get the proxy and the remote reference by rmiregistry lookup

            return rm;
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException("Unable to connect to rmi");
        }
    }

    public static int newUniqueId() {
        return UUID.randomUUID().hashCode();
    }
}
