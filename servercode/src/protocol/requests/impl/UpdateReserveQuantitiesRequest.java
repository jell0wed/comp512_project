package protocol.requests.impl;

import ResImpl.exceptions.TransactionException;
import ResInterface.ResourceManager;
import protocol.requests.BaseTCPRequest;
import protocol.requests.TCPRequestTypes;
import protocol.responses.BaseTCPResponse;
import protocol.responses.impl.ExceptionResponse;
import protocol.responses.impl.SuccessFailureResponse;

import java.rmi.RemoteException;

/**
 * Created by jpoisson on 2017-10-05.
 */
public class UpdateReserveQuantitiesRequest extends BaseTCPRequest {
    public final String key;
    public final int incQty;

    public UpdateReserveQuantitiesRequest(String key, int incQty) {
        super(TCPRequestTypes.UPDATE_RESERVED_QUANTITIES);
        this.key = key;
        this.incQty = incQty;
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) throws TransactionException {
        try {
            boolean success = resManager.updateReservedQuantities(this.id, this.key, this.incQty);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
