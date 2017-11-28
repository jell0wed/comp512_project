// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
//
package ResImpl;

import ResImpl.exceptions.DeadlockException;
import ResInterface.ResourceManager;
import ResImpl.exceptions.TransactionException;
import transactions.LockManager.LockManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ResourceManagerRMIImpl implements ResourceManager {
    protected ResourceManagerDatabase rmDb;
    private TransactionManager transManager = new TransactionManager();

    private static final File masterRecordFile = new File("master.bin");
    private static final File shadowRecordFile = new File("shadow.bin");

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
        this.rmDb = new ResourceManagerDatabase(this.transManager);

        if(masterRecordFile.exists()) {
            this.rmDb.loadFromFile(masterRecordFile);
        } else {
            this.rmDb.dumpToFile(masterRecordFile);
        }
    }

    private void handleTransactionException(int transId, TransactionException e) throws TransactionException {
        if(e instanceof DeadlockException) {
            this.abortTransaction(transId);
        }
    }

    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException, TransactionException {
        try {
            return this.rmDb.addFlight(id, flightNum, flightSeats, flightPrice);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException, TransactionException {
        try {
            return this.rmDb.addCars(id, location, numCars, price);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException, TransactionException {
        try {
            return this.rmDb.addRooms(id, location, numRooms, price);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public int newCustomer(int id) throws RemoteException, TransactionException {
        try {
            return this.rmDb.newCustomer(id);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException, TransactionException {
        try {
            return this.rmDb.newCustomer(id, cid);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException, TransactionException {
        try {
            return this.rmDb.deleteFlight(id, flightNum);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException, TransactionException {
        try {
            return this.rmDb.deleteCars(id, location);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException, TransactionException {
        try {
            return this.rmDb.deleteRooms(id, location);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException, TransactionException {
        try {
            return this.rmDb.deleteCustomer(id, customer);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException, TransactionException {
        try {
            return this.rmDb.queryFlight(id, flightNumber);
        } catch (TransactionException e) {
            this.handleTransactionException(id ,e);
            throw e;
        }
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException, TransactionException {
        try {
            return this.rmDb.queryCars(id, location);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException, TransactionException {
        try {
            return this.rmDb.queryRooms(id, location);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException, TransactionException {
        try {
            return this.rmDb.queryCustomerInfo(id, customer);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException, TransactionException {
        try {
            return this.rmDb.queryFlightPrice(id, flightNumber);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException, TransactionException {
        try {
            return this.rmDb.queryCarsPrice(id, location);
        } catch (TransactionException e) {
            throw e;
        }
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException, TransactionException {
        try {
            return this.rmDb.queryRoomsPrice(id, location);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException, TransactionException {
        try {
            return this.rmDb.reserveFlight(id, customer, flightNumber);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException, TransactionException {
        try {
            return this.rmDb.reserveCar(id, customer, location);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd) throws RemoteException, TransactionException {
        try {
            return this.rmDb.reserveRoom(id, customer, locationd);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            throw e;
        }
    }

    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room) throws RemoteException {
        return this.rmDb.itinerary(id, customer, flightNumbers, location, Car, Room);
    }

    @Override
    public boolean updateReservedQuantities(int id, String key, int incQty) throws RemoteException {
        try {
            return this.rmDb.updateReservedQuantities(id, key, incQty);
        } catch (TransactionException e) {
            return false;
        }
    }

    @Override
    public int startTransaction() {
        return this.transManager.initializeTransaction();
    }

    @Override
    public boolean commitTransaction(int transId) {
        try {
            this.transManager.commitTransaction(transId);

            // first write to shadow copy (may be more than 1 IO)
            this.rmDb.dumpToFile(shadowRecordFile);
            // points the master to the shadow
            Files.copy(shadowRecordFile.toPath(), masterRecordFile.toPath());

            return true;
        } catch (TransactionException e) {
            return false;
        } catch (IOException e) {
            Trace.error("IOException while dumping files " + e.getMessage());
            System.exit(1);
            return false;
        }
    }

    @Override
    public boolean abortTransaction(int transId) {
        try {
            this.transManager.abortTransaction(transId, this.rmDb);

            // TODO : do we really need to read A into main memory?
            this.rmDb.loadFromFile(masterRecordFile);

            return true;
        } catch (TransactionException e) {
            return false;
        }
    }

    @Override
    public boolean shutdown() throws RemoteException {
        System.exit(0);
        return true;
    }


}