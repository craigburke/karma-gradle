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

    private List<String> additionalDependencies = []
    private Map configProperties = [:]

    void dependencies(List<String> dependencies) {
        additionalDependencies += dependencies
    }

    def propertyMissing(String name, value) {
        configProperties[name] = value
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

        def simpleAdditionalDependencies = additionalDependencies.collect { getSimpleDependency(it) }
        def overridenDependencies = dependencies.findAll { simpleAdditionalDependencies.contains(getSimpleDependency(it)) }
        dependencies = dependencies - overridenDependencies + additionalDependencies
        dependencies.findAll { it }
    }

    private String getSimpleDependency(String dependency) {
        dependency.split('@').first()
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

        configProperties.each { properties[it.key] = it.value }

        def json = new JsonBuilder()
        json(properties)
        json.toString()
    }

    String getConfigJavaScript() {
        "module.exports = function(config) { config.set(${configJson}) };"
    }
}

