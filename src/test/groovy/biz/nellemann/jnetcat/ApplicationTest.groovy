package biz.nellemann.jnetcat

import picocli.CommandLine
import spock.lang.Specification

import java.net.ServerSocket

class ApplicationTest extends Specification {

    Application application


    def "server and client run modes are mutually exclusive"() {
        when:
        int exitCode = runArgs("-s", "-c", "localhost")

        then:
        exitCode == 2
    }


    def "a run mode must be given"() {
        when:
        int exitCode = runArgs()

        then:
        exitCode == 2
    }


    def "port outside 1..65535 is rejected"() {
        expect:
        runArgs("-s", "-p", "0") == 2
        runArgs("-s", "-p", "70000") == 2
    }


    def "buffer size outside 1..65536 kB is rejected"() {
        expect:
        runArgs("-s", "-b", "0") == 2
        runArgs("-s", "-b", "65537") == 2
    }


    def "invalid combination of valid options is rejected by picocli"() {
        when:
        int exitCode = runArgs("--bogus")

        then:
        exitCode == 2
    }


    def "server mode parses with only -s"() {
        when:
        application = new Application()
        new CommandLine(application).parseArgs("-s")

        then:
        application.runMode.runServer
        !application.restart
        application.port == 4445
        application.bufferSize == 128
    }


    def "restart option is parsed"() {
        when:
        application = new Application()
        new CommandLine(application).parseArgs("-s", "--restart")

        then:
        application.runMode.runServer
        application.restart
    }


    def "client mode parses with -c and stores the remote server"() {
        when:
        application = new Application()
        new CommandLine(application).parseArgs("-c", "server.example.org", "-p", "1234", "-b", "8")

        then:
        !application.runMode.runServer
        application.runMode.remoteServer == "server.example.org"
        application.port == 1234
        application.bufferSize == 8
    }


    def "client exits with code 1 when the server is unreachable"() {
        given: "a free port with nothing listening"
        int freePort = findFreePort()

        when:
        int exitCode = runArgs("-c", "localhost", "-p", String.valueOf(freePort))

        then:
        exitCode == 1
    }


    private int runArgs(String... args) {
        return new CommandLine(new Application()).execute(args)
    }

    private int findFreePort() {
        new ServerSocket(0).withCloseable { it.localPort }
    }
}
