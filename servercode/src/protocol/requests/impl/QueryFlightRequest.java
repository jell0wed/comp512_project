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
public class QueryFlightRequest extends BaseTCPRequest {
    public final int flightNo;

    public QueryFlightRequest(int flightNo) {
        super(TCPRequestTypes.QUERY_FLIGHT_REQUEST);
        this.flightNo = flightNo;
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) {
        try {
            int resp = resManager.queryFlight(this.id, this.flightNo);
            return new IntegerResponse(resp);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
