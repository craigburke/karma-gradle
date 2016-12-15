package com.craigburke.gradle

import spock.lang.Unroll
import static TestConstants.*

class KarmaConfigSpec extends KarmaBaseSpec {

    @Unroll('Reporter #reporter is added to config file')
    def "reporters are added to config file"() {
        karmaConfig.reporters = [reporter]
        karmaConfig.finalizeConfig()

        expect:
        configMap['reporters'] == [reporter]

        where:
        reporter << REPORTER_LIST
    }

    @Unroll('Browser #browser is added to config file')
    def "Browsers are added to config file"() {
        karmaConfig.browsers = [browser]
        karmaConfig.finalizeConfig()

        expect:
        configMap['browsers'] == [browser]

        where:
        browser << BROWSER_LIST
    }

    @Unroll('Framework #framework is added to config file')
    def "frameworks are added to config file"() {
        karmaConfig.frameworks = [framework]
        karmaConfig.finalizeConfig()

        expect:
        configMap['frameworks'] == [framework]

        where:
        framework << FRAMEWORK_LIST
    }

    @Unroll('Add addition property #property')
    def "additional properties added to DSL"() {
        when:
        karma {
            this[property] = value
        }

        and:
        karmaConfig.finalizeConfig()

        then:
        configMap[property] == value

        where:
        property      | value
        'stringProp'  | 'bar'
        'booleanProp' | true
        'mapProp'     | ['foo': ['bar': 999]]
        'arrayProp'   | ['foo', 'bar', 'foobar']
    }

    def "it should have a function in the json"(){
        given:
            String jsFunction = 'function(){console.log(\'hi\');}'
            KarmaModuleExtension temp = new KarmaModuleExtension()
            karma {
                this['myFunc'] = temp.jsFunction(jsFunction)
            }
            karmaConfig.finalizeConfig()
        when:
            String config = karmaConfig.configJson
        then:
            config.contains('"myFunc": ' + jsFunction)
    }

    def "it should add a property with the function tokens"(){
        given:
            String jsFunction = 'function(){console.log(\'hi\');}'
            karmaConfig.finalizeConfig()
        when:
            String encodedFunc = karmaConfig.jsFunction(jsFunction)

        then:
            encodedFunc == "#startKarmaFunction#$jsFunction#endKarmaFunction#"
    }

}
