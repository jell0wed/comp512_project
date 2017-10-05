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
public class QueryRoomRequest extends BaseTCPRequest {
    public final String location;

    public QueryRoomRequest(String loc) {
        super(TCPRequestTypes.QUERY_ROOM_REQUEST);
        this.location = loc;

    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) {
        try {
            int resp = resManager.queryRooms(this.id, this.location);
            return new IntegerResponse(resp);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
