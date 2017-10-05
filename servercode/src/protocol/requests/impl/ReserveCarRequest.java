package protocol.requests.impl;

import ResInterface.ResourceManager;
import middleware.MiddlewareServer;
import protocol.requests.BaseTCPRequest;
import protocol.requests.TCPRequestTypes;
import protocol.responses.BaseTCPResponse;
import protocol.responses.impl.ExceptionResponse;
import protocol.responses.impl.SuccessFailureResponse;

import java.rmi.RemoteException;

/**
 * Created by jpoisson on 2017-09-30.
 */
public class ReserveCarRequest extends BaseTCPRequest {
    public final int customerId;
    public final String location;

    public ReserveCarRequest(int cid, String loc) {
        super(TCPRequestTypes.RESERVE_CAR_REQUEST);
        this.customerId = cid;
        this.location = loc;
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) {
        try {
            boolean success = resManager.reserveCar(this.id, this.customerId, this.location);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
