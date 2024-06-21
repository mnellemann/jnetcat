package biz.nellemann.jnetcat;

import java.io.*;
import java.net.*;

public class TcpClient {

    public TcpClient(String hostname, int port, int bufferSize) throws IOException {

        InetAddress address = InetAddress.getByName(hostname);
        Socket socket = new Socket(address, port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        byte[] buffer = new byte[bufferSize * 1024];

        long totalBytesSent = 0;
        int bytesOut;
        while ((bytesOut = System.in.read(buffer)) > 0) {
            totalBytesSent += bytesOut;
            out.write(buffer, 0, bytesOut);
        }

        out.close();
        socket.close();

        System.err.println("bytes sent: " + totalBytesSent);
    }

}
