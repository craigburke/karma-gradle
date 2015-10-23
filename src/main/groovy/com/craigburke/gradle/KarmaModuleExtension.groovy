package com.craigburke.gradle

import groovy.json.JsonBuilder
import static KarmaConstants.*

class KarmaModuleExtension {

    String basePath

    List frameworks = ['jasmine']
    List browsers = ['PhantomJS']
    List reporters = ['progress']

    Map preprocessors = [:]
    List files = []
    List exclude = []

    private List<String> additionalDependencies = []
    private Map configProperties = [:]

    void profile(String profileName, Closure configClosure = null) {
        def profile = getProfile(profileName)

        if (profile) {
            if (configClosure) {
                configClosure.rehydrate(profile, profile, profile).call()
            }
            files += profile.files
        }
    }

    private Profile getProfile(String profileName) {
        PROFILES[profileName].clone()
    }

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
        def overriddenDependencies = dependencies.findAll { simpleAdditionalDependencies?.contains(getSimpleDependency(it)) }
        dependencies = dependencies - overriddenDependencies + additionalDependencies
        dependencies.findAll { it }
    }

    static String getSimpleDependency(String dependency) {
        dependency?.split('@')?.first()
    }

    String getConfigJson() {
        if (!files) {
            files = getProfile('default').files
        }

        Map properties = [
                basePath     : "../${basePath}",
                logLevel     : 'ERROR',
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

