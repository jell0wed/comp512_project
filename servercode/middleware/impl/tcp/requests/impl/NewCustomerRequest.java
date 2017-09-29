package middleware.impl.tcp.requests.impl;

import middleware.MiddlewareServer;
import middleware.impl.tcp.requests.MiddlewareBaseTCPRequest;
import middleware.impl.tcp.requests.MiddlewareTCPRequestTypes;
import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.impl.ExceptionResponse;
import middleware.impl.tcp.responses.impl.IntegerResponse;

import java.rmi.RemoteException;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class NewCustomerRequest extends MiddlewareBaseTCPRequest {
    public NewCustomerRequest() {
        super(MiddlewareTCPRequestTypes.ADD_CUSTOMER_REQUEST);
    }

    @Override
    public MiddlewareBaseTCPResponse executeRequest(MiddlewareServer server) {
        try {
            int cid = server.getMiddlewareInterface().newCustomer(this.id);
            return new IntegerResponse(cid);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
