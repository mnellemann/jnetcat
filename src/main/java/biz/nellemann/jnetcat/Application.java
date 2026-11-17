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


import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.concurrent.Callable;


@Command(name = "jnetcat", mixinStandardHelpOptions = true,
    versionProvider = VersionProvider.class,
    description = "For more information visit https://github.com/mnellemann/jnetcat")
public class Application implements Callable<Integer> {

    static final int MIN_PORT = 1;
    static final int MAX_PORT = 65535;
    static final int MAX_BUFFER_SIZE_KB = 65536;

    @CommandLine.ArgGroup(multiplicity = "1")
    RunMode runMode;

    static class RunMode {
        @CommandLine.Option(names = { "-c", "--connect" }, required = true, description = "Connect to remote server (client).", paramLabel = "SRV")
        String remoteServer;

        @CommandLine.Option(names = { "-s", "--server" }, required = true, description = "Run server and wait for client (server).")
        boolean runServer = false;
    }

    @CommandLine.Option(names = { "-p", "--port" }, paramLabel = "NUM", description = "Network port [range: 1..65535, default: ${DEFAULT-VALUE}].")
    int port = 4445;

    @CommandLine.Option(names = { "-b", "--buffer" }, paramLabel = "NUM", description = "Buffer size in kB [range: 1..65536, default: ${DEFAULT-VALUE}].")
    int bufferSize = 128;

    @CommandLine.Option(names = { "-r", "--restart" }, description = "Keep running and wait for the next client (server).")
    boolean restart = false;


    @Override
    public Integer call() {

        try {
            validateOptions();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return 2;
        }

        try {
            if (runMode.runServer) {
                runServer();
            } else if (runMode.remoteServer != null) {
                runClient(runMode.remoteServer);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 1;
        }

        return 0;
    }


    private void validateOptions() {
        if (port < MIN_PORT || port > MAX_PORT) {
            throw new IllegalArgumentException(String.format("Port must be between %d and %d: %d", MIN_PORT, MAX_PORT, port));
        }
        if (bufferSize < 1 || bufferSize > MAX_BUFFER_SIZE_KB) {
            throw new IllegalArgumentException(String.format("Buffer size must be between 1 and %d kB: %d", MAX_BUFFER_SIZE_KB, bufferSize));
        }
    }


    public static void main(String... args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }


    private void runClient(String remoteHost) throws IOException {
        new TcpClient(remoteHost, port, bufferSize * 1024).run();
    }


    private void runServer() throws IOException, InterruptedException {
        try (TcpServer tcpServer = new TcpServer(port, bufferSize * 1024, restart)) {
            Thread serverThread = new Thread(tcpServer, "jnetcat-server");
            serverThread.start();
            serverThread.join();
        }
    }

}
