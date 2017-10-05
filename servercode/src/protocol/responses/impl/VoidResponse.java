package protocol.responses.impl;

import protocol.responses.BaseTCPResponse;
import protocol.responses.TCPResponseTypes;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class VoidResponse extends BaseTCPResponse {
    public final String message;

    public VoidResponse(String message) {
        super(TCPResponseTypes.VOID_RESPONSE);
        this.message = message;
    }
}
