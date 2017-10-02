package middleware.impl.tcp.requests.impl;

import middleware.MiddlewareServer;
import middleware.impl.tcp.requests.MiddlewareBaseTCPRequest;
import middleware.impl.tcp.requests.MiddlewareTCPRequestTypes;
import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.impl.ExceptionResponse;
import middleware.impl.tcp.responses.impl.IntegerResponse;

import java.rmi.RemoteException;

/**
 * Created by jpoisson on 2017-09-30.
 */
public class QueryFlightPriceRequest extends MiddlewareBaseTCPRequest {
    private final int flightNo;

    public QueryFlightPriceRequest(int flightNo) {
        super(MiddlewareTCPRequestTypes.QUERY_FLIGHT_PRICE_REQUEST);
        this.flightNo = flightNo;
    }

    @Override
    public MiddlewareBaseTCPResponse executeRequest(MiddlewareServer server) {
        try {
            int resp = server.getMiddlewareInterface().queryFlightPrice(this.id, this.flightNo);
            return new IntegerResponse(resp);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
