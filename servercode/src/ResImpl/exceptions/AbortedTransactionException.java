package ResImpl.exceptions;

public class AbortedTransactionException extends TransactionException {
    public AbortedTransactionException(String msg) {
        super(msg);
    }
}
