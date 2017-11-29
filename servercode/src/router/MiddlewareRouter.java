package router;

import ResImpl.Trace;
import ResImpl.exceptions.TransactionException;
import ResInterface.ResourceManager;
import middleware.database.ICustomerDatabase;
import middleware.transactions.IDistributedTransactionManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import java.util.function.Consumer;

public class MiddlewareRouter {
    private static boolean mainMiddlewareAlive = true;
    private static ResourceManager mainMiddleware;
    private static ResourceManager backupMiddleware;

    public static ResourceManager getActiveMiddleware() {
        if(mainMiddlewareAlive) {
            return mainMiddleware;
        } else {
            return backupMiddleware;
        }
    }

    public static void setMainMiddlewareDead() {
        mainMiddlewareAlive = false;
    }

    private static ResourceManager resourceManager = new ResourceManager() {
        @Override
        public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().addFlight(id, flightNum, flightSeats, flightPrice);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().addFlight(id, flightNum, flightSeats, flightPrice);
            }
        }

        @Override
        public boolean addCars(int id, String location, int numCars, int price) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().addCars(id, location, numCars, price);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().addCars(id, location, numCars, price);
            }
        }

        @Override
        public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().addRooms(id, location, numRooms, price);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().addRooms(id, location, numRooms, price);
            }
        }

        @Override
        public int newCustomer(int id) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().newCustomer(id);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().newCustomer(id);
            }
        }

        @Override
        public boolean newCustomer(int id, int cid) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().newCustomer(id, cid);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().newCustomer(id, cid);
            }
        }

        @Override
        public boolean deleteFlight(int id, int flightNum) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().deleteFlight(id, flightNum);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().deleteFlight(id, flightNum);
            }
        }

        @Override
        public boolean deleteCars(int id, String location) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().deleteCars(id, location);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().deleteCars(id, location);
            }
        }

        @Override
        public boolean deleteRooms(int id, String location) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().deleteRooms(id, location);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().deleteRooms(id, location);
            }
        }

        @Override
        public boolean deleteCustomer(int id, int customer) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().deleteCustomer(id, customer);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().deleteCustomer(id, customer);
            }
        }

        @Override
        public int queryFlight(int id, int flightNumber) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().queryFlight(id, flightNumber);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().queryFlight(id, flightNumber);
            }
        }

        @Override
        public int queryCars(int id, String location) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().queryCars(id, location);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().queryCars(id, location);
            }
        }

        @Override
        public int queryRooms(int id, String location) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().queryRooms(id, location);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().queryRooms(id, location);
            }
        }

        @Override
        public String queryCustomerInfo(int id, int customer) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().queryCustomerInfo(id, customer);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().queryCustomerInfo(id, customer);
            }
        }

        @Override
        public int queryFlightPrice(int id, int flightNumber) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().queryFlightPrice(id, flightNumber);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().queryFlightPrice(id, flightNumber);
            }
        }

        @Override
        public int queryCarsPrice(int id, String location) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().queryCarsPrice(id, location);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().queryCarsPrice(id, location);
            }
        }

        @Override
        public int queryRoomsPrice(int id, String location) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().queryRoomsPrice(id, location);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().queryRoomsPrice(id, location);
            }
        }

        @Override
        public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().reserveFlight(id, customer, flightNumber);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().reserveFlight(id, customer, flightNumber);
            }
        }

        @Override
        public boolean reserveCar(int id, int customer, String location) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().reserveCar(id, customer, location);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().reserveCar(id, customer, location);
            }
        }

        @Override
        public boolean reserveRoom(int id, int customer, String locationd) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().reserveRoom(id, customer, locationd);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().reserveRoom(id, customer, locationd);
            }
        }

        @Override
        public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().itinerary(id, customer, flightNumbers, location, Car, Room);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().itinerary(id, customer, flightNumbers, location, Car, Room);
            }
        }

        @Override
        public boolean updateReservedQuantities(int id, String key, int incQty) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().updateReservedQuantities(id, key, incQty);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().updateReservedQuantities(id, key, incQty);
            }
        }

        @Override
        public int startTransaction() throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().startTransaction();
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().startTransaction();
            }
        }

        @Override
        public boolean commitTransaction(int transId) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().commitTransaction(transId);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().commitTransaction(transId);
            }
        }

        @Override
        public boolean abortTransaction(int transId) throws RemoteException, TransactionException {
            try {
                return getActiveMiddleware().abortTransaction(transId);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().abortTransaction(transId);
            }
        }

        @Override
        public boolean shutdown() throws RemoteException {
            try {
                return getActiveMiddleware().shutdown();
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                return getActiveMiddleware().shutdown();
            }
        }

        @Override
        public void registerAsMiddlewareBackup(String connectStr) throws RemoteException {
            try {
                getActiveMiddleware().registerAsMiddlewareBackup(connectStr);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                getActiveMiddleware().registerAsMiddlewareBackup(connectStr);
            }
        }

        @Override
        public void executeReservationOperation(Consumer<ICustomerDatabase> dbOp) throws RemoteException {
            try {
                getActiveMiddleware().executeReservationOperation(dbOp);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                getActiveMiddleware().executeReservationOperation(dbOp);
            }
        }

        @Override
        public void executeTransactionOperation(Consumer<IDistributedTransactionManager> transOp) throws RemoteException {
            try {
                getActiveMiddleware().executeTransactionOperation(transOp);
            } catch (RemoteException e) {
                setMainMiddlewareDead();
                getActiveMiddleware().executeTransactionOperation(transOp);
            }
        }
    };

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry RMIRegistry = LocateRegistry.getRegistry("localhost", 1099);
        mainMiddleware = (ResourceManager) RMIRegistry.lookup("rmMiddleware");
        backupMiddleware = (ResourceManager) RMIRegistry.lookup("rmMiddlewareBackup");

        ResourceManager rm = (ResourceManager) UnicastRemoteObject.exportObject(resourceManager, 0);
        RMIRegistry.rebind("rmMiddlewareRouter", rm);

        Trace.info("Started router on 0.0.0.0:1099/rmMiddlewareRouter");
    }
}
