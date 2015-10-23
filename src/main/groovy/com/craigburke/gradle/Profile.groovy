package com.craigburke.gradle

import groovy.transform.AutoClone

@AutoClone
class Profile {

    String testsBase = ''
    String librariesBase = 'bower/**/'
    String sourceBase = ''

    List<String> libraries = []
    List<String> source = []
    List<String> tests = []

    List<String> getFiles() {
        def files = []
        files += getFileList(ProfileFileType.LIBRARIES)
        files += getFileList(ProfileFileType.SOURCE)
        files += getFileList(ProfileFileType.TESTS)
        files
    }

    List<String> getFileList(ProfileFileType type) {
        switch (type) {
            case ProfileFileType.LIBRARIES:
                libraries.collect { "${librariesBase}${it}" }
                break

            case ProfileFileType.SOURCE:
                source.collect { "${sourceBase}${it}"}
                break

            case ProfileFileType.TESTS:
                tests.collect { "${testsBase}${it}"}
                break

        }

    }
}
