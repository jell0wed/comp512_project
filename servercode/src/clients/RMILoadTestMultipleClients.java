package clients;

import java.util.*;

public class RMILoadTestMultipleClients {
    private int numberOfclients,
                requestPerSeconds;
    private ArrayList<RMILoadTestClient> clients;

    public static void main(String[] args) {
        RMILoadTestMultipleClients loadTest = new RMILoadTestMultipleClients(2, 1);
        loadTest.run();
    }

    public RMILoadTestMultipleClients(int _numberOfclients, int _requestPerSeconds) {
        numberOfclients = _numberOfclients;
        requestPerSeconds = _requestPerSeconds;

        clients = new ArrayList<RMILoadTestClient>();
    }

    public void run() {
        float clientRequestPerSeconds = 1.0f / ((float) numberOfclients / (float)requestPerSeconds);

        try {
            for (int client = 0; client < numberOfclients; client++) {
                System.out.println("Starting client " + client);
                RMILoadTestClient clientClass = new RMILoadTestClient(clientRequestPerSeconds, client);
                Thread clientThread = new Thread(clientClass);

                clientThread.start();
                clients.add(clientClass);

                // wait between 0.5s and 1s to start new thread
                Thread.sleep(500 + (int) (Math.random() * 500));
            }
        } catch(Exception e) {
            System.err.println("Client error: " + e.toString());
            e.printStackTrace();
        }
    }
}
