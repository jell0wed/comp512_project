package middleware.impl.tcp;

import ResImpl.Trace;
import middleware.impl.tcp.requests.impl.AddFlightRequest;
import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by jpoisson on 2017-09-28.
 */
public class MiddlewareTCPClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket clientSock = new Socket("localhost", 8080);
        ObjectOutputStream objOut = new ObjectOutputStream(clientSock.getOutputStream());
        ObjectInput objIn = new ObjectInputStream(clientSock.getInputStream());

        Trace.info("Connected to localhost:8080");

        AddFlightRequest addFlightReq = new AddFlightRequest(10, 10, 100);
        objOut.writeObject(addFlightReq);

        Object respObj = (MiddlewareBaseTCPResponse) objIn.readObject();
        if(respObj == null) {
            throw new RuntimeException("Invalid response from server");
        }

        MiddlewareBaseTCPResponse resp = (MiddlewareBaseTCPResponse) respObj;
        Trace.info("Received response type = " + resp.type);
    }
}
