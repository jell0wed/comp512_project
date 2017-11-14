package ResImpl;

import ResImpl.exceptions.RMBaseException;
import ResInterface.ResourceManager;
import protocol.requests.BaseTCPRequest;
import protocol.requests.impl.*;
import protocol.responses.BaseTCPResponse;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class ResourceManagerTCPClient implements ResourceManager, Remote {
    private String hostname;
    private int port;

    private Socket clientSock;
    private ObjectOutputStream objOut;
    private ObjectInput objIn;

    public ResourceManagerTCPClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

        this.initializeClientSocket();
    }

    private void initializeClientSocket() {
        try {
            this.clientSock = new Socket(this.hostname, this.port);
            this.objOut = new ObjectOutputStream(clientSock.getOutputStream());
            this.objIn = new ObjectInputStream(clientSock.getInputStream());
        } catch (IOException e) {
            throw new RMBaseException("IOException", e);
        }
    }

    private BaseTCPResponse send(BaseTCPRequest req) {
        try {
            objOut.writeObject(req);
            Object respObj = (BaseTCPResponse) objIn.readObject();
            if(respObj == null) {
                throw new RuntimeException("Invalid response from server");
            }
            return (BaseTCPResponse) respObj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RMBaseException("", e);
        }
    }
    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        AddFlightRequest addFlightReq = new AddFlightRequest(flightNum, flightSeats, flightPrice);
        BaseTCPResponse addFlightResp = this.send(addFlightReq);

        return addFlightResp.asSuccessFailureResponse().success;
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        AddCarsRequest addCarReq = new AddCarsRequest(location, numCars, price);
        BaseTCPResponse addCarResp = this.send(addCarReq);

        return addCarResp.asSuccessFailureResponse().success;
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        AddRoomsRequest addRoomReq = new AddRoomsRequest(location, numRooms, price);
        BaseTCPResponse addRoomResp = this.send(addRoomReq);

        return addRoomResp.asSuccessFailureResponse().success;
    }

    @Override
    public int newCustomer(int id) throws RemoteException {
        NewCustomerRequest newCustReq = new NewCustomerRequest();
        BaseTCPResponse newCustResp = this.send(newCustReq);

        return newCustResp.asIntegerResponse().value;
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        NewCustomerWithIdRequest newCustIdReq = new NewCustomerWithIdRequest(cid);
        BaseTCPResponse newCustIdResp = this.send(newCustIdReq);

        return newCustIdResp.asSuccessFailureResponse().success;
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
        DeleteFlightRequest deleteFlightReq = new DeleteFlightRequest(flightNum);
        BaseTCPResponse deleteFlightResp = this.send(deleteFlightReq);

        return deleteFlightResp.asSuccessFailureResponse().success;
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        DeleteCarRequest deleteCarReq = new DeleteCarRequest(location);
        BaseTCPResponse deleteCarResp = this.send(deleteCarReq);

        return deleteCarResp.asSuccessFailureResponse().success;
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        DeleteRoomRequest deleteRoomReq = new DeleteRoomRequest(location);
        BaseTCPResponse deleteRoomResp = this.send(deleteRoomReq);

        return deleteRoomResp.asSuccessFailureResponse().success;
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException {
        DeleteCustomerRequest delCustReq = new DeleteCustomerRequest(customer);
        BaseTCPResponse delCustResp = this.send(delCustReq);

        return delCustResp.asSuccessFailureResponse().success;
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException {
        QueryFlightRequest queryFlightReq = new QueryFlightRequest(flightNumber);
        BaseTCPResponse queryFlightResp = this.send(queryFlightReq);

        return queryFlightResp.asIntegerResponse().value;
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        QueryCarRequest queryCarReq = new QueryCarRequest(location);
        BaseTCPResponse queryCarResp = this.send(queryCarReq);

        return queryCarResp.asIntegerResponse().value;
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        QueryRoomRequest queryRoomReq = new QueryRoomRequest(location);
        BaseTCPResponse queryRoomResp = this.send(queryRoomReq);

        return queryRoomResp.asIntegerResponse().value;
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException {
        QueryCustomerInfoRequest queryCustInfoReq = new QueryCustomerInfoRequest(customer);
        BaseTCPResponse queryCustInfoResp = this.send(queryCustInfoReq);

        return queryCustInfoResp.asStringResponse().value;
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        QueryFlightPriceRequest queryFlightPriceReq = new QueryFlightPriceRequest(flightNumber);
        BaseTCPResponse queryFlightPriceResp = this.send(queryFlightPriceReq);

        return queryFlightPriceResp.asIntegerResponse().value;
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        QueryCarPriceRequest queryCarPriceReq = new QueryCarPriceRequest(location);
        BaseTCPResponse queryCarPriceResp = this.send(queryCarPriceReq);

        return queryCarPriceResp.asIntegerResponse().value;
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        QueryRoomPriceRequest queryRoomPriceReq = new QueryRoomPriceRequest(location);
        BaseTCPResponse queryRoomPriceResp = this.send(queryRoomPriceReq);

        return queryRoomPriceResp.asIntegerResponse().value;
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException {
        ReserveFlightRequest reserveFlightReq = new ReserveFlightRequest(customer, flightNumber);
        BaseTCPResponse reserveFlightResp = this.send(reserveFlightReq);

        return reserveFlightResp.asSuccessFailureResponse().success;
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException {
        ReserveCarRequest reserveCarReq = new ReserveCarRequest(customer, location);
        BaseTCPResponse reserveCarResp = this.send(reserveCarReq);

        return reserveCarResp.asSuccessFailureResponse().success;
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd) throws RemoteException {
        ReserveRoomRequest reserveRoomReq = new ReserveRoomRequest(customer, locationd);
        BaseTCPResponse reserveRoomResp = this.send(reserveRoomReq);

        return reserveRoomResp.asSuccessFailureResponse().success;
    }

    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room) throws RemoteException {
        ItineraryRequest itiReq = new ItineraryRequest(customer, flightNumbers, location, Car, Room);
        BaseTCPResponse itiResp = this.send(itiReq);

        return itiResp.asSuccessFailureResponse().success;
    }

    @Override
    public boolean updateReservedQuantities(int id, String key, int incQty) throws RemoteException {
        UpdateReserveQuantitiesRequest updateQtyReq = new UpdateReserveQuantitiesRequest(key, incQty);
        BaseTCPResponse updateQtyResp = this.send(updateQtyReq);

        return updateQtyResp.asSuccessFailureResponse().success;
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
}
