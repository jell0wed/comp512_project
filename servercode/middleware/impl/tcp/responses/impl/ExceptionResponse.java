package middleware.impl.tcp.responses.impl;

import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.MiddlewareTCPResponseTypes;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class ExceptionResponse extends MiddlewareBaseTCPResponse {
    public final Throwable wrappedException;
    public ExceptionResponse(Throwable ex) {
        super(MiddlewareTCPResponseTypes.EXCEPTION_RESPONSE);
        this.wrappedException = ex;
    }
}
