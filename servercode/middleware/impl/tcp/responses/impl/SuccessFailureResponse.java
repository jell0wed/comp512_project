package middleware.impl.tcp.responses.impl;

import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.MiddlewareTCPResponseTypes;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class SuccessFailureResponse extends MiddlewareBaseTCPResponse {
    public final boolean success;

    public SuccessFailureResponse(boolean success) {
        super(MiddlewareTCPResponseTypes.SUCCESS_FAILURE_RESPONSE);
        this.success = success;
    }
}
