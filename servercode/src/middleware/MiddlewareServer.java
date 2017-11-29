package middleware;

import ResImpl.Trace;
import ResInterface.ResourceManager;
import middleware.database.ICustomerDatabase;
import middleware.database.MemoryCustomerDatabase;
import middleware.database.ReplicatedCustomerDatabase;
import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.*;
import middleware.transactions.DistributedTransactionManager;
import utils.RMIStringUtils;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by jpoisson on 2017-09-28.
 */
public abstract class MiddlewareServer {
    private final RemoteResourceManagerFactory remoteRMFactory;

    private Collection<String> availableRMs;
    private Hashtable<ResourceManagerTypes, AbstractRemoteResourceManager> remoteResourceManagers;
    private MiddlewareInterface middlewareInterface;
    private DistributedTransactionManager transactionMananger;
    private ICustomerDatabase middlewareDatabase;

    protected abstract void initializeServer();

    protected MiddlewareServer(RemoteResourceManagerImplementationTypes implType, String... availRMs) {
        remoteRMFactory = new RemoteResourceManagerFactory(implType);
        this.availableRMs = Arrays.asList(availRMs);
        this.middlewareInterface = new MiddlewareInterface(this);
        this.transactionMananger = new DistributedTransactionManager(this);
        this.middlewareDatabase = new MemoryCustomerDatabase();

        this.initializeRemoteResourceManagers();
        this.initializeServer();

    }

    private void initializeRemoteResourceManagers() {
        this.remoteResourceManagers = new Hashtable<>();

        LinkedList<String> rmStrs = new LinkedList<>();
        this.availableRMs.forEach(rmStrs::push);
        Collections.reverse(rmStrs);

        if(rmStrs.size() < 4) {
            throw new MiddlewareBaseException("Not enough RM servers.");
        }

        this.remoteResourceManagers.put(ResourceManagerTypes.CARS_ONLY, remoteRMFactory.createRemoteResourceManager(rmStrs.pop()));
        this.remoteResourceManagers.put(ResourceManagerTypes.FLIGHTS_ONLY, remoteRMFactory.createRemoteResourceManager(rmStrs.pop()));
        this.remoteResourceManagers.put(ResourceManagerTypes.ROOMS_ONLY, remoteRMFactory.createRemoteResourceManager(rmStrs.pop()));
        this.remoteResourceManagers.put(ResourceManagerTypes.OTHERS, remoteRMFactory.createRemoteResourceManager(rmStrs.pop()));
    }

    public AbstractRemoteResourceManager getRemoteResourceManagerForType(ResourceManagerTypes type) {
        if (!this.remoteResourceManagers.containsKey(type)) {
            throw new MiddlewareBaseException("No remote resource manager for type " + type.toString());
        }

        return this.remoteResourceManagers.get(type);
    }

    public void markDefectiveRemoteResourceManagerForType(ResourceManagerTypes type) {
        if (!this.remoteResourceManagers.containsKey(type)) {
            throw new MiddlewareBaseException("No remote resource manager for type " + type.toString());
        }

        AbstractRemoteResourceManager rm = this.remoteResourceManagers.get(type);
        Trace.info("Marking " + type.toString() + " as defective.");
        rm.setIsAliveStatus(false);
        this.remoteResourceManagers.put(type, rm);
    }

    protected DistributedTransactionManager getTransactionManager() {
        return this.transactionMananger;
    }

    protected Collection<AbstractRemoteResourceManager> getAllRemoteResourceManager() {
        return this.remoteResourceManagers.values();
    }

    public MiddlewareInterface getMiddlewareInterface() {
        return this.middlewareInterface;
    }

    public ICustomerDatabase getMiddlewareDatabase() { return this.middlewareDatabase; }

    public synchronized void registerRemoteMiddlewareAsBackup(String connStr) {
        // try to connect to remote middleware through rmi
        Matcher rmAddressMatch = RMIStringUtils.RMI_URL_PATTERN.matcher(connStr);
        if(!rmAddressMatch.matches()) {
            throw new RuntimeException("Invalid connection string");
        }

        String hostname = rmAddressMatch.group(1);
        Integer port = Integer.parseInt(rmAddressMatch.group(2));
        String key = rmAddressMatch.group(3);

        try {
            Registry registry = LocateRegistry.getRegistry(hostname, port);
            ResourceManager backupMiddleware = (ResourceManager) registry.lookup(key);
            this.middlewareDatabase = new ReplicatedCustomerDatabase(this.middlewareDatabase, backupMiddleware);
            Trace.info("Registered backup middleware from " + connStr);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }


}
