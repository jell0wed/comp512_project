package protocol.responses.impl;

import protocol.responses.BaseTCPResponse;
import protocol.responses.TCPResponseTypes;

/**
 * Created by jpoisson on 2017-09-30.
 */
public class StringResponse extends BaseTCPResponse {
    public final String value;

    public StringResponse(String val) {
        super(TCPResponseTypes.STRING_RESPONSE);
        this.value = val;
    }
}
