package biz.nellemann.jnetcat;

import java.io.*;
import java.net.*;

public class TcpClient {

    public TcpClient(String hostname, int port, int bufferSize) throws IOException {

        Statistics statistics = new Statistics();

        InetAddress address = InetAddress.getByName(hostname);
        Socket socket = new Socket(address, port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        byte[] buffer = new byte[bufferSize * 1024];

        int bytesOut;
        while ((bytesOut = System.in.read(buffer)) > 0) {
            out.write(buffer, 0, bytesOut);
            statistics.tick();
            statistics.transferBytes(bytesOut);
        }

        out.close();
        socket.close();

        statistics.printSummary();
    }

}
