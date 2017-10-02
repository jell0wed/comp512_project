package middleware.impl.tcp.requests.impl;

import middleware.MiddlewareServer;
import middleware.impl.tcp.requests.MiddlewareBaseTCPRequest;
import middleware.impl.tcp.requests.MiddlewareTCPRequestTypes;
import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.impl.ExceptionResponse;
import middleware.impl.tcp.responses.impl.SuccessFailureResponse;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Created by jpoisson on 2017-09-30.
 */
public class ItineraryRequest extends MiddlewareBaseTCPRequest {
    public final int customerId;
    public final Vector<String> flightNumbers;
    public final String location;
    public final boolean bookCar;
    public final boolean bookRoom;

    public ItineraryRequest(int cid, Vector<String> flightNos, String loc, boolean bookCar, boolean bookRoom) {
        super(MiddlewareTCPRequestTypes.ITINERARY_REQUEST);
        this.customerId = cid;
        this.flightNumbers = flightNos;
        this.location = loc;
        this.bookCar = bookCar;
        this.bookRoom = bookRoom;
    }

    @Override
    public MiddlewareBaseTCPResponse executeRequest(MiddlewareServer server) {
        try {
            boolean success = server.getMiddlewareInterface().itinerary(this.id, this.customerId, this.flightNumbers, this.location, this.bookCar, this.bookRoom);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
