package middleware.impl.tcp.responses.impl;

import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.MiddlewareTCPResponseTypes;

/**
 * Created by jpoisson on 2017-09-30.
 */
public class StringResponse extends MiddlewareBaseTCPResponse {
    public final String value;

    public StringResponse(String val) {
        super(MiddlewareTCPResponseTypes.STRING_RESPONSE);
        this.value = val;
    }
}
