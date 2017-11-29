package middleware.transactions;

import ResImpl.Trace;
import ResImpl.exceptions.AbortedTransactionException;
import ResImpl.exceptions.InvalidTransactionException;
import ResImpl.exceptions.TransactionException;
import ResInterface.ResourceManager;
import middleware.database.ICustomerDatabase;
import middleware.resource_managers.ResourceManagerTypes;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.function.Consumer;

public class ReplicatedTransactionManager implements IDistributedTransactionManager {
    private IDistributedTransactionManager localTransactionManager;
    private ResourceManager backupMiddleware;

    public ReplicatedTransactionManager(IDistributedTransactionManager local, ResourceManager remote) {
        this.localTransactionManager = local;
        this.backupMiddleware = remote;
    }

    @Override
    public int openTransaction() {
        int result = this.localTransactionManager.openTransaction();
        try {
            this.backupMiddleware.executeTransactionOperation((Consumer<IDistributedTransactionManager>&Serializable) tMgr -> {
                tMgr.openTransaction();
            });
        } catch (RemoteException e) {
            Trace.error("Error while executing transaction operation remotely");
        }

        return result;
    }

    @Override
    public void commitTransaction(int transId) throws InvalidTransactionException, AbortedTransactionException {
        this.localTransactionManager.commitTransaction(transId);
        try {
            this.backupMiddleware.executeTransactionOperation((Consumer<IDistributedTransactionManager>&Serializable) tMgr -> {
                try {
                    tMgr.commitTransaction(transId);
                } catch (InvalidTransactionException | AbortedTransactionException e) {
                    e.printStackTrace();
                }
            });
        } catch (RemoteException e) {
            Trace.error("Error while executing transaction operation remotely");
        }
    }

    @Override
    public void abortTransaction(int transId) throws InvalidTransactionException {
        this.localTransactionManager.abortTransaction(transId);
        try {
            this.backupMiddleware.executeTransactionOperation((Consumer<IDistributedTransactionManager>&Serializable) tMgr -> {
                try {
                    tMgr.abortTransaction(transId);
                } catch (InvalidTransactionException e) {
                    e.printStackTrace();
                }
            });
        } catch (RemoteException e) {
            Trace.error("Error while executing transaction operation remotely");
        }
    }

    @Override
    public int enlistResourceManager(int transId, ResourceManagerTypes rmType) throws TransactionException {
        int result = this.localTransactionManager.enlistResourceManager(transId, rmType);
        try {
            this.backupMiddleware.executeTransactionOperation((Consumer<IDistributedTransactionManager>&Serializable) tMgr -> {
                try {
                    tMgr.enlistResourceManager(transId, rmType);
                } catch (TransactionException e) {
                    e.printStackTrace();
                }
            });
        } catch (RemoteException e) {
            Trace.error("Error while executing transaction operation remotely");
        }
        return result;
    }

    @Override
    public void appendReservationUndoLog(int transId, Consumer<ICustomerDatabase> undoFn) throws InvalidTransactionException {
        this.localTransactionManager.appendReservationUndoLog(transId, undoFn);
        try {
            this.backupMiddleware.executeTransactionOperation((Consumer<IDistributedTransactionManager>&Serializable) tMgr -> {
                try {
                    tMgr.appendReservationUndoLog(transId, undoFn);
                } catch (InvalidTransactionException e) {
                    e.printStackTrace();
                }
            });
        } catch (RemoteException e) {
            Trace.error("Error while executing transaction operation remotely");
        }
    }

    @Override
    public void ensureTransactionExists(int transId) throws InvalidTransactionException {
        this.localTransactionManager.ensureTransactionExists(transId);
        try {
            this.backupMiddleware.executeTransactionOperation((Consumer<IDistributedTransactionManager>&Serializable) tMgr -> {
                try {
                    tMgr.ensureTransactionExists(transId);
                } catch (InvalidTransactionException e) {
                    e.printStackTrace();
                }
            });
        } catch (RemoteException e) {
            Trace.error("Error while executing transaction operation remotely");
        }
    }
}
