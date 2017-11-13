package ResImpl.exceptions;

public class TransactionException extends Exception {
    public TransactionException(String msg) {
        super(msg);
    }

    public TransactionException(String msg, Exception src) {
        super(msg, src);
    }
}
