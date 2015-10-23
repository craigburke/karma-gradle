package com.craigburke.gradle

class KarmaConstants {

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

    static final Map<String, Profile> PROFILES = [
            'default': new Profile(
                    libraries: ['**/*.js'],
                    source: ['**/!(*.spec).js'],
                    tests: ['**/*.spec.js']
            ),
            'angularJS': new Profile(
                    libraries: ['**/angular.js', '**/*.js'],
                    source: ['**/*.module.js', '**/!(*.spec).js'],
                    tests: ['**/*.spec.js']
            )
    ]

}
