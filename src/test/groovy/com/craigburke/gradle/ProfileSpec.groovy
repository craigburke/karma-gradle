package com.craigburke.gradle

import spock.lang.Unroll
import static TestConstants.*
import static ProfileFileType.*

class ProfileSpec extends KarmaBaseSpec {

    def "default profile is applied when files aren't specified"() {
        when:
        karma { }

        and:
        karmaConfig.finalizeConfig()

        then:
        configMap.files == getProfile('default').files
    }

    def "default profile is not applied when files are specified"() {
        when:
        karma {
            files = ['foo.js']
        }

        then:
        configMap.files == ['foo.js']
    }

    @Unroll('can apply the #profileName profile')
    def "can apply a specific profile"() {
        when:
        karma {
            profile(profileName)
        }

        and:
        karmaConfig.finalizeConfig()

        then:
        configMap.files == getProfile(profileName).files

        where:
        profileName << PROFILE_LIST
    }

    @Unroll('can override libary files list for the #profileName profile')
    def 'can override libraries list'() {
        when:
        karma {
            profile(profileName) {
                libraryBases = ['']
                libraryFiles = ['lib.js']
            }
        }

        and:
        karmaConfig.finalizeConfig()

        then:
        configMap.files == ["lib.js"] + currentProfile.getFileListByType(SOURCE) + currentProfile.getFileListByType(TESTS)

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)


    }

    @Unroll('can override source file list for the #profileName profile')
    def 'can override sources list'() {
        when:
        karma {
            profile(profileName) {
                sourceBases = ['']
                sourceFiles = ['source.js']
            }
        }

        and:
        karmaConfig.finalizeConfig()

        then:
        configMap.files == currentProfile.getFileListByType(LIBRARIES) + ['source.js'] + currentProfile.getFileListByType(TESTS)

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

    @Unroll('can override test file list for the #profileName profile')
    def 'can override tests list'() {
        when:
        karma {
            profile(profileName) {
                testBases = ['']
                testFiles = ['test.js']
            }
        }

        and:
        karmaConfig.finalizeConfig()

        then:
        configMap.files == currentProfile.getFileListByType(LIBRARIES) + currentProfile.getFileListByType(SOURCE) + ['test.js']

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

    @Unroll('can override all file lists for the #profileName profile')
    def 'can override all file lists'() {
        when:
        karma {
            profile(profileName) {
                libraryBases = ['/libs/']
                libraryFiles = ['lib.js']
                sourceBases = ['/source/']
                sourceFiles = ['source.js']
                testBases = ['/test/']
                testFiles = ['test.js']
            }
        }

        and:
        karmaConfig.finalizeConfig()

        then:
        configMap.files == ['/libs/lib.js', '/source/source.js', '/test/test.js']

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

    @Unroll('can set a custom library base path #profileName profile')
    def 'can set a custom library base path'() {
        when:
        karma {
            profile(profileName) {
                libraryBases = ['/libs/', '/vendor/']
                libraryFiles = ['lib1.js', 'foo/bar/lib2.js']

                sourceFiles = []
                testFiles = []
            }
        }

        and:
        karmaConfig.finalizeConfig()

        then:
        configMap.files == [
                '/libs/lib1.js',
                '/vendor/lib1.js',
                '/libs/foo/bar/lib2.js',
                '/vendor/foo/bar/lib2.js'
        ]

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }


    @Unroll('can set a custom source base path #profileName profile')
    def 'can set a custom source base paths'() {
        when:
        karma {
            profile(profileName) {
                sourceBases = ['/src/']
                sourceFiles = ['source1.js', 'source2.js', 'foo/bar/source3.js']

                libraryFiles = []
                testFiles = []
            }
        }

        and:
        karmaConfig.finalizeConfig()

        then:
        configMap.files == ['/src/source1.js', '/src/source2.js', '/src/foo/bar/source3.js']

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

    @Unroll('can set a custom test base path #profileName profile')
    def 'can set a custom test base path'() {
        when:
        karma {
            profile(profileName) {
                testFiles = ['test1.js', 'test2.js', 'foo/bar/test3.js']
                testBases = ['/tests/']

                libraryFiles = []
                sourceFiles = []
            }
        }

        and:
        karmaConfig.finalizeConfig()

        then:
        configMap.files == ['/tests/test1.js', '/tests/test2.js', '/tests/foo/bar/test3.js']

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

    @Unroll('library defaults set when using asset pipeline for profile #profileName')
    def 'Defaults set when using asset pipeline'() {
        when:
        karma {
            profile(profileName) {
                sourceFiles = []
                testFiles = []
            }
        }

        and:
        karmaConfig.finalizeConfig(true, 'assets')

        then:
        configMap.files == Profile.getFileList(bases, currentProfile.libraryFilesDefault)

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
        bases = ['assets/bower/'] + currentProfile.libraryBaseDefault as List<String>
    }

    @Unroll('library defaults set when not using asset pipeline for profile #profileName')
    def 'Library defaults set when not using asset pipeline'() {
        when:
        karma {
            profile(profileName) {
                sourceFiles = []
                testFiles = []
            }
        }
        and:
        karmaConfig.finalizeConfig(false)

        then:
        configMap.files == Profile.getFileList(currentProfile.libraryBaseDefault, currentProfile.libraryFilesDefault)

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

    @Unroll('Source defaults set when using asset pipeline for profile #profileName')
    def 'Source defaults set when using asset pipeline'() {
        when:
        karma {
            profile(profileName) {
                libraryFiles = []
                testFiles = []
            }
        }

        and:
        karmaConfig.finalizeConfig(true, 'assets')

        then:
        configMap.files == Profile.getFileList(['assets/'], currentProfile.sourceFilesDefault)

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

    @Unroll('Source defaults set when not using asset pipeline for profile #profileName')
    def 'Source defaults set when not using asset pipeline'() {
        when:
        karma {
            profile(profileName) {
                libraryFiles = []
                testFiles = []
            }
        }
        and:
        karmaConfig.finalizeConfig(false)

        then:
        configMap.files == Profile.getFileList(currentProfile.sourceBasesDefault, currentProfile.sourceFilesDefault)

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

    @Unroll('Test defaults set when using asset pipeline for profile #profileName')
    def 'Test defaults set when using asset pipeline'() {
        when:
        karma {
            profile(profileName) {
                libraryFiles = []
                sourceFiles = []
            }
        }

        and:
        karmaConfig.finalizeConfig(true, 'assets')

        then:
        configMap.files == Profile.getFileList(testBases, currentProfile.testFilesDefault)

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
        testBases =  ['assets/'] + currentProfile.testBasesDefault as List<String>
    }

    @Unroll('Test defaults set when not using asset pipeline for profile #profileName')
    def 'Test defaults set when not using asset pipeline'() {
        when:
        karma {
            profile(profileName) {
                libraryFiles = []
                sourceFiles = []
            }
        }
        and:
        karmaConfig.finalizeConfig(false)

        then:
        configMap.files == Profile.getFileList(currentProfile.testBasesDefault, currentProfile.testFilesDefault)

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

}
