package ResImpl.exceptions;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class RMBaseException extends RuntimeException {
    public RMBaseException(String msg) {
        super(msg);
    }

    public RMBaseException(String msg, Throwable src) {
        super(msg, src);
    }
}
