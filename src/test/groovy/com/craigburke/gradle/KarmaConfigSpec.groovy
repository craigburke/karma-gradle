package com.craigburke.gradle

import groovy.json.JsonSlurper
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import static KarmaConstants.*

class KarmaConfigSpec extends Specification {
    @Shared
    KarmaModuleExtension karmaConfig

    def setup() {
        karmaConfig = new KarmaModuleExtension()
    }

    @Unroll('Reporter #reporter is added to config file')
    def "reporters are added to config file"() {
        karmaConfig.reporters = [reporter]

        expect:
        configMap['reporters'] == [reporter]

        where:
        reporter << REPORTERS
    }

    @Unroll('Browser #browser is added to config file')
    def "Browsers are added to config file"() {
        karmaConfig.browsers = [browser]

        expect:
        configMap['browsers'] == [browser]

        where:
        browser << BROWSERS
    }

    @Unroll('Framework #framework is added to config file')
    def "frameworks are added to config file"() {
        karmaConfig.frameworks = [framework]

        expect:
        configMap['frameworks'] == [framework]

        where:
        framework << FRAMEWORKS
    }

    @Unroll('Add addition property #property')
    def "additional properties added to DSL"() {
        setup:
        def configBlock = configClosure {
            this[property] = value
        }

        when:
        configBlock()

        then:
        configMap[property] == value

        where:
        property      | value
        'stringProp'  | 'bar'
        'booleanProp' | true
        'mapProp'     | ['foo': ['bar': 999]]
        'arrayProp'   | ['foo', 'bar', 'foobar']
    }

    @Unroll('Override #defaultDependency with #overrideDependency')
    def "override dependencies with a specfic version"() {
        expect:
        karmaConfig.dependencies.contains(defaultDependency)

        and:
        !karmaConfig.dependencies.contains(overrideDependency)

        when:
        karmaConfig.dependencies([overrideDependency])

        then:
        karmaConfig.dependencies.contains(overrideDependency)

        and:
        !karmaConfig.dependencies.contains(defaultDependency)

        where:
        defaultDependency | overrideDependency
        'karma'           | 'karma@v1'
        'karma'           | 'karma@foo'
        'phantomjs'       | 'phantomjs@1'
        'phantomjs'       | 'phantomjs@foo'
    }

    private Closure configClosure(Closure closure) {
        closure.rehydrate(karmaConfig, karmaConfig, karmaConfig)
    }

    private getConfigMap() {
        new JsonSlurper().parseText(karmaConfig.configJson)
    }

}
