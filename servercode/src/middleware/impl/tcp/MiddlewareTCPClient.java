package middleware.impl.tcp;


import ResImpl.Trace;
import middleware.impl.tcp.requests.MiddlewareBaseTCPRequest;
import middleware.impl.tcp.requests.impl.*;
import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.impl.IntegerResponse;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class MiddlewareTCPClient {
    static ObjectOutputStream objOut;
    static ObjectInput objIn;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket clientSock = new Socket("localhost", 8080);
        objOut = new ObjectOutputStream(clientSock.getOutputStream());
        objIn = new ObjectInputStream(clientSock.getInputStream());

        Trace.info("Connected to localhost:8080");
        MiddlewareBaseTCPResponse resp = null;

        // add flight
        AddFlightRequest addFlightReq = new AddFlightRequest(10, 10, 100);
        resp = (MiddlewareBaseTCPResponse) send(addFlightReq);
        Trace.info("(AddFlight) Received response type = " + resp.type);

        // add car
        AddCarsRequest addCarReq = new AddCarsRequest("mtl", 2, 10);
        resp = send(addCarReq);
        Trace.info("(AddCar) Received response type = " + resp.type);

        // add room
        AddRoomsRequest addRoomReq = new AddRoomsRequest("mtl", 2, 10);
        resp = send(addRoomReq);
        Trace.info("(AddRoom) Received response type " + resp.type);

        // add customer
        NewCustomerRequest newCustReq = new NewCustomerRequest();
        resp = send(newCustReq);
        Trace.info("(NewCustomer) Received response type " + resp.type);

        // add customer with id
        NewCustomerWithIdRequest newCustIdReq = new NewCustomerWithIdRequest(1010);
        resp = send(newCustIdReq);
        Trace.info("(NewCustomerWithId) Received response type " + resp.type);

        // delete flight
        DeleteFlightRequest delFlightReq = new DeleteFlightRequest(10);
        resp = send(delFlightReq);
        Trace.info("(DeleteFlight) Received response type " + resp.type);

        // delete car
        DeleteCarRequest delCarReq = new DeleteCarRequest("mtl");
        resp = send(delCarReq);
        Trace.info("(DeleteCar) Received response type " + resp.type);

        // delete room
        DeleteRoomRequest delRoomReq = new DeleteRoomRequest("mtl");
        resp = send(delRoomReq);
        Trace.info("(DeleteRoom) Received response type " + resp.type);

        // delete customer
        DeleteCustomerRequest delCustomerReq = new DeleteCustomerRequest(1010);
        resp = send(delCustomerReq);
        Trace.info("(DeleteCustomer) Received response type " + resp.type);

        // query flight
        QueryFlightRequest queryFlightReq = new QueryFlightRequest(10);
        resp = send(queryFlightReq);
        Trace.info("(QueryFlight) Received response type " + resp.type);

        // query cars
        QueryCarRequest queryCarReq = new QueryCarRequest("mtl");
        resp = send(queryCarReq);
        Trace.info("(QueryCar) Received response type " + resp.type);

        // query rooms
        QueryRoomRequest queryRoomReq = new QueryRoomRequest("mtl");
        resp = send(queryRoomReq);
        Trace.info("(QueryRoom) Received response type " + resp.type);

        NewCustomerWithIdRequest newCustIdReq2 = new NewCustomerWithIdRequest(2020);
        resp = send(newCustIdReq2);
        Trace.info("(NewCustomerWithId) Received response type " + resp.type);

        // query customer infos
        QueryCustomerInfoRequest queryCustInfoReq = new QueryCustomerInfoRequest(2020);
        resp = send(queryCustInfoReq);
        Trace.info("(QueryCustomerInfos) Received response type " + resp.type);

        // query flight price
        QueryFlightPriceRequest queryFlightPriceReq = new QueryFlightPriceRequest(10);
        resp = send(queryFlightPriceReq);
        Trace.info("(QueryFlightPrice) Received response type " + resp.type);

        // query car price
        QueryCarPriceRequest queryCarPriceReq = new QueryCarPriceRequest("mtl");
        resp = send(queryCarPriceReq);
        Trace.info("(QueryCarPrice) Received response type " + resp.type);

        // query room price
        QueryRoomPriceRequest queryRoomPriceReq = new QueryRoomPriceRequest("mtl");
        resp = send(queryRoomPriceReq);
        Trace.info("(QueryRoomPrice) Received response type " + resp.type);

        // add customer with id
        NewCustomerRequest newCustId2Req = new NewCustomerRequest();
        resp = send(newCustId2Req);
        Trace.info("(NewCustomer) Received response type " + resp.type);
        int custId = ((IntegerResponse) resp).value;

        // add flight
        AddFlightRequest addFlight2Req = new AddFlightRequest(10, 10, 100);
        resp = (MiddlewareBaseTCPResponse) send(addFlight2Req);
        Trace.info("(AddFlight) Received response type = " + resp.type);

        // reserve flight
        ReserveFlightRequest reserveFlightReq = new ReserveFlightRequest(custId, 10);
        resp = send(reserveFlightReq);
        Trace.info("(ReserveFlight) Received response type " + resp.type);

        // reserve car
        ReserveCarRequest reserveCarReq = new ReserveCarRequest(custId, "mtl");
        resp = send(reserveCarReq);
        Trace.info("(ReserveCar) Received response type " + resp.type);

        // reserve room
        ReserveRoomRequest reserveRoomReq = new ReserveRoomRequest(custId, "mtl");
        resp = send(reserveRoomReq);
        Trace.info("(ReserveRoom) Received response type " + resp.type);

        // itinerary
        Vector<String> fNos = new Vector<String>();
        fNos.add("10");
        ItineraryRequest itReq = new ItineraryRequest(custId, fNos, "mtl", true, true);
        resp = send(itReq);
        Trace.info("(Itinerary) Received response type " + resp.type);
    }

    public static MiddlewareBaseTCPResponse send(MiddlewareBaseTCPRequest req) throws IOException, ClassNotFoundException {
        objOut.writeObject(req);
        Object respObj = (MiddlewareBaseTCPResponse) objIn.readObject();
        if(respObj == null) {
            throw new RuntimeException("Invalid response from server");
        }

        return (MiddlewareBaseTCPResponse) respObj;
    }
}
