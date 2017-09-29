package middleware.impl.tcp.responses;

import java.io.Serializable;

/**
 * Created by jpoisson on 2017-09-28.
 */
public abstract class MiddlewareBaseTCPResponse implements Serializable {
    public final MiddlewareTCPResponseTypes type;

    protected MiddlewareBaseTCPResponse(MiddlewareTCPResponseTypes type) {
        this.type = type;
    }
}
