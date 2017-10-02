package middleware.impl.tcp.requests.impl;

import middleware.MiddlewareServer;
import middleware.impl.tcp.requests.MiddlewareBaseTCPRequest;
import middleware.impl.tcp.requests.MiddlewareTCPRequestTypes;
import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.impl.ExceptionResponse;
import middleware.impl.tcp.responses.impl.SuccessFailureResponse;

import java.rmi.RemoteException;

/**
 * Created by jpoisson on 2017-09-30.
 */
public class ReserveFlightRequest extends MiddlewareBaseTCPRequest {
    public final int customerId;
    public final int flightId;

    public ReserveFlightRequest(int cid, int flightId) {
        super(MiddlewareTCPRequestTypes.RESERVE_FLIGHT_REQUEST);
        this.customerId = cid;
        this.flightId = flightId;
    }

    @Override
    public MiddlewareBaseTCPResponse executeRequest(MiddlewareServer server) {
        try {
            boolean success = server.getMiddlewareInterface().reserveFlight(this.id, this.customerId, this.flightId);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
