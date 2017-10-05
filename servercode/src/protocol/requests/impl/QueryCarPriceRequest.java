package protocol.requests.impl;

import ResInterface.ResourceManager;
import middleware.MiddlewareServer;
import protocol.requests.BaseTCPRequest;
import protocol.requests.TCPRequestTypes;
import protocol.responses.BaseTCPResponse;
import protocol.responses.impl.ExceptionResponse;
import protocol.responses.impl.IntegerResponse;

import java.rmi.RemoteException;

/**
 * Created by jpoisson on 2017-09-30.
 */
public class QueryCarPriceRequest extends BaseTCPRequest {
    public final String location;

    public QueryCarPriceRequest(String loc) {
        super(TCPRequestTypes.QUERY_CAR_PRICE_REQUEST);
        this.location = loc;
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) {
        try {
            int resp = resManager.queryCarsPrice(this.id, this.location);
            return new IntegerResponse(resp);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
