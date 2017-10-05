package middleware.impl.tcp;

import ResImpl.Trace;
import middleware.MiddlewareServer;
import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.RemoteResourceManagerImplementationTypes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class MiddlewareTCPServer extends MiddlewareServer {
    private static final int tcpPort = 8080;
    private static final int maxConnections = 10;

    private ServerSocket sock;
    private Set<Socket> clientConnections;
    private ExecutorService clientSessions;

    public MiddlewareTCPServer(String... availableRMs) {
        super(RemoteResourceManagerImplementationTypes.TCP, availableRMs);
    }

    @Override
    protected void initializeServer() {
        this.initializeServerSocket();
        this.listen();
    }

    private void initializeServerSocket() {
        try {
            this.clientConnections = new HashSet<Socket>();
            this.clientSessions = Executors.newFixedThreadPool(maxConnections);
            this.sock = new ServerSocket(tcpPort);
        } catch (IOException e) {
            throw new MiddlewareBaseException("Unable to configure TCP server socket on port " + tcpPort, e);
        }
    }

    private void listen() {
        Trace.info("Listening for incoming client connection on port " + tcpPort);
        while(true) {
            try {
                // create new client session
                Socket clientSock = this.sock.accept();
                MiddlewareTCPSession clientSession = new MiddlewareTCPSession(this, clientSock);
                this.clientSessions.submit(clientSession::listenSession);
            } catch (IOException e) {
                Trace.error("IOException while accepting incoming client connection");
                Trace.error(e.getMessage());
            }
        }
    }

    void acceptClientConnection(Socket clientSock) {
        this.clientConnections.add(clientSock);
        Trace.info(String.format("Accepted client %s connection.", clientSock.getInetAddress().toString()));
    }

    void deleteClientConnection(Socket clientSock) {
        this.clientConnections.remove(clientSock);
        Trace.info(String.format("Removed client %s connection.", clientSock.getInetAddress().toString()));
    }

    public static void main(String[] args) {
        MiddlewareTCPServer server = new MiddlewareTCPServer(
                "//localhost:10001/rmCar",
                "//localhost:10002/rmFlight",
                "//localhost:10003/rmRoom",
                "//localhost:10004/rmOther"
        );
    }
}
