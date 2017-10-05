package protocol.responses.impl;

import protocol.responses.BaseTCPResponse;
import protocol.responses.TCPResponseTypes;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class ExceptionResponse extends BaseTCPResponse {
    public final Throwable wrappedException;
    public ExceptionResponse(Throwable ex) {
        super(TCPResponseTypes.EXCEPTION_RESPONSE);
        this.wrappedException = ex;
    }
}
