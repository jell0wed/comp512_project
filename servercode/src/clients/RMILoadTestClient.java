package clients;

import ResInterface.ResourceManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.*;

public class RMILoadTestClient implements Runnable {
    final String REGISTRY_HOST = "localhost";
    final int    REGISTRY_POST =  1099;
    final String REGISTRY_NAME = "rmMiddleware";

    private volatile boolean lastRequestDone;
    private Timer requestTimer;
    private long startTime = -1;
    private ResourceManager rm;
    private float requestPerSeconds;
    private int requestCount = 0,
                earlyAbort = 0,
                requestErrors = 0,
                lowestReponseTime = 999999,
                highestReponseTime = 0,
                stopRequestCount = 1000,
                clientIdentifier;
    private long cumulativeRequestTimes;

    public static void main(String[] args) {
        RMILoadTestClient client = new RMILoadTestClient(80.0f, 0);
        client.run();
    }

    public RMILoadTestClient(float _requestPerSeconds, int _clientIdentifier) {
        clientIdentifier = _clientIdentifier;
        requestPerSeconds = _requestPerSeconds;
        stopRequestCount = (int)(requestPerSeconds * 60.0f * 5.0f);
    }

    public void run() {
        System.out.println("Starting load client #" + clientIdentifier + " with " + requestPerSeconds + " requests/s, doing " + stopRequestCount + " requests");

        try {
            Registry registry = LocateRegistry.getRegistry(REGISTRY_HOST, REGISTRY_POST);
            rm = (ResourceManager) registry.lookup(REGISTRY_NAME);

            lastRequestDone = true;

            long sleepTime = 0;
            if(requestPerSeconds >= 1.0f) {
                sleepTime = 1000 / (int)requestPerSeconds;
            } else {
                sleepTime = 1000 * (int)(1/requestPerSeconds);
            }

            System.out.println("#" + clientIdentifier + " Running load test..");

            ExecutorService es = Executors.newCachedThreadPool();

            while(true) {
                if(requestCount >= stopRequestCount) {
                    break;
                }

                Thread request = new Thread(new RMILoadTestRequest());
                es.execute(request);

                Thread.sleep(sleepTime);
            }

            es.shutdown();
            boolean finshed = es.awaitTermination(1, TimeUnit.MINUTES);

            this.printAnalysis();
        } catch(Exception e) {
            System.err.println("#" + clientIdentifier + " Client error happened while running tests: " + e.toString());
            e.printStackTrace(System.err);
        }
    }

    public void timeAwareLogging(String string) {
        if(startTime == -1) {
            startTime = System.currentTimeMillis();
        }

        long difference = System.currentTimeMillis() - startTime;

        System.out.println("#" + clientIdentifier + " " + difference + "ms " + string);
    }

    public void printAnalysis() {
        System.out.println("---- REPORT #" + clientIdentifier + " ----");

        int successfullRequests = requestCount - earlyAbort - requestErrors;

        System.out.println("Requests per seconds: " + requestPerSeconds + "/s");
        System.out.println("Requests: " + requestCount);
        System.out.println("Success: " + successfullRequests + " (" + printFormattedPercentage(successfullRequests, requestCount) + "%)");
        System.out.println("Early abort: " + earlyAbort + " (" + printFormattedPercentage(earlyAbort, requestCount) + "%)");
        System.out.println("Errored requests: " + requestErrors + " (" + printFormattedPercentage(requestErrors, requestCount) + "%)");
        System.out.println("Average response time: " + (float)cumulativeRequestTimes/(requestCount) + "ms");
        System.out.println("Highest reponse time: " + highestReponseTime + "ms");
        System.out.println("Lowest reponse time: " + lowestReponseTime + "ms");
    }

    public float printFormattedPercentage(int q1, int q2) {
        float sub_zero = (float)q1/(float)q2;
        return (float)Math.ceil(sub_zero * 10000) / 100;
    }

    class RMILoadTestRequest implements Runnable {
        public RMILoadTestRequest() { super(); }

        public void run() {
            requestCount++;

            if(!lastRequestDone) {
                earlyAbort++;
                timeAwareLogging("ERROR: Could not start new request because previous request is still running");
                return;
            }

            timeAwareLogging("Initiating request (" + requestCount + "/" + stopRequestCount + ")");

            lastRequestDone = false;
            long requestStartTime = System.currentTimeMillis();
            long requestEndTime = -1;

            // Request
            try {
                // Test add car
                // Involves one resource manangers
                int transId = rm.startTransaction();
                rm.addCars(transId, "mtl", 1, 100);
                rm.commitTransaction(transId);

                // Test query all
                // Involves all resource managers
                int transId4 = rm.startTransaction();
                rm.queryFlight(transId4, 9000);
                rm.queryCars(transId4, "mtl");
                rm.queryRooms(transId4, "mtl");
                rm.commitTransaction(transId4);
            } catch(Exception e) {
                requestErrors++;
                System.err.println("#" + clientIdentifier + " Client exception: " + e.toString());
                e.printStackTrace();
            } finally {
                lastRequestDone = true;

                requestEndTime = System.currentTimeMillis();

                long requestTime = (requestEndTime - requestStartTime);
                timeAwareLogging("Request time: " + requestTime + "ms");

                if(requestTime < lowestReponseTime) {
                    lowestReponseTime = (int)requestTime;
                }

                if(requestTime > highestReponseTime) {
                    highestReponseTime = (int)requestTime;
                }

                cumulativeRequestTimes += requestTime;
            }
        }
    }
}
