package clients;

import ResInterface.ResourceManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    public static void main(String[] args) {
        try
        {
            // get a reference to the rmiregistry
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            // get the proxy and the remote reference by rmiregistry lookup
            ResourceManager rm = (ResourceManager) registry.lookup("rmMiddleware");
            if(rm!=null)
            {
                System.out.println("Successful");
                System.out.println("Connected to RM");
            }
            else
            {
                System.out.println("Unsuccessful");
            }

            // Test add
            int transId = rm.startTransaction();
            rm.addCars(transId, "mtl", 1, 100);
            rm.commitTransaction(transId);

            // Test read
            int transId2 = rm.startTransaction();
            rm.queryCars(transId2, "mtl");
            rm.commitTransaction(transId2);

            // Test write + read (convert)
            int transId3 = rm.startTransaction();
            rm.queryCars(transId3, "mtl");
            rm.addCars(transId3, "mtl", 1, 200);
            rm.abortTransaction(transId3);
        }
        catch (Exception e)
        {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
