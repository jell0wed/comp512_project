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
public class QueryRoomPriceRequest extends MiddlewareBaseTCPRequest {
    public final String location;

    public QueryRoomPriceRequest(String loc) {
        super(MiddlewareTCPRequestTypes.QUERY_ROOM_PRICE_REQUEST);
        this.location = loc;
    }

    @Override
    public MiddlewareBaseTCPResponse executeRequest(MiddlewareServer server) {
        try {
            int resp = server.getMiddlewareInterface().queryRoomsPrice(this.id, this.location);
            return new IntegerResponse(resp);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
