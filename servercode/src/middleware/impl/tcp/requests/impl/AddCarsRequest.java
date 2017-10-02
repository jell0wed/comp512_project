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
public class AddCarsRequest extends MiddlewareBaseTCPRequest {
    public final String location;
    public final int numCars;
    public final int price;

    public AddCarsRequest(String loc, int numCars, int price) {
        super(MiddlewareTCPRequestTypes.ADD_CARS_REQUEST);
        this.location = loc;
        this.numCars = numCars;
        this.price = price;
    }

    @Override
    public MiddlewareBaseTCPResponse executeRequest(MiddlewareServer server) {
        try {
            boolean success = server.getMiddlewareInterface().addCars(this.id, this.location, this.numCars, this.price);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
