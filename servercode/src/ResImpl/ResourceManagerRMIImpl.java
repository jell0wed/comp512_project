// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
//
package ResImpl;

import ResInterface.ResourceManager;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

public class ResourceManagerRMIImpl implements ResourceManager, Remote {
    
    protected ResourceManagerDatabase rmDb = new ResourceManagerDatabase();


    public static void main(String args[]) {
        // Figure out where server is running
        String server = "localhost";
        int port = 1099;
        String resourceManager = "MyGroupResourceManager";

        if (args.length == 2) {
            server = server + ":" + args[0];
            port = Integer.parseInt(args[0]);
            resourceManager = args[1];
        } else if (args.length != 0 &&  args.length != 1) {
            System.err.println ("Wrong usage");
            System.out.println("Usage: java src.ResImpl.ResourceManagerRMIImpl [port]");
            System.exit(1);
        }

        try {
            // create a new Server object
            ResourceManagerRMIImpl obj = new ResourceManagerRMIImpl();
            // dynamically generate the stub (client proxy)
            ResourceManager rm = (ResourceManager) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry(port);
            registry.rebind(resourceManager, rm);

            System.err.println("Server ready with resource manager name = " + resourceManager);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

        // Create and install a security manager
        /*if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }*/
    }
     
    public ResourceManagerRMIImpl() throws RemoteException {
    }

    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        return this.rmDb.addFlight(id, flightNum, flightSeats, flightPrice);
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        return this.rmDb.addCars(id, location, numCars, price);
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        return this.rmDb.addRooms(id, location, numRooms, price);
    }

    @Override
    public int newCustomer(int id) throws RemoteException {
        return this.rmDb.newCustomer(id);
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        return this.rmDb.newCustomer(id, cid);
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
        return this.rmDb.deleteFlight(id, flightNum);
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        return this.deleteCars(id, location);
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        return this.deleteRooms(id, location);
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException {
        return this.deleteCustomer(id, customer);
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException {
        return this.queryFlight(id, flightNumber);
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        return this.queryCars(id, location);
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        return this.queryRooms(id, location);
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException {
        return this.queryCustomerInfo(id, customer);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        return this.queryFlightPrice(id, flightNumber);
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        return this.queryCarsPrice(id, location);
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        return this.queryRoomsPrice(id, location);
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException {
        return this.reserveFlight(id, customer, flightNumber);
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException {
        return this.rmDb.reserveCar(id, customer, location);
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd) throws RemoteException {
        return this.rmDb.reserveRoom(id, customer, locationd);
    }

    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room) throws RemoteException {
        return this.rmDb.itinerary(id, customer, flightNumbers, location, Car, Room);
    }

    @Override
    public boolean updateReservedQuantities(int id, String key, int incQty) throws RemoteException {
        return this.rmDb.updateReservedQuantities(id, key, incQty);
    }
}