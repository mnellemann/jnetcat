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
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class TcpClient {

    private final String hostname;
    private final int port;
    private final int bufferSizeBytes;
    private final InputStream source;

    public TcpClient(String hostname, int port, int bufferSizeBytes) {
        this(hostname, port, bufferSizeBytes, System.in);
    }

    TcpClient(String hostname, int port, int bufferSizeBytes, InputStream source) {
        if (bufferSizeBytes < 1) {
            throw new IllegalArgumentException("Buffer size must be at least 1 byte: " + bufferSizeBytes);
        }
        this.hostname = hostname;
        this.port = port;
        this.bufferSizeBytes = bufferSizeBytes;
        this.source = source;
    }


    public void run() throws IOException {
        try (Socket socket = new Socket(hostname, port)) {
            send(socket);
        }
    }


    private void send(Socket socket) throws IOException {
        Statistics statistics = new Statistics();
        try {
            statistics.start();
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            byte[] buffer = new byte[bufferSizeBytes];

            int bytesOut;
            while ((bytesOut = source.read(buffer)) != -1) {
                out.write(buffer, 0, bytesOut);
                statistics.transferBytes(bytesOut);
            }

            out.flush();
        } finally {
            statistics.close();
        }
    }

}
