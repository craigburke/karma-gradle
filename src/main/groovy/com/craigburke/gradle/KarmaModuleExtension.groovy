package com.craigburke.gradle

import groovy.json.JsonBuilder
import static KarmaConstants.*

class KarmaModuleExtension {

    String basePath
    boolean colors = true

    List frameworks = ['jasmine']
    List browsers = ['PhantomJS']
    List reporters = ['progress']

    Map preprocessors = [:]
    List files = []
    List exclude = []

    private List<String> additionalDependencies = []
    private Map configProperties = [:]

    private String profileName = 'default'
    private Closure profileConfig
    private Profile profile

    void profile(String profileName, Closure profileConfig = null) {
        this.profileName = profileName
        this.profileConfig = profileConfig
    }

    void finalizeConfig(boolean usesAssetPipeline = false, String assetPath = '') {
        profile = PROFILES[profileName].clone()
        profile.setDefaults(usesAssetPipeline, assetPath)

        if (profile && profileConfig) {
            profileConfig.rehydrate(profile, profile, profile).call()
        }
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
        List<String> karmaFiles = files ?: profile.files

        Map properties = [
                basePath     : basePath,
                colors       : colors,
                logLevel     : 'ERROR',
                files        : karmaFiles,
                browsers     : browsers,
                frameworks   : frameworks,
                reporters    : reporters,
                preprocessors: preprocessors,
                exclude      : exclude
        ]

        configProperties.each { properties[it.key] = it.value }

        def json = new JsonBuilder()
        json(properties)
        json.toPrettyString()
    }

    String getConfigJavaScript() {
        "module.exports = function(config) { config.set(${configJson}) };"
    }
}

