package middleware.impl.tcp.requests;

import middleware.MiddlewareServer;
import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;

import java.io.Serializable;

/**
 * Created by jpoisson on 2017-09-28.
 */
public abstract class MiddlewareBaseTCPRequest implements Serializable {
    private static int idSeq = 0;
    public final MiddlewareTCPRequestTypes type;
    public final int id = idSeq++;

    protected MiddlewareBaseTCPRequest(MiddlewareTCPRequestTypes type) {
        this.type = type;
    }

    public abstract MiddlewareBaseTCPResponse executeRequest(MiddlewareServer server);
}
