package com.craigburke.gradle

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import static KarmaConstants.*

class ConfigDependenciesSpec extends Specification {
    @Shared KarmaModuleExtension karmaConfig

    def setup() {
        karmaConfig = new KarmaModuleExtension()
    }

    @Unroll('generate config dependencies for #browser browser')
    def "generate browser config dependencies"() {
        given:
        karmaConfig.browsers = [browser]
        def dependencies = karmaConfig.BROWSER_DEPENDENCIES[browser]

        expect:
        dependencies
        dependencies.every { karmaConfig.dependencies.contains(it) }

        where:
        browser << BROWSERS
    }

    @Unroll('generate config dependencies for #framework framework')
    def "generate framework config dependencies"() {
        given:
        karmaConfig.frameworks = [framework]
        def dependencies = karmaConfig.FRAMEWORK_DEPENDENCIES[framework]

        expect:
        dependencies
        dependencies.every { karmaConfig.dependencies.contains(it) }

        where:
        framework << FRAMEWORKS
    }

    @Unroll('generate config dependencies for #reporter reporter')
    def "generate reporter config dependencies"() {
        given:
        karmaConfig.reporters = [reporter]
        def dependencies = karmaConfig.REPORTER_DEPENDENCIES[reporter]

        expect:
        dependencies
        dependencies.every { karmaConfig.dependencies.contains(it) }

        where:
        reporter << REPORTERS
    }

}
