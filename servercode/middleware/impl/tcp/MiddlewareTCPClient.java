package middleware.impl.tcp;

import ResImpl.Trace;
import middleware.impl.tcp.requests.MiddlewareBaseTCPRequest;
import middleware.impl.tcp.requests.impl.*;
import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
