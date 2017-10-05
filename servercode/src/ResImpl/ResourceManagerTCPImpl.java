package ResImpl;

import ResImpl.exceptions.RMBaseException;
import ResInterface.ResourceManager;
import protocol.requests.BaseTCPRequest;
import protocol.responses.BaseTCPResponse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Created by jpoisson on 2017-10-05.
 */
public class ResourceManagerTCPImpl implements ResourceManager, Remote {
    protected ServerSocket rmServerSock;
    protected ResourceManagerDatabase rmDb = new ResourceManagerDatabase();
    private String name;
    private int port;

    public ResourceManagerTCPImpl(String name, int port) {
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
                            BaseTCPResponse response = request.executeRequest(this);
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
        return this.rmDb.deleteCars(id, location);
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        return this.rmDb.deleteRooms(id, location);
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException {
        return this.rmDb.deleteCustomer(id, customer);
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException {
        return this.rmDb.queryFlight(id, flightNumber);
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        return this.rmDb.queryCars(id, location);
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        return this.rmDb.queryRooms(id, location);
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException {
        return this.rmDb.queryCustomerInfo(id, customer);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        return this.rmDb.queryFlightPrice(id, flightNumber);
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        return this.rmDb.queryCarsPrice(id, location);
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        return this.rmDb.queryRoomsPrice(id, location);
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException {
        return this.rmDb.reserveFlight(id, customer, flightNumber);
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
