package middleware.transactions;

import ResImpl.exceptions.AbortedTransactionException;
import ResImpl.exceptions.InvalidTransactionException;
import ResImpl.exceptions.TransactionException;
import middleware.database.ICustomerDatabase;
import middleware.resource_managers.ResourceManagerTypes;

import java.util.function.Consumer;

public interface IDistributedTransactionManager {
    public int openTransaction();
    public void commitTransaction(int transId) throws InvalidTransactionException, AbortedTransactionException;
    public void abortTransaction(int transId) throws InvalidTransactionException;
    public int enlistResourceManager(int transId, ResourceManagerTypes rmType) throws TransactionException;
    public int enlistAlreadyEnlistedResourceManager(int transId, ResourceManagerTypes rmType, int rmTransId) throws TransactionException;
    public void appendReservationUndoLog(int transId, Consumer<ICustomerDatabase> undoFn) throws InvalidTransactionException;
    public void ensureTransactionExists(int transId) throws InvalidTransactionException;
}
