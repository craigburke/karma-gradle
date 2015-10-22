package com.craigburke.gradle

import groovy.json.JsonBuilder

class KarmaModuleExtension {

    String basePath

    List frameworks = ['jasmine']
    List browsers = ['PhantomJS']
    List reporters = []
    Map preprocessors = [:]
    List files = []
    List exclude = []

    static final BROWSER_DEPENDENCIES = [
            'Chrome'           : ['karma-chrome-launcher'],
            'ChromeCanary'     : ['karma-chrome-launcher'],
            'PhantomJS'        : ['karma-phantomjs-launcher', 'phantomjs'],
            'Firefox'          : ['karma-firefox-launcher'],
            'Opera'            : ['karma-opera-launcher'],
            'Internet Explorer': ['karma-ie-launcher'],
            'Safari'           : ['karma-safari-launcher']
    ]

    static final FRAMEWORK_DEPENDENCIES = [
            'jasmine': ['karma-jasmine@2_0'],
            'mocha'  : ['karma-mocha'],
            'qunit'  : ['karma-qunit']
    ]

    static final REPORTER_DEPENDENCIES = [
            'growl'   : ['karma-growl-reporter'],
            'junit'   : ['karma-junit-reporter'],
            'teamcity': ['karma-teamcity-reporter'],
            'coverage': ['karma-coverage']
    ]

    private List additionalDependencies = []
    private Map additionalProperties = [:]

    void dependencies(List<String> dependencies) {
        additionalDependencies += dependencies
    }

    def propertyMissing(String name, value) {
        additionalProperties[name] = value
    }

    List getDependencies() {
        List dependencies = ['karma']

        browsers.each { String browser ->
            dependencies += BROWSER_DEPENDENCIES[browser]
        }
        frameworks.each { String framework ->
            dependencies += FRAMEWORK_DEPENDENCIES[framework]
        }
        reporters.each { String reporter ->
            dependencies += REPORTER_DEPENDENCIES[reporter]
        }
        dependencies += additionalDependencies
        dependencies.findAll { it }
    }

    String getConfigJson() {
        Map properties = [
                basePath     : "../${basePath}",
                files        : files,
                browsers     : browsers,
                frameworks   : frameworks,
                reporters    : reporters,
                preprocessors: preprocessors,
                exclude      : exclude
        ]

        additionalProperties.each { properties[it.key] = it.value }

        def json = new JsonBuilder()
        json(properties)
        json.toString()
    }

    String getConfigJavaScript() {
        "module.exports = function(config) { config.set(${configJson}) };"
    }
}

