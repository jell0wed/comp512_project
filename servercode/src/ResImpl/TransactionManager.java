package ResImpl;

import ResImpl.exceptions.AbortedTransactionException;
import ResImpl.exceptions.InvalidTransactionException;
import ResImpl.exceptions.TransactionException;
import transactions.LockManager.LockManager;

import java.util.*;
import java.util.function.Consumer;

public class TransactionManager {
    private final Map<Integer, Stack<Consumer<ResourceManagerDatabase>>> undoLogMap = new Hashtable<Integer, Stack<Consumer<ResourceManagerDatabase>>>();
    private final Set<Integer> openTransactions = new HashSet<Integer>();
    private final Set<Integer> abortedTransactions = new HashSet<>();
    private LockManager lockManager = new LockManager();
    private volatile int transactionCounter = 0;

    public synchronized int initializeTransaction() {
        int transId = ++this.transactionCounter;
        this.openTransactions.add(transId);
        this.undoLogMap.put(transId, new Stack<>());
        Trace.info("Initialized transaction id " + transId);
        return transId;
    }

    public synchronized void appendUndoLog(Integer transId, Consumer<ResourceManagerDatabase> undoFn) {
        if(!this.doesTransactionExists(transId)) {
            return;
        }

        Stack<Consumer<ResourceManagerDatabase>> transUndoLog = this.undoLogMap.get(transId);
        transUndoLog.push(undoFn);
        this.undoLogMap.put(transId, transUndoLog);
    }

    private synchronized boolean doesTransactionExists(int transId) {
        return this.openTransactions.stream().anyMatch(x -> x.equals(new Integer(transId)));
    }

    public synchronized void lock(int transId, String key, int type) throws TransactionException {
        if(!this.doesTransactionExists(transId)) {
            throw new TransactionException("No transaction with id " + transId);
        }

        this.lockManager.Lock(transId, key, type);
        Trace.info("Locked " + key + " to trans id " + transId);
    }

    public synchronized void commitTransaction(int transId) throws InvalidTransactionException, AbortedTransactionException {
        if(!this.doesTransactionExists(transId)) {
            throw new InvalidTransactionException("Transaction " + transId + " does not exists.");
        }

        if(this.abortedTransactions.contains(transId)) {
            throw new AbortedTransactionException("Transaction " + transId + " aborted.");
        }

        try {
            Trace.info("Commit transaction id " + transId);
            this.openTransactions.remove(transId);
            this.undoLogMap.remove(transId);
        } finally {
            this.lockManager.UnlockAll(transId);
        }
    }

    public synchronized void abortTransaction(int transId, ResourceManagerDatabase rmDb) throws InvalidTransactionException {
        if(!this.doesTransactionExists(transId)) {
            throw new InvalidTransactionException("No transaction with id " + transId);
        }

        try {
            Trace.info("Abort transaction id " + transId);
            // unroll the undo log
            Stack<Consumer<ResourceManagerDatabase>> transUndoLog = this.undoLogMap.get(transId);
            while(!transUndoLog.isEmpty()) {
                Consumer<ResourceManagerDatabase> undoOp = transUndoLog.pop();
                undoOp.accept(rmDb);
                Trace.info("Unrolled operation for trans " + transId);
            }

            this.abortedTransactions.add(transId);
            this.openTransactions.remove(transId);
            this.undoLogMap.remove(transId);
        } finally {
            this.lockManager.UnlockAll(transId);
        }
    }
}
