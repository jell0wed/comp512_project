package clients.load;

import ResImpl.Trace;
import ResInterface.ResourceManager;
import oracle.jvm.hotspot.jfr.Producer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

public class SingleClientRMILoadTest implements Runnable {
    final String REGISTRY_HOST = "localhost";
    final int    REGISTRY_POST =  1099;
    final String REGISTRY_NAME = "rmMiddleware";

    // Settings
    private int preloadRequests = 1000;
    private int requestsTodo = 1000;
    private int timeOut = 2 * 60 * 1000;
    private boolean burst = false;
    private int sleepTime = 0;
    private ResourceManager rm;
    private AnalysisResults results = null;

    public SingleClientRMILoadTest() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(REGISTRY_HOST, REGISTRY_POST);
        rm = (ResourceManager) registry.lookup(REGISTRY_NAME);
    }

    private Collection<Function<ResourceManager, Boolean>> requestTypes = new LinkedList<>();

    public SingleClientRMILoadTest(int preload, int requestsTodo) {
        this.preloadRequests = preload;
        this.requestsTodo = requestsTodo;
        this.burst = true;
    }

    public SingleClientRMILoadTest(int preload, int reqPerSec, int timeOut) {
        this.preloadRequests = preload;
        this.burst = false;
        this.sleepTime = 1000 / reqPerSec;
    }

    public void addRequestType(Function<ResourceManager, Boolean> reqType) {
        this.requestTypes.add(reqType);
    }

    @Override
    public void run() {
        Trace.info("Starting test");
        // preload test
        for(int i = 0; i < this.preloadRequests; i++) {
            for(Function<ResourceManager, Boolean> req: this.requestTypes) {
                req.apply(this.rm);
            }
        }
        Trace.info("Finished preloading test.");

        this.results = new AnalysisResults();
        if(this.burst) {
            int numTrans = 0;
            for(int i = 0; i < this.requestsTodo; i++) {
                for(Function<ResourceManager, Boolean> req: this.requestTypes) {
                    long startTime = System.nanoTime();
                    Boolean success = req.apply(this.rm);
                    long endTime = System.nanoTime();
                    if(success) {
                        numTrans++;
                    }
                    numTrans++;
                    this.results.appendResponseTime(endTime - startTime);
                }
            }

            this.results.transactionCount = numTrans;
            this.results.requestCount = this.requestsTodo;
        } else {

        }
    }
}
