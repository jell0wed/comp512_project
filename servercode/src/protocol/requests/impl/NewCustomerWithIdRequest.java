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
public class NewCustomerWithIdRequest extends BaseTCPRequest {
    public final int requestedCustId;

    public NewCustomerWithIdRequest(int cid) {
        super(TCPRequestTypes.ADD_CUSTOMER_WITH_ID_REQUEST);
        this.requestedCustId = cid;
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) {
        try {
            boolean success = resManager.newCustomer(this.id, this.requestedCustId);
            return new SuccessFailureResponse(success);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
