package protocol.requests;


import ResImpl.exceptions.TransactionException;
import ResInterface.ResourceManager;
import protocol.responses.BaseTCPResponse;

import java.io.Serializable;

/**
 * Created by jpoisson on 2017-09-28.
 */
public abstract class BaseTCPRequest implements Serializable {
    private static int idSeq = 0;
    public final TCPRequestTypes type;
    public final int id = idSeq++;

    protected BaseTCPRequest(TCPRequestTypes type) {
        this.type = type;
    }

    public abstract BaseTCPResponse executeRequest(ResourceManager resManager) throws TransactionException;
}
