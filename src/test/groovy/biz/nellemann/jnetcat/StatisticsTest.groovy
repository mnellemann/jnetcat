package biz.nellemann.jnetcat

import spock.lang.Specification

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class StatisticsTest extends Specification {

    private static class MutableClock extends Clock {
        private Instant now

        MutableClock(Instant start) {
            this.now = start
        }

        void advance(long millis) {
            now = now.plusMillis(millis)
        }

        @Override
        ZoneId getZone() { ZoneId.systemDefault() }

        @Override
        Clock withZone(ZoneId zone) { this }

        @Override
        Instant instant() { now }
    }


    def "transferBytes accumulates interval and total counters"() {
        given:
        def stats = new Statistics()

        when:
        stats.transferBytes(100)
        stats.transferBytes(123)

        then:
        stats.bytesTransferredTotal == 223
    }


    def "tick computes the rate from the elapsed interval and prints status"() {
        given:
        def output = new ByteArrayOutputStream()
        def clock = new MutableClock(Instant.now())
        def stats = new Statistics(new PrintStream(output, true), clock)

        when:
        stats.transferBytes(5_000)
        clock.advance(1_000)
        stats.tick()

        then:
        stats.bytesPerSec == 5_000
        output.toString().contains("Status")
    }


    def "tick scales the rate to the actual interval length"() {
        given:
        def output = new ByteArrayOutputStream()
        def clock = new MutableClock(Instant.now())
        def stats = new Statistics(new PrintStream(output, true), clock)

        when:
        stats.transferBytes(2_000)
        clock.advance(500)
        stats.tick()

        then:
        stats.bytesPerSec == 4_000
    }


    def "closing prints a summary with the transferred total"() {
        given:
        def output = new ByteArrayOutputStream()
        def stats = new Statistics(new PrintStream(output, true))

        when:
        stats.transferBytes(1_000_000)
        stats.close()

        then:
        stats.bytesTransferredTotal == 1_000_000
        output.toString().contains("Summary")
    }


    def "rolling average resets when the window wraps"() {
        given:
        def output = new ByteArrayOutputStream()
        def clock = new MutableClock(Instant.now())
        def stats = new Statistics(new PrintStream(output, true), clock)

        when: "fill the whole rolling window"
        (1..300).each { i ->
            stats.transferBytes(i * 1000)
            clock.advance(1000)
            stats.tick()
        }

        then: "the window has wrapped and is empty"
        stats.averageBytesPerSec == 0

        when: "a new, much slower interval is recorded"
        stats.transferBytes(2_000)
        clock.advance(1000)
        stats.tick()

        then: "the average only reflects fresh samples, not stale ones"
        stats.averageBytesPerSec == 2_000
    }
}
