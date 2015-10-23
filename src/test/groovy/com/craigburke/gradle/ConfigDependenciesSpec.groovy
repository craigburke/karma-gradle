package com.craigburke.gradle

import spock.lang.Unroll
import static KarmaConstants.*
import static TestConstants.*

class ConfigDependenciesSpec extends KarmaBaseSpec {

    @Unroll('generate config dependencies for #browser browser')
    def "generate browser config dependencies"() {
        given:
        karmaConfig.browsers = [browser]
        def dependencies = BROWSER_DEPENDENCIES[browser]

        expect:
        dependencies
        dependencies.every { karmaConfig.dependencies.contains(it) }

        where:
        browser << BROWSER_LIST
    }

    @Unroll('generate config dependencies for #framework framework')
    def "generate framework config dependencies"() {
        given:
        karmaConfig.frameworks = [framework]
        def dependencies = FRAMEWORK_DEPENDENCIES[framework]

        expect:
        dependencies
        dependencies.every { karmaConfig.dependencies.contains(it) }

        where:
        framework << FRAMEWORK_LIST
    }

    @Unroll('generate config dependencies for #reporter reporter')
    def "generate reporter config dependencies"() {
        given:
        karmaConfig.reporters = [reporter]
        def dependencies = REPORTER_DEPENDENCIES[reporter]

        expect:
        dependencies
        dependencies.every { karmaConfig.dependencies.contains(it) }

        where:
        reporter << REPORTER_LIST
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

}
