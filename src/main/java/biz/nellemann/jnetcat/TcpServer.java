/*
   Copyright 2024 mark.nellemann@gmail.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package biz.nellemann.jnetcat;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer implements Runnable, Closeable {

    private final int bufferSizeBytes;
    private final boolean restart;
    private final OutputStream destination;
    private final ServerSocket serverSocket;

    private volatile boolean running = true;

    public TcpServer(int port, int bufferSizeBytes, boolean restart) throws IOException {
        this(port, bufferSizeBytes, restart, System.out);
    }

    TcpServer(int port, int bufferSizeBytes, boolean restart, OutputStream destination) throws IOException {
        if (bufferSizeBytes < 1) {
            throw new IllegalArgumentException("Buffer size must be at least 1 byte: " + bufferSizeBytes);
        }
        this.bufferSizeBytes = bufferSizeBytes;
        this.restart = restart;
        this.destination = destination;
        this.serverSocket = new ServerSocket(port);
    }


    @Override
    public void run() {
        while (running) {
            handleConnection();
            if (!restart) {
                running = false;
            }
        }
    }


    @Override
    public void close() throws IOException {
        running = false;
        if (!serverSocket.isClosed()) {
            serverSocket.close();
        }
    }


    private void handleConnection() {

        Socket client;
        try {
            client = serverSocket.accept();
        } catch (IOException e) {
            // The server socket was closed or accept failed.
            if (running) {
                System.err.println("Error: " + e.getMessage());
            }
            return;
        }

        if (!running) {
            try { client.close(); } catch (IOException ignored) { }
            return;
        }

        Statistics statistics = new Statistics();
        try {
            statistics.start();
            receive(client, statistics);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            statistics.close();
            try { client.close(); } catch (IOException ignored) { }
        }
    }


    private void receive(Socket client, Statistics statistics) throws IOException {
        DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
        byte[] buffer = new byte[bufferSizeBytes];

        int bytesIn;
        while ((bytesIn = in.read(buffer)) != -1) {
            destination.write(buffer, 0, bytesIn);
            statistics.transferBytes(bytesIn);
        }

        destination.flush();
    }

}
