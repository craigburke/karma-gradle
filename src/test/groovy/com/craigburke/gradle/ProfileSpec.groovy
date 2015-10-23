package com.craigburke.gradle

import spock.lang.Unroll
import static TestConstants.*
import static ProfileFileType.*

class ProfileSpec extends KarmaBaseSpec {

    def "default profile is applied when files aren't specified"() {
        when:
        karma { }

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

        then:
        configMap.files == getProfile(profileName).files

        where:
        profileName << PROFILE_LIST
    }

    @Unroll('can override libaries list for the #profileName profile')
    def 'can override libraries list'() {
        when:
        karma {
            profile(profileName) {
                libraries = ['lib.js']
            }
        }

        then:
        configMap.files == ["${currentProfile.librariesBase}lib.js"] + currentProfile.getFileList(SOURCE) + currentProfile.getFileList(TESTS)

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)


    }

    @Unroll('can override sources list for the #profileName profile')
    def 'can override sources list'() {
        when:
        karma {
            profile(profileName) {
                source = ['source.js']
            }
        }

        then:
        configMap.files == currentProfile.getFileList(LIBRARIES) + ["${currentProfile.sourceBase}source.js"] + currentProfile.getFileList(TESTS)

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

    @Unroll('can override tests list for the #profileName profile')
    def 'can override tests list'() {
        when:
        karma {
            profile(profileName) {
                tests = ['test.js']
            }
        }

        then:
        configMap.files == currentProfile.getFileList(LIBRARIES) + currentProfile.getFileList(SOURCE) + ["${currentProfile.testsBase}test.js"]

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

    @Unroll('can override all file lists for the #profileName profile')
    def 'can override all file lists'() {
        when:
        karma {
            profile(profileName) {
                libraries = ['lib.js']
                source = ['source.js']
                tests = ['test.js']
            }
        }

        then:
        configMap.files == ["${currentProfile.librariesBase}lib.js", "${currentProfile.sourceBase}source.js", "${currentProfile.testsBase}test.js"]

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

    @Unroll('can set a custom library base path #profileName profile')
    def 'can set a custom library base path'() {
        when:
        karma {
            profile(profileName) {
                libraries = ['lib1.js', 'lib2.js', 'foo/bar/lib3.js']
                librariesBase = '/libs/'

                source = []
                tests = []
            }
        }

        then:
        configMap.files == ['/libs/lib1.js', '/libs/lib2.js', '/libs/foo/bar/lib3.js']

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }


    @Unroll('can set a custom source base path #profileName profile')
    def 'can set a custom source base path'() {
        when:
        karma {
            profile(profileName) {
                source = ['source1.js', 'source2.js', 'foo/bar/source3.js']
                sourceBase = '/src/'

                libraries = []
                tests = []
            }
        }

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
                tests = ['test1.js', 'test2.js', 'foo/bar/test3.js']
                testsBase = '/tests/'

                libraries = []
                source = []
            }
        }

        then:
        configMap.files == ['/tests/test1.js', '/tests/test2.js', '/tests/foo/bar/test3.js']

        where:
        profileName << PROFILE_LIST
        currentProfile = getProfile(profileName)
    }

}
