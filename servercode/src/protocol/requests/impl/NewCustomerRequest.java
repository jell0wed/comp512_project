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
 * Created by jpoisson on 2017-09-28.
 */
public class NewCustomerRequest extends BaseTCPRequest {
    public NewCustomerRequest() {
        super(TCPRequestTypes.ADD_CUSTOMER_REQUEST);
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) {
        try {
            int cid = resManager.newCustomer(this.id);
            return new IntegerResponse(cid);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
