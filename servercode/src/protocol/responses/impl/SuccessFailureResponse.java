package protocol.responses.impl;

import protocol.responses.BaseTCPResponse;
import protocol.responses.TCPResponseTypes;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class SuccessFailureResponse extends BaseTCPResponse {
    public final boolean success;

    public SuccessFailureResponse(boolean success) {
        super(TCPResponseTypes.SUCCESS_FAILURE_RESPONSE);
        this.success = success;
    }
}
