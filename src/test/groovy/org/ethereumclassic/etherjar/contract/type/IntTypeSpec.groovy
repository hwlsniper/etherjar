package org.ethereumclassic.etherjar.contract.type

import org.ethereumclassic.etherjar.model.Hex32
import spock.lang.Specification

class IntTypeSpec extends Specification {

    final static DEFAULT_TYPE = [] as IntType

    def "should create a default instance"() {
        expect:
        DEFAULT_TYPE.bytes == Hex32.SIZE_BYTES
        !DEFAULT_TYPE.signed
    }

    def "should create an instance with specified number of bits"() {
        def type = [40] as IntType

        expect:
        type.bytes == 5
        !type.signed
    }

    def "should return a minimal value (inclusive)"() {
        def type = [bits] as IntType

        expect:
        type.minValue == new BigInteger(str, 16)

        where:
        bits    | str
        8       | '-80'
        40      | '-8000000000'
        64      | '-8000000000000000'
        128     | '-80000000000000000000000000000000'
        256     | '-8000000000000000000000000000000000000000000000000000000000000000'
    }

    def "should return a maximal value (exclusive)"() {
        def type = [bits] as IntType

        expect:
        type.maxValue == new BigInteger(str, 16)

        where:
        bits    | str
        8       | '+80'
        40      | '+8000000000'
        64      | '+8000000000000000'
        128     | '+80000000000000000000000000000000'
        256     | '+8000000000000000000000000000000000000000000000000000000000000000'
    }

    def "should accept visitor"() {
        def visitor = Mock(Type.Visitor)

        when:
        DEFAULT_TYPE.visit visitor

        then:
        1 * visitor.visit(DEFAULT_TYPE as IntType)
        0 * _
    }

    def "should parse string representation"() {
        when:
        def opt = DEFAULT_TYPE.parse input

        then:
        opt.present
        opt.get().name == output

        where:
        input       | output
        'int'       | 'int256'
        'int8'      | 'int8'
        'int40'     | 'int40'
        'int64'     | 'int64'
        'int128'    | 'int128'
        'int256'    | 'int256'
    }

    def "should detect null string representation"() {
        when:
        DEFAULT_TYPE.parse null

        then:
        thrown NullPointerException
    }

    def "should ignore empty string representation"() {
        when:
        def opt = DEFAULT_TYPE.parse ''

        then:
        !opt.present
    }

    def "should ignore wrong string representation"() {
        when:
        def opt = DEFAULT_TYPE.parse input

        then:
        !opt.present

        where:
        _ | input
        _ | 'uint40'
        _ | 'bool'
    }

    def "should detect wrong inputs in string representation"() {
        when:
        DEFAULT_TYPE.parse input

        then:
        thrown IllegalArgumentException

        where:
        _ | input
        _ | 'int257'
        _ | 'int1024'
    }

    def "should return a canonical string representation" () {
        def type  = [size] as IntType

        expect:
        type.name == str

        where:
        size    | str
        8       | 'int8'
        40      | 'int40'
        64      | 'int64'
        128     | 'int128'
        256     | 'int256'
    }

    def "should be converted to a string representation"() {
        def type = [64] as IntType

        when:
        def str = type as String

        then:
        str ==~ /IntType\{.+}/
        str.contains "bytes=8"
    }
}
