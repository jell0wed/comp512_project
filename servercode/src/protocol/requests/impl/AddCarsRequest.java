package protocol.requests.impl;


import ResInterface.ResourceManager;
import protocol.requests.BaseTCPRequest;
import protocol.requests.TCPRequestTypes;
import protocol.responses.BaseTCPResponse;
import protocol.responses.impl.ExceptionResponse;
import protocol.responses.impl.SuccessFailureResponse;

import java.rmi.RemoteException;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class AddCarsRequest extends BaseTCPRequest {
    public final String location;
    public final int numCars;
    public final int price;

    public AddCarsRequest(String loc, int numCars, int price) {
        super(TCPRequestTypes.ADD_CARS_REQUEST);
        this.location = loc;
        this.numCars = numCars;
        this.price = price;
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) {
        try {
            boolean success = resManager.addCars(this.id, this.location, this.numCars, this.price);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
