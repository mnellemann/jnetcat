package biz.nellemann.jnetcat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer extends Thread {

    private final int port;
    private final int bufferSize;

    public TcpServer(int port, int bufferSize) {
        this.port = port;
        this.bufferSize = bufferSize;
    }


    public void run() {

        Statistics statistics = new Statistics();

        try {
            ServerSocket socket = new ServerSocket(port);
            socket.setSoTimeout(0); // Wait indefinitely

            Socket server = socket.accept();
            DataInputStream in = new DataInputStream(server.getInputStream());


            byte[] buffer = new byte[bufferSize * 1024];

            int bytesIn;
            while ((bytesIn = in.read(buffer)) > 0) {
                System.out.write(buffer, 0, bytesIn);
                statistics.tick();
                statistics.transferBytes(bytesIn);
            }

            socket.close();
            server.close();

            statistics.printSummary();
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }

    }

}
