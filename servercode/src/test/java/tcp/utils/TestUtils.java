package tcp.utils;

import ResImpl.Trace;
import protocol.requests.BaseTCPRequest;
import protocol.responses.BaseTCPResponse;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by jpoisson on 2017-10-02.
 */
public class TestUtils {
    private static Socket clientSock;
    private static ObjectOutputStream objOut;
    private static ObjectInput objIn;

    public static BaseTCPResponse send(BaseTCPRequest req) throws IOException, ClassNotFoundException {
        objOut.writeObject(req);
        Object respObj = (BaseTCPResponse) objIn.readObject();
        if(respObj == null) {
            throw new RuntimeException("Invalid response from server");
        }

        return (BaseTCPResponse) respObj;
    }

    public static void initializeClientSock() throws IOException {
        clientSock = new Socket("localhost", 8080);
        objOut = new ObjectOutputStream(clientSock.getOutputStream());
        objIn = new ObjectInputStream(clientSock.getInputStream());

        Trace.info("Connected to localhost:8080");
    }

    public static int newUniqueId() {
        return UUID.randomUUID().hashCode();
    }
}
