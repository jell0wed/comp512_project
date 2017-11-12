package middleware.transactions;

import ResImpl.Trace;
import ResImpl.exceptions.TransactionException;
import ResInterface.ResourceManager;
import middleware.MiddlewareServer;
import middleware.resource_managers.ResourceManagerTypes;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;

public class DistributedTransactionManager {
    private volatile int transactionCounter = 0;
    private Map<Integer, DistributedTransaction> openTransactions = new Hashtable<Integer, DistributedTransaction>();
    private MiddlewareServer middleware;

    public DistributedTransactionManager(MiddlewareServer middlewareInstance) {
        this.middleware = middlewareInstance;
    }

    public synchronized int openTransaction() {
        int transId = ++this.transactionCounter;
        DistributedTransaction transaction = new DistributedTransaction();

        this.openTransactions.put(transId, transaction);

        return transId;
    }

    public synchronized void commitTransaction(int transId) throws TransactionException {
        // commit on all enlisted resource managers
        if(!transactionExists(transId)) {
            throw new TransactionException("Transaction does not exists.");
        }

        // make sure to commit on all resource managers
        DistributedTransaction distTrans = this.openTransactions.get(transId);
        for(ResourceManagerTypes rmType: distTrans.enlistedRms.keySet()) {
            ResourceManager rm = this.middleware.getRemoteResourceManagerForType(rmType).getResourceManager();
            int rmTransId = distTrans.enlistedRms.get(rmType);

            try {
                rm.commitTransaction(rmTransId);
            } catch (RemoteException e) {
                // TODO : re-consider this?
                Trace.error("Error while commiting transaction on " + rmType);
            }
        }
    }

    public synchronized void abortTransaction(int transId) throws TransactionException {
        // abort all enlisted resource managers
        if(!transactionExists(transId)) {
            throw new TransactionException("Transaction does not exists.");
        }

        // make sure to abort on all resource managers
        DistributedTransaction distTrans = this.openTransactions.get(transId);
        for(ResourceManagerTypes rmType: distTrans.enlistedRms.keySet()) {
            ResourceManager rm = this.middleware.getRemoteResourceManagerForType(rmType).getResourceManager();
            int rmTransId = distTrans.enlistedRms.get(rmType);

            try {
                rm.abortTransaction(rmTransId);
            } catch (RemoteException e) {
                // TODO : re-consider this?
                Trace.error("Error while aborting transaction on " + rmType);
            }
        }
    }

    public synchronized int enlistResourceManager(int transId, ResourceManagerTypes rmType) throws TransactionException {
        if(!transactionExists(transId)) {
            throw new TransactionException("Transaction does not exists.");
        }

        // make sure a transaction has been initialized for rmType
        DistributedTransaction distTrans = this.openTransactions.get(transId);
        Integer rmTransId = null;
        if(!distTrans.enlistedRms.containsKey(rmType)) {
            // start a new transaction at the rm
            try {
                rmTransId = this.middleware.getRemoteResourceManagerForType(rmType).getResourceManager().startTransaction();
                distTrans.enlistedRms.put(rmType, rmTransId);
            } catch (RemoteException e) {
                throw new TransactionException("Unable to start new transactionat rm" + rmType);
            }
        } else {
            rmTransId = distTrans.enlistedRms.get(rmType);
        }

        // return appropriate transaction id
        return rmTransId;
    }

    public synchronized void ensureTransactionExists(int transId) throws TransactionException {
        if(!transactionExists(transId)) {
            throw new TransactionException("Transaction " + transId + " does not exists.");
        }
    }

    private boolean transactionExists(int id) {
        return this.openTransactions.keySet().stream().anyMatch(x -> x.equals(id));
    }
}
