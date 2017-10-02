package middleware.impl.tcp.responses;

import middleware.exceptions.MiddlewareBaseException;
import middleware.impl.tcp.responses.impl.IntegerResponse;
import middleware.impl.tcp.responses.impl.StringResponse;
import middleware.impl.tcp.responses.impl.SuccessFailureResponse;

import java.io.Serializable;

/**
 * Created by jpoisson on 2017-09-28.
 */
public abstract class MiddlewareBaseTCPResponse implements Serializable {
    public final MiddlewareTCPResponseTypes type;

    protected MiddlewareBaseTCPResponse(MiddlewareTCPResponseTypes type) {
        this.type = type;
    }

    public SuccessFailureResponse asSuccessFailureResponse() {
        if(type != MiddlewareTCPResponseTypes.SUCCESS_FAILURE_RESPONSE) {
            throw new MiddlewareBaseException("Cannot get as Success Failure type is " + type);
        }

        return (SuccessFailureResponse) this;
    }

    public IntegerResponse asIntegerResponse() {
        if(type != MiddlewareTCPResponseTypes.INTEGER_RESPONSE) {
            throw new MiddlewareBaseException("Cannot get as Integer type is " + type);
        }

        return (IntegerResponse) this;
    }

    public StringResponse asStringResponse() {
        if(type != MiddlewareTCPResponseTypes.STRING_RESPONSE) {
            throw new MiddlewareBaseException("Cannot get as String type is " + type);
        }

        return (StringResponse) this;
    }
}
