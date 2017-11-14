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
 * Created by jpoisson on 2017-09-30.
 */
public class DeleteRoomRequest extends BaseTCPRequest {
    public final String location;

    public DeleteRoomRequest(String loc) {
        super(TCPRequestTypes.DELETE_ROOM_REQUEST);
        this.location = loc;
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) throws TransactionException {
        try {
            boolean success = resManager.deleteRooms(this.id, this.location);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
