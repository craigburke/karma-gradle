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

    void setDefaults(boolean usesAssetPipeline = false, String assetPath = '') {
        if (libraryBases == null) {
            libraryBases = usesAssetPipeline ? ["${assetPath}/bower/"] : []
            libraryBases += libraryBaseDefault
        }
        if (libraryFiles == null) {
            libraryFiles = libraryFilesDefault
        }

        if (sourceBases == null) {
            sourceBases = usesAssetPipeline ? ["${assetPath}/"] : sourceBasesDefault
        }
        if (sourceFiles == null) {
            sourceFiles = sourceFilesDefault
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
