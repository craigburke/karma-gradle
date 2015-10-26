package com.craigburke.gradle

import groovy.transform.AutoClone

@AutoClone
class Profile {

    List<String> libraryBaseDefault
    List<String> libraryBases
    List<String> libraryFilesDefault
    List<String> libraryFiles

    List<String> sourceBasesDefault
    List<String> sourceBases
    List<String> sourceFilesDefault
    List<String> sourceFiles

    List<String> testBasesDefault
    List<String> testBases
    List<String> testFilesDefault
    List<String> testFiles

    void setDefaults(boolean usesAssetPipeline = false, String assetPath = '', String assetPipelineCompileDir = '') {
        if (libraryBases == null) {
            libraryBases = usesAssetPipeline ? [] : libraryBaseDefault
        }
        if (libraryFiles == null) {
            libraryFiles = usesAssetPipeline ? [] : libraryFilesDefault
        }

        if (sourceBases == null) {
            sourceBases = usesAssetPipeline ? ["${assetPipelineCompileDir}/"] : ['']
        }
        if (sourceFiles == null) {
            sourceFiles = usesAssetPipeline ? ['app.js' , 'application.js'] : sourceFilesDefault
        }

        if (testBases == null) {
            testBases = usesAssetPipeline ? ["${assetPath}/"] + testBasesDefault : testBasesDefault
        }
        if (testFiles == null) {
            testFiles = testFilesDefault
        }

    }

    List<String> getFiles() {
        def files = []
        files += getFileListByType(ProfileFileType.LIBRARIES)
        files += getFileListByType(ProfileFileType.SOURCE)
        files += getFileListByType(ProfileFileType.TESTS)
        files
    }

    List<String> getFileListByType(ProfileFileType type) {
        List<String> bases
        List<String> files

        switch (type) {
            case ProfileFileType.LIBRARIES:
                bases = libraryBases
                files = libraryFiles
                break

            case ProfileFileType.SOURCE:
                bases = sourceBases
                files = sourceFiles
                break

            case ProfileFileType.TESTS:
                bases = testBases
                files = testFiles

                break
        }

        getFileList(bases, files)
    }

    static List<String> getFileList(List<String> bases, List<String> files) {
        files?.collect { String file ->
            bases ? bases.collect { String base -> "${base}${file}" } : file
        }?.flatten() ?: []
    }

}
