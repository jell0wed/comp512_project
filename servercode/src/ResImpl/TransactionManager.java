package ResImpl;

import ResImpl.exceptions.TransactionException;
import transactions.LockManager.LockManager;

import java.util.*;
import java.util.function.Consumer;

public class TransactionManager {
    private final Map<Integer, Stack<Consumer<RMHashtable>>> undoLogMap = new Hashtable<Integer, Stack<Consumer<RMHashtable>>>();
    private final Set<Integer> openTransactions = new HashSet<Integer>();
    private LockManager lockManager = new LockManager();
    private volatile int transactionCounter = 0;

    public synchronized int initializeTransaction() {
        int transId = ++this.transactionCounter;
        this.openTransactions.add(transId);
        this.undoLogMap.put(transId, new Stack<>());
        Trace.info("Initialized transaction id " + transId);
        return transId;
    }

    public synchronized void appendUndoLog(Integer transId, Consumer<RMHashtable> undoFn) {
        if(!this.doesTransactionExists(transId)) {
            return;
        }

        Stack<Consumer<RMHashtable>> transUndoLog = this.undoLogMap.get(transId);
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

    public synchronized void commitTransaction(int transId) throws TransactionException {
        if(!this.doesTransactionExists(transId)) {
            throw new TransactionException("No transaction with id " + transId);
        }

        try {
            Trace.info("Commit transaction id " + transId);
        } finally {
            this.lockManager.UnlockAll(transId);
        }
    }

    public synchronized void abortTransaction(int transId, ResourceManagerDatabase rmDb) throws TransactionException {
        if(!this.doesTransactionExists(transId)) {
            throw new TransactionException("No transaction with id " + transId);
        }

        try {
            Trace.info("Abort transaction id " + transId);
            // unroll the undo log
            Stack<Consumer<RMHashtable>> transUndoLog = this.undoLogMap.get(transId);
            while(!transUndoLog.isEmpty()) {
                Consumer<RMHashtable> undoOp = transUndoLog.pop();
                undoOp.accept(rmDb.m_itemHT);
                Trace.info("Unrolled operation for trans " + transId);
            }
        } finally {
            this.lockManager.UnlockAll(transId);
        }
    }
}
