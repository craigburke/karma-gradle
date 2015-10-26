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
            'jasmine': ['karma-jasmine'],
            'mocha'  : ['karma-mocha'],
            'qunit'  : ['karma-qunit']
    ]

    static final REPORTER_DEPENDENCIES = [
            'growl'   : ['karma-growl-reporter'],
            'junit'   : ['karma-junit-reporter'],
            'teamcity': ['karma-teamcity-reporter'],
            'coverage': ['karma-coverage']
    ]

    static final List<String> LIBRARY_BASE_PATTERNS = [
            '**/bower_components/', '**/bower/', '**/vendor/'
    ]

    static final List<String> TEST_BASE_PATTERNS = [
            '**/tests/', 'src/test/js/'
    ]

    static final List<String> TEST_FILE_PATTERNS = [
            '**/*.spec.js',
            '**/tests/**.js',
            '**/mock/**',
            '**/spec/**',
    ]

    static final List<String> SOURCE_FILE_PATTERNS = [
            '**/!(*.spec).js'
    ]

    static final Map<String, Profile> PROFILES = [
            'default'  : new Profile(
                    libraryBaseDefault: LIBRARY_BASE_PATTERNS,
                    libraryFilesDefault: ['**/*.js'],
                    sourceBasesDefault: [''],
                    sourceFilesDefault: SOURCE_FILE_PATTERNS,
                    testBasesDefault: TEST_BASE_PATTERNS,
                    testFilesDefault: TEST_FILE_PATTERNS
            ),
            'angularJS': new Profile(
                    libraryBaseDefault: LIBRARY_BASE_PATTERNS,
                    libraryFilesDefault: ['**/angular.js', '**/*.js'],
                    sourceBasesDefault: [''],
                    sourceFilesDefault: ['**/*.module.js'] + SOURCE_FILE_PATTERNS,
                    testBasesDefault: TEST_BASE_PATTERNS,
                    testFilesDefault: ['**/angular-mocks.js'] + TEST_FILE_PATTERNS
            )
    ]

}
