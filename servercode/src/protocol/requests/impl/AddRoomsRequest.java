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
 * Created by jpoisson on 2017-09-28.
 */
public class AddRoomsRequest extends BaseTCPRequest {
    public final String location;
    public final int numRooms;
    public final int price;

    public AddRoomsRequest(String loc, int numRooms, int price) {
        super(TCPRequestTypes.ADD_ROOMS_REQUEST);
        this.location = loc;
        this.numRooms = numRooms;
        this.price = price;
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) {
        try {
            boolean success = resManager.addRooms(this.id, this.location, this.numRooms, this.price);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
