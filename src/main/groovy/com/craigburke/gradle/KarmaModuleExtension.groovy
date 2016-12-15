/*
 * Copyright 2016 Craig Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.craigburke.gradle

import groovy.json.JsonBuilder
import static KarmaConstants.*

class KarmaModuleExtension {

    private static final String START_FUNC ='#startKarmaFunction#'
    private static final String END_FUNC ='#endKarmaFunction#'

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
        def overriddenDependencies = dependencies.findAll {
            simpleAdditionalDependencies?.contains(getSimpleDependency(it))
        }
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
        replaceFunctionTokens(json)
    }

    private static String replaceFunctionTokens(JsonBuilder config) {
        config.toPrettyString()
                .replaceAll(/"$START_FUNC/, '')
                .replaceAll(/$END_FUNC"/, '')
    }

    String getConfigJavaScript() {
        "module.exports = function(config) { config.set(${configJson}) };"
    }

    static String jsFunction(String function) {
        "$START_FUNC$function$END_FUNC"
    }
}

