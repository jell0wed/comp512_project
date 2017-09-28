package middleware.exceptions;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class MiddlewareBaseException extends RuntimeException {
    public MiddlewareBaseException(String msg) {
        super(msg);
    }

    public MiddlewareBaseException(String msg, Throwable src) {
        super(msg, src);
    }
}
