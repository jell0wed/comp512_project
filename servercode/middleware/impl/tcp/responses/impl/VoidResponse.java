package middleware.impl.tcp.responses.impl;

import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.MiddlewareTCPResponseTypes;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class VoidResponse extends MiddlewareBaseTCPResponse {
    public final String message;

    public VoidResponse(String message) {
        super(MiddlewareTCPResponseTypes.VOID_RESPONSE);
        this.message = message;
    }
}
