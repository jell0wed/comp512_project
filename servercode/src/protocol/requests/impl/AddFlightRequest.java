package protocol.requests.impl;


import ResImpl.exceptions.TransactionException;
import ResInterface.ResourceManager;
import middleware.MiddlewareServer;
import protocol.requests.BaseTCPRequest;
import protocol.requests.TCPRequestTypes;
import protocol.responses.BaseTCPResponse;
import protocol.responses.impl.ExceptionResponse;
import protocol.responses.impl.SuccessFailureResponse;

import java.rmi.RemoteException;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class AddFlightRequest extends BaseTCPRequest {
    public final int flightNum;
    public final int flightSeats;
    public final int flightPrice;

    public AddFlightRequest(int flightNum, int flightSeats, int flightPrice) {
        super(TCPRequestTypes.ADD_FLIGHTS_REQUEST);
        this.flightNum = flightNum;
        this.flightSeats = flightSeats;
        this.flightPrice = flightPrice;
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) throws TransactionException {
        try {
            boolean success = resManager.addFlight(this.id, this.flightNum, this.flightSeats, this.flightPrice);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
