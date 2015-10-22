package com.craigburke.gradle

import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import static KarmaConstants.*

class KarmaConfigSpec extends Specification {
    @Shared KarmaModuleExtension karmaConfig

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

    private getConfigMap() {
        new JsonSlurper().parseText(karmaConfig.configJson)
    }

}
