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
public class DeleteFlightRequest extends BaseTCPRequest {
    public final int flightNum;

    public DeleteFlightRequest(int flightNum) {
        super(TCPRequestTypes.DELETE_FLIGHT_REQUEST);
        this.flightNum = flightNum;
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) throws TransactionException {
        try {
            boolean success = resManager.deleteFlight(this.id, this.flightNum);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
