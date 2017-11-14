package protocol.requests.impl;

import ResImpl.exceptions.TransactionException;
import ResInterface.ResourceManager;
import middleware.MiddlewareServer;
import protocol.requests.BaseTCPRequest;
import protocol.requests.TCPRequestTypes;
import protocol.responses.BaseTCPResponse;
import protocol.responses.impl.ExceptionResponse;
import protocol.responses.impl.StringResponse;

import java.rmi.RemoteException;

/**
 * Created by jpoisson on 2017-09-30.
 */
public class QueryCustomerInfoRequest extends BaseTCPRequest {
    public final int customerId;

    public QueryCustomerInfoRequest(int cid) {
        super(TCPRequestTypes.QUERY_CUSTOMER_INFO_REQUEST);
        this.customerId = cid;
    }

    @Override
    public BaseTCPResponse executeRequest(ResourceManager resManager) throws TransactionException {
        try {
            String resp = resManager.queryCustomerInfo(this.id, this.customerId);
            return new StringResponse(resp);
        } catch (RemoteException e) {
            return new ExceptionResponse(e);
        }
    }
}
