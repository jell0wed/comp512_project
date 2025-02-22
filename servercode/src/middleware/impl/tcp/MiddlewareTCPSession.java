package middleware.impl.tcp;


import ResImpl.Trace;
import ResImpl.exceptions.TransactionException;
import middleware.exceptions.MiddlewareBaseException;
import protocol.requests.BaseTCPRequest;
import protocol.responses.BaseTCPResponse;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by jpoisson on 2017-09-28.
 */
class MiddlewareTCPSession {
    private Socket clientSock;
    private MiddlewareTCPServer server;
    private ObjectOutputStream objOutStream;
    private ObjectInputStream objInStream;

    MiddlewareTCPSession(MiddlewareTCPServer server, Socket clientSock) {
        this.server = server;
        this.clientSock = clientSock;

        this.server.acceptClientConnection(this.clientSock);
        this.initializeSockStreams();
    }

    private void initializeSockStreams() {
        try {
            this.objOutStream = new ObjectOutputStream(this.clientSock.getOutputStream());
            this.objInStream = new ObjectInputStream(this.clientSock.getInputStream());
        } catch (IOException e) {
            throw new MiddlewareBaseException("Unable to initialize object streams", e);
        }
    }

    void listenSession() {
        try {
            while(true) {
                try {
                    Object requestObj = (BaseTCPRequest) this.objInStream.readObject();
                    BaseTCPRequest request = (BaseTCPRequest) requestObj;

                    if(request != null) {
                        BaseTCPResponse response = null;
                        try {
                            response = request.executeRequest(this.server.getMiddlewareInterface());
                        } catch (TransactionException e) {
                            Trace.error(e.getMessage());
                        }
                        this.objOutStream.writeObject(response);
                    } else {
                        Trace.error("Received invalid object");
                    }
                } catch(EOFException e) { // stream reached the end, close the connection
                    break;
                } catch (IOException e) {
                    Trace.error("IOException while receiving object : " + e.getMessage());
                    break;
                } catch (ClassNotFoundException e) {
                    Trace.error("Received invalid request from client");
                    break;
                }
            }
        } finally {
            this.server.deleteClientConnection(this.clientSock);
        }
    }
}
