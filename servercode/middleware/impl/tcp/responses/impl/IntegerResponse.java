package middleware.impl.tcp.responses.impl;

import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.MiddlewareTCPResponseTypes;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class IntegerResponse extends MiddlewareBaseTCPResponse {
    public final int value;

    public IntegerResponse(int val) {
        super(MiddlewareTCPResponseTypes.INTEGER_RESPONSE);
        this.value = val;
    }
}
