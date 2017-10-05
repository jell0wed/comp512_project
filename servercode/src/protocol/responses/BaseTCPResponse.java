package protocol.responses;

import middleware.exceptions.MiddlewareBaseException;
import protocol.responses.impl.IntegerResponse;
import protocol.responses.impl.StringResponse;
import protocol.responses.impl.SuccessFailureResponse;

import java.io.Serializable;

/**
 * Created by jpoisson on 2017-09-28.
 */
public abstract class BaseTCPResponse implements Serializable {
    public final TCPResponseTypes type;

    protected BaseTCPResponse(TCPResponseTypes type) {
        this.type = type;
    }

    public SuccessFailureResponse asSuccessFailureResponse() {
        if(type != TCPResponseTypes.SUCCESS_FAILURE_RESPONSE) {
            throw new MiddlewareBaseException("Cannot get as Success Failure type is " + type);
        }

        return (SuccessFailureResponse) this;
    }

    public IntegerResponse asIntegerResponse() {
        if(type != TCPResponseTypes.INTEGER_RESPONSE) {
            throw new MiddlewareBaseException("Cannot get as Integer type is " + type);
        }

        return (IntegerResponse) this;
    }

    public StringResponse asStringResponse() {
        if(type != TCPResponseTypes.STRING_RESPONSE) {
            throw new MiddlewareBaseException("Cannot get as String type is " + type);
        }

        return (StringResponse) this;
    }
}
