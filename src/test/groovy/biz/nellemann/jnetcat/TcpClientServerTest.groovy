package biz.nellemann.jnetcat

import spock.lang.Shared
import spock.lang.Specification

class TcpClientServerTest extends Specification {

    static final int port = 9876;
    static final int bufferSize = 2;

    @Shared
    TcpServer tcpServer = new TcpServer(port, bufferSize)

    // run before every feature method
    def setup() {
        tcpServer.start();
    }

    // run after every feature method
    def cleanup() {
    }

    // run before the first feature method
    def setupSpec() {
    }

    // run after the last feature method
    def cleanupSpec() {
    }


    def "test client to server communication"() {
        when:
        TcpClient client = new TcpClient("localhost", port, bufferSize)

        then:
        noExceptionThrown()
    }

}
