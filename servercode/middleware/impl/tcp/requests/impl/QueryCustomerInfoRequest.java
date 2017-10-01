package middleware.impl.tcp.requests.impl;

import middleware.MiddlewareServer;
import middleware.impl.tcp.requests.MiddlewareBaseTCPRequest;
import middleware.impl.tcp.requests.MiddlewareTCPRequestTypes;
import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.impl.ExceptionResponse;
import middleware.impl.tcp.responses.impl.StringResponse;

import java.rmi.RemoteException;

/**
 * Created by jpoisson on 2017-09-30.
 */
public class QueryCustomerInfoRequest extends MiddlewareBaseTCPRequest {
    public final int customerId;

    public QueryCustomerInfoRequest(int cid) {
        super(MiddlewareTCPRequestTypes.QUERY_CUSTOMER_INFO_REQUEST);
        this.customerId = cid;
    }

    @Override
    public MiddlewareBaseTCPResponse executeRequest(MiddlewareServer server) {
        try {
            String resp = server.getMiddlewareInterface().queryCustomerInfo(this.id, this.customerId);
            return new StringResponse(resp);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
