package biz.nellemann.jnetcat

import spock.lang.Specification

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.ServerSocket
import java.util.concurrent.ThreadLocalRandom

class TcpClientServerTest extends Specification {

    static final int BUFFER_SIZE = 4 * 1024

    int port
    ByteArrayOutputStream received
    TcpServer server
    Thread serverThread
    ServerSocket rebound

    def setup() {
        port = findFreePort()
        received = new ByteArrayOutputStream()
    }

    def cleanup() {
        rebound?.close()
        server?.close()
        serverThread?.join(10_000)
    }


    def "client to server transfers data unchanged"() {
        given:
        byte[] data = randomBytes(512 * 1024)
        server = new TcpServer(port, BUFFER_SIZE, false, received)

        when:
        serverThread = Thread.start { server.run() }
        new TcpClient("localhost", port, BUFFER_SIZE, new ByteArrayInputStream(data)).run()
        serverThread.join(10_000)

        then:
        !serverThread.alive
        received.toByteArray() == data
    }


    def "server stops after a single client when restart is disabled"() {
        given:
        byte[] data = randomBytes(64 * 1024)
        server = new TcpServer(port, BUFFER_SIZE, false, received)

        when:
        serverThread = Thread.start { server.run() }
        new TcpClient("localhost", port, BUFFER_SIZE, new ByteArrayInputStream(data)).run()
        serverThread.join(10_000)

        then:
        !serverThread.alive
        received.toByteArray() == data
    }


    def "server serves multiple sequential clients in restart mode"() {
        given:
        byte[] first = randomBytes(100 * 1024)
        byte[] second = randomBytes(200 * 1024)
        server = new TcpServer(port, BUFFER_SIZE, true, received)

        when:
        serverThread = Thread.start { server.run() }
        new TcpClient("localhost", port, BUFFER_SIZE, new ByteArrayInputStream(first)).run()
        new TcpClient("localhost", port, BUFFER_SIZE, new ByteArrayInputStream(second)).run()
        server.close()
        serverThread.join(10_000)

        then:
        !serverThread.alive
        byte[] expected = new byte[first.length + second.length]
        System.arraycopy(first, 0, expected, 0, first.length)
        System.arraycopy(second, 0, expected, first.length, second.length)
        received.toByteArray() == expected
    }


    def "server frees the port when closed"() {
        given:
        server = new TcpServer(port, BUFFER_SIZE, true, received)
        serverThread = Thread.start { server.run() }

        when:
        server.close()
        serverThread.join(10_000)
        rebound = new ServerSocket(port)

        then:
        !serverThread.alive
        rebound.localPort == port
    }


    def "client and server reject non-positive buffer sizes"() {
        when:
        new TcpClient("localhost", port, 0)

        then:
        thrown(IllegalArgumentException)

        when:
        new TcpServer(port, -1, false)

        then:
        thrown(IllegalArgumentException)
    }


    private byte[] randomBytes(int size) {
        byte[] data = new byte[size]
        ThreadLocalRandom.current().nextBytes(data)
        return data
    }

    private int findFreePort() {
        new ServerSocket(0).withCloseable { it.localPort }
    }
}
