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
public class ReserveCarRequest extends MiddlewareBaseTCPRequest {
    public final int customerId;
    public final String location;

    public ReserveCarRequest(int cid, String loc) {
        super(MiddlewareTCPRequestTypes.RESERVE_CAR_REQUEST);
        this.customerId = cid;
        this.location = loc;
    }

    @Override
    public MiddlewareBaseTCPResponse executeRequest(MiddlewareServer server) {
        try {
            boolean success = server.getMiddlewareInterface().reserveCar(this.id, this.customerId, this.location);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
