package clients;

import ResInterface.ResourceManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;

public class RMILoadTestClient2 {
    final String REGISTRY_HOST = "localhost";
    final int    REGISTRY_POST =  1099;
    final String REGISTRY_NAME = "rmMiddleware";

    private volatile boolean lastRequestDone;
    private Timer requestTimer;
    private long startTime = -1;
    private ResourceManager rm;
    private int requestCount = 0,
                earlyAbort = 0,
                requestErrors = 0,
                lowestReponseTime = 999999,
                highestReponseTime = 0,
                stopRequestCount = 10;
    private long cumulativeRequestTimes;

    public static void main(String[] args) {
//        if(args.length != 1) {
//            throw new RuntimeException("Usage: <requests per seconds>");
//        }
//
//        int requestPerSeconds = Integer.valueOf(args[0]);
        RMILoadTestClient2 client = new RMILoadTestClient2(2);
    }

    public RMILoadTestClient2(int requestPerSeconds) {
        System.out.println("Starting load client with " + requestPerSeconds + " requests/s");

        try {
            Registry registry = LocateRegistry.getRegistry(REGISTRY_HOST, REGISTRY_POST);
            rm = (ResourceManager) registry.lookup(REGISTRY_NAME);

            lastRequestDone = true;

            long sleepTime = 1000/requestPerSeconds;

            System.out.println("Starting load test in a 100ms");

            while(true) {
                Thread request = new Thread(new RMILoadTestRequest());
                request.run();

                if(requestCount >= stopRequestCount) {
                    break;
                }

                Thread.sleep(sleepTime);
            }

            this.printAnalysis();
        } catch(Exception e) {
            System.err.println("Client error happened while running tests: " + e.toString());
            e.printStackTrace(System.err);
        }
    }

    public void timeAwareLogging(String string) {
        if(startTime == -1) {
            startTime = System.currentTimeMillis();
        }

        long difference = System.currentTimeMillis() - startTime;

        System.out.println("+" + difference + "ms " + string);
    }

    public void printAnalysis() {
        System.out.println("---- REPORT ----");

        System.out.println("Requests: " + requestCount);
        System.out.println("Early abort: " + earlyAbort + " (" + printFormattedPercentage(earlyAbort, requestCount) + "%)");
        System.out.println("Errored requests: " + requestErrors + " (" + printFormattedPercentage(requestErrors, requestCount) + "%)");
        System.out.println("Average response time: " + (float)cumulativeRequestTimes/(requestCount));
        System.out.println("Highest reponse time: " + lowestReponseTime);
        System.out.println("Lowest reponse time: " + highestReponseTime);
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

            //timeAwareLogging("Initiating request (" + requestCount + "/" + stopRequestCount + ")");

            lastRequestDone = false;
            long requestStartTime = System.currentTimeMillis();
            long requestEndTime = -1;

            // Request
            try {
                // Test add car
                int transId = rm.startTransaction();
                rm.addCars(transId, "mtl", 1, 100);
                rm.commitTransaction(transId);

                // Test read car
                int transId2 = rm.startTransaction();
                rm.queryCars(transId2, "mtl");
                rm.commitTransaction(transId2);

                // Test write + read (convert)
                int transId3 = rm.startTransaction();
                rm.queryCars(transId3, "mtl");
                rm.addCars(transId3, "mtl", 1, 200);
                rm.abortTransaction(transId3);

                // Test add flight
                int transId4 = rm.startTransaction();
                rm.addFlight(transId4, 9000, 2, 200);
                rm.queryCars(transId4, "mtl");
                rm.commitTransaction(transId4);

                // Test add room
                int transId5 = rm.startTransaction();
                rm.addRooms(transId5, "mtl", 2, 100);
                rm.abortTransaction(transId5);

            } catch(Exception e) {
                requestErrors++;
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();

            } finally {
                lastRequestDone = true;

                requestEndTime = System.currentTimeMillis();

                long requestTime = (requestEndTime - requestStartTime);
                //timeAwareLogging("Request time: " + requestTime + "ms");

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
