package biz.nellemann.jnetcat

import spock.lang.Specification

class VersionProviderTest extends Specification {

    def "getVersion returns two version lines without throwing"() {
        when:
        String[] version = new VersionProvider().getVersion()

        then:
        version != null
        version.length == 2
        version[0] != null
        version[1] != null
        version[1].startsWith("JVM:")
    }

}
