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

    @CommandLine.ArgGroup(multiplicity = "1")
    RunMode runMode;

    static class RunMode {
        @CommandLine.Option(names = { "-c", "--connect" }, required = true, description = "Connect to remote server (client).", paramLabel = "SRV")
        String remoteServer;

        @CommandLine.Option(names = { "-s", "--server" }, required = true, description = "Run server and wait for client (server).")
        boolean runServer = false;
    }

    @CommandLine.Option(names = { "-p", "--port" }, paramLabel = "NUM", description = "Network port [default: ${DEFAULT-VALUE}].")
    int port = 4445;

    @CommandLine.Option(names = { "-b", "--buffer" }, paramLabel = "NUM", description = "Buffer size in kB [default: ${DEFAULT-VALUE}].")
    int bufferSize = 128;

    //@CommandLine.Option(names = { "-r", "--restart" }, description = "Restart when done (server) [default: ${DEFAULT-VALUE}].")
    //boolean restart = false;


    @Override
    public Integer call() {

        try {
            if (runMode.runServer) {
                runServer();
            } else if (runMode.remoteServer != null) {
                runClient(runMode.remoteServer);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }

        return 0;
    }


    public static void main(String... args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }



    private void runClient(String remoteHost) throws InterruptedException, IOException {
        new TcpClient(remoteHost, port, bufferSize);
    }


    private void runServer() throws IOException, InterruptedException {
        TcpServer tcpServer = new TcpServer(port, bufferSize);
        tcpServer.start();
        tcpServer.join();
    }

}
