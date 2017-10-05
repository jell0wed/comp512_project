package protocol.responses.impl;

import protocol.responses.BaseTCPResponse;
import protocol.responses.TCPResponseTypes;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class IntegerResponse extends BaseTCPResponse {
    public final int value;

    public IntegerResponse(int val) {
        super(TCPResponseTypes.INTEGER_RESPONSE);
        this.value = val;
    }
}
