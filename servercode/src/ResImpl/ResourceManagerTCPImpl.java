package ResImpl;

import ResImpl.exceptions.RMBaseException;
import ResInterface.ResourceManager;
import middleware.database.ICustomerDatabase;
import middleware.transactions.IDistributedTransactionManager;
import protocol.requests.BaseTCPRequest;
import protocol.responses.BaseTCPResponse;
import ResImpl.exceptions.TransactionException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import transactions.LockManager.LockManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.function.Consumer;

/**
 * Created by jpoisson on 2017-10-05.
 */
public class ResourceManagerTCPImpl implements ResourceManager, Remote {
    protected ServerSocket rmServerSock;
    private TransactionManager transManager = new TransactionManager();
    protected ResourceManagerDatabase rmDb;
    private String name;
    private int port;

    public ResourceManagerTCPImpl(String name, int port) {
        this.rmDb = new ResourceManagerDatabase(this.transManager);
        this.name = name;
        this.port = port;
        this.initializeServerSocket();
        this.listen();
    }

    private void initializeServerSocket() {
        try {
            this.rmServerSock = new ServerSocket(this.port);
            Trace.info(String.format("Initialized TCP ResourceManager Server on //localhost:%d/%s/", this.port, this.name));
        } catch (IOException e) {
            throw new RMBaseException("Unable to configure TCP server socket on port " + this.port, e);
        }
    }

    private void listen() {
        while(true) {
            try {
                Socket clientSock = this.rmServerSock.accept();
                ObjectInputStream clientObjInStream = new ObjectInputStream(clientSock.getInputStream());
                ObjectOutputStream clientObjOutStream = new ObjectOutputStream(clientSock.getOutputStream());
                Trace.info("Accepting incoming Middleware connection " + clientSock.toString());

                while(true) {
                    try {
                        Object requestObj = (BaseTCPRequest) clientObjInStream.readObject();
                        BaseTCPRequest request = (BaseTCPRequest) requestObj;

                        if(request != null) {
                            BaseTCPResponse response = null;
                            try {
                                response = request.executeRequest(this);
                            } catch (TransactionException e) {
                                Trace.error(e.getMessage());
                            }
                            clientObjOutStream.writeObject(response);
                        } else {
                            Trace.error("Invalid request");
                        }
                    } catch (ClassNotFoundException e) {
                        Trace.error("Invalid request");
                    } catch(EOFException e) { // stream reached the end, close the connection
                        break;
                    } catch (IOException e) {
                        Trace.error("IOException while receiving object : " + e.getMessage());
                        break;
                    }
                }
            } catch (IOException e) {
                Trace.error("IOException while accepting incoming client connection");
                Trace.error(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Wrong usage");
            return;
        }

        ResourceManagerTCPImpl tcpImpl = new ResourceManagerTCPImpl(args[0], Integer.valueOf(args[1]));
    }

    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        try {
            return this.rmDb.addFlight(id, flightNum, flightSeats, flightPrice);
        } catch (TransactionException e) {
            return false;
        }
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        try {
            return this.rmDb.addCars(id, location, numCars, price);
        } catch (TransactionException e) {
            return false;
        }
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        try {
            return this.rmDb.addRooms(id, location, numRooms, price);
        } catch (TransactionException e) {
            return false;
        }
    }

    @Override
    public int newCustomer(int id) throws RemoteException {
        try {
            return this.rmDb.newCustomer(id);
        } catch (TransactionException e) {
            return -1;
        }
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        try {
            return this.rmDb.newCustomer(id, cid);
        } catch (TransactionException e) {
            return false;
        }
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
        try {
            return this.rmDb.deleteFlight(id, flightNum);
        } catch (TransactionException e) {
            return false;
        }
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        try {
            return this.rmDb.deleteCars(id, location);
        } catch (TransactionException e) {
            return false;
        }
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        try {
            return this.rmDb.deleteRooms(id, location);
        } catch (TransactionException e) {
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException {
        try {
            return this.rmDb.deleteCustomer(id, customer);
        } catch (TransactionException e) {
            return false;
        }
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException {
        try {
            return this.rmDb.queryFlight(id, flightNumber);
        } catch (TransactionException e) {
            return -1;
        }
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        try {
            return this.rmDb.queryCars(id, location);
        } catch (TransactionException e) {
            return -1;
        }
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        try {
            return this.rmDb.queryRooms(id, location);
        } catch (TransactionException e) {
            return -1;
        }
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException {
        try {
            return this.rmDb.queryCustomerInfo(id, customer);
        } catch (TransactionException e) {
            return "";
        }
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        try {
            return this.rmDb.queryFlightPrice(id, flightNumber);
        } catch (TransactionException e) {
            return -1;
        }
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        try {
            return this.rmDb.queryCarsPrice(id, location);
        } catch (TransactionException e) {
            return -1;
        }
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        try {
            return this.rmDb.queryRoomsPrice(id, location);
        } catch (TransactionException e) {
            return -1;
        }
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException {
        try {
            return this.rmDb.reserveFlight(id, customer, flightNumber);
        } catch (TransactionException e) {
            return false;
        }
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException {
        try {
            return this.rmDb.reserveCar(id, customer, location);
        } catch (TransactionException e) {
            return false;
        }
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd) throws RemoteException {
        try {
            return this.rmDb.reserveRoom(id, customer, locationd);
        } catch (TransactionException e) {
            return false;
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
        return 0;
    }

    @Override
    public boolean commitTransaction(int transId) {
        return false;
    }

    @Override
    public boolean abortTransaction(int transId) {
        return false;
    }

    @Override
    public boolean shutdown() throws RemoteException {
        System.exit(0);
        return true;
    }

    @Override
    public void registerAsMiddlewareBackup(String connectStr) throws RemoteException {
        throw new NotImplementedException();
    }

    @Override
    public void executeReservationOperation(Consumer<ICustomerDatabase> dbOp) throws RemoteException {
        throw new NotImplementedException();
    }

    @Override
    public void executeTransactionOperation(Consumer<IDistributedTransactionManager> transOp) throws RemoteException {
        throw new NotImplementedException();
    }
}
