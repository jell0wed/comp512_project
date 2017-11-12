package ResImpl.exceptions;

public class InvalidTransactionException extends TransactionException {
    public InvalidTransactionException(String msg) {
        super(msg);
    }
}
