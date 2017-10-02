package middleware.impl.tcp.requests.impl;

import middleware.MiddlewareServer;
import middleware.impl.tcp.requests.MiddlewareBaseTCPRequest;
import middleware.impl.tcp.requests.MiddlewareTCPRequestTypes;
import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.impl.ExceptionResponse;
import middleware.impl.tcp.responses.impl.SuccessFailureResponse;

import java.rmi.RemoteException;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class AddRoomsRequest extends MiddlewareBaseTCPRequest {
    public final String location;
    public final int numRooms;
    public final int price;

    public AddRoomsRequest(String loc, int numRooms, int price) {
        super(MiddlewareTCPRequestTypes.ADD_ROOMS_REQUEST);
        this.location = loc;
        this.numRooms = numRooms;
        this.price = price;
    }

    @Override
    public MiddlewareBaseTCPResponse executeRequest(MiddlewareServer server) {
        try {
            boolean success = server.getMiddlewareInterface().addRooms(this.id, this.location, this.numRooms, this.price);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
