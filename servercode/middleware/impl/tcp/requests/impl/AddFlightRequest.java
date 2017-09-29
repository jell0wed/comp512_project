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
public class AddFlightRequest extends MiddlewareBaseTCPRequest {
    public final int flightNum;
    public final int flightSeats;
    public final int flightPrice;

    public AddFlightRequest(int flightNum, int flightSeats, int flightPrice) {
        super(MiddlewareTCPRequestTypes.ADD_FLIGHT_REQUEST);
        this.flightNum = flightNum;
        this.flightSeats = flightSeats;
        this.flightPrice = flightPrice;
    }

    @Override
    public MiddlewareBaseTCPResponse executeRequest(MiddlewareServer server) {
        try {
            boolean success = server.getMiddlewareInterface().addFlight(this.id, this.flightNum, this.flightSeats, this.flightSeats);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
