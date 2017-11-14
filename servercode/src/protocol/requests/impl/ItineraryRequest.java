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
import java.util.Vector;

/**
 * Created by jpoisson on 2017-09-30.
 */
public class ItineraryRequest extends BaseTCPRequest {
    public final int customerId;
    public final Vector<String> flightNumbers;
    public final String location;
    public final boolean bookCar;
    public final boolean bookRoom;

    public ItineraryRequest(int cid, Vector<String> flightNos, String loc, boolean bookCar, boolean bookRoom) {
        super(TCPRequestTypes.ITINERARY_REQUEST);
        this.customerId = cid;
        this.flightNumbers = flightNos;
        this.location = loc;
        this.bookCar = bookCar;
        this.bookRoom = bookRoom;
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) throws TransactionException {
        try {
            boolean success = resManager.itinerary(this.id, this.customerId, this.flightNumbers, this.location, this.bookCar, this.bookRoom);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
