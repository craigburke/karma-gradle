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
            'jasmine': ['karma-jasmine', 'jasmine-core'],
            'mocha'  : ['karma-mocha', 'mocha'],
            'qunit'  : ['karma-qunit', 'qunitjs']
    ]

    static final REPORTER_DEPENDENCIES = [
            'growl'   : ['karma-growl-reporter'],
            'junit'   : ['karma-junit-reporter'],
            'teamcity': ['karma-teamcity-reporter'],
            'coverage': ['karma-coverage']
    ]

    static final List<String> LIBRARY_BASE_PATTERNS = [
            '**/assets/bower_components/',
            '**/assets/bower/',
            '**/assets/vendor/',
            '**/assets/libs/'
    ]

    static final List<String> SOURCE_BASE_PATTERNS = [
            'src/assets/'
    ]

    static final List<String> SOURCE_FILE_PATTERNS = [
            '**/*!(spec|Spec).js'
    ]

    static final List<String> ANGULAR_SOURCE_FILE_PATTERNS = [
            '**/app.js',
            '**/application.js',
            '**/*.module.js',
            '**/!(controllers|directives|services|domain|conf|config)/*!(Spec|spec).js',
            '**/*!(Spec|spec).js'
    ]

    static final List<String> TEST_BASE_PATTERNS = ['src/test/']

    static final List<String> TEST_FILE_PATTERNS = [
            '**/*spec.js',
            '**/*Spec.js',
            '**/test/**/*.js',
            '**/tests/**/*.js',
            '**/mock/**/*.js',
            '**/spec/**/*.js',
    ]

    static final Map<String, Profile> PROFILES = [
            'default'  : new Profile(
                    libraryBaseDefault: LIBRARY_BASE_PATTERNS,
                    libraryFilesDefault: ['**/*.js'],
                    sourceBasesDefault: SOURCE_BASE_PATTERNS,
                    sourceFilesDefault: SOURCE_FILE_PATTERNS,
                    testBasesDefault: TEST_BASE_PATTERNS,
                    testFilesDefault: TEST_FILE_PATTERNS
            ),
            'angularJS': new Profile(
                    libraryBaseDefault: LIBRARY_BASE_PATTERNS,
                    libraryFilesDefault: ['**/jquery.js', '**/angular.js', '**/*.js'],
                    sourceBasesDefault: SOURCE_BASE_PATTERNS,
                    sourceFilesDefault: ANGULAR_SOURCE_FILE_PATTERNS,
                    testBasesDefault: TEST_BASE_PATTERNS,
                    testFilesDefault: TEST_FILE_PATTERNS
            )
    ]

}
