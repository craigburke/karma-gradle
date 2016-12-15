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

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.task.NodeTask
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class KarmaPlugin implements Plugin<Project> {

    static final String NPM_OUTPUT_PATH = 'node_modules'
    static final String DEFAULT_NODE_VERSION = '4.2.3'

    static final String INIT_TASK = 'karmaInit'
    static final String REFRESH_TASK = 'karmaRefresh'
    static final String CLEAN_TASK = 'karmaClean'
    static final String RUN_TASK = 'karmaRun'
    static final String WATCH_TASK = 'karmaWatch'
    static final String GENERATE_CONFIG_TASK = 'karmaGenerateConfig'
    static final String DEPENDENCIES_TASK = 'karmaDependencies'
    static final String GROUP_NAME = 'Karma'

    void apply(Project project) {
        setupNode(project)

        final File KARMA_EXEC = project.file("${NPM_OUTPUT_PATH}/karma/bin/karma")
        final File KARMA_CONFIG = project.file("${project.buildDir.absolutePath}/karma.conf.js")

        KarmaModuleExtension config = project.extensions.create('karma', KarmaModuleExtension)
        boolean karmaDebug = project.hasProperty('karmaDebug') ? project.property('karmaDebug') : false

        def nodeExecOverrides = {
            if (!karmaDebug) {
                it.standardOutput = new ByteArrayOutputStream()
            }
        }

        project.task(INIT_TASK, group: null,
                description: 'Sets up folder structure needed for the karma plugin') {
            project.file(NPM_OUTPUT_PATH).mkdirs()
        }

        project.task(DEPENDENCIES_TASK, type: NpmTask, dependsOn: INIT_TASK,
                description: 'Installs dependencies needed for running karma tests.', group: null) {
            args = ['install']
            if (!karmaDebug) {
                args += ['--silent']
            }
            outputs.dir project.file(NPM_OUTPUT_PATH)
            execOverrides nodeExecOverrides
        }

        project.task(GENERATE_CONFIG_TASK, description: 'Generates the karma config file', group: null) {
            outputs.file KARMA_CONFIG
            doLast {
                KARMA_CONFIG.parentFile.mkdirs()
                KARMA_CONFIG.text = config.configJavaScript
            }
            shouldRunAfter CLEAN_TASK
        }

        project.task(REFRESH_TASK, group: GROUP_NAME, dependsOn: [CLEAN_TASK, GENERATE_CONFIG_TASK],
                description: 'Refreshes the generated karma config file')

        project.task(RUN_TASK, type: NodeTask, dependsOn: [DEPENDENCIES_TASK, GENERATE_CONFIG_TASK], group: 'Karma',
                 description: 'Executes karma tests') {
            script = KARMA_EXEC
            args = ['start', KARMA_CONFIG.absolutePath, '--single-run']
        }

        project.task(WATCH_TASK, type: NodeTask, dependsOn: [DEPENDENCIES_TASK, GENERATE_CONFIG_TASK], group: 'Karma',
                description: 'Executes karma tests in watch mode') {
            script = KARMA_EXEC
            args = ['start', KARMA_CONFIG.absolutePath, '--auto-watch']
        }

        project.task(CLEAN_TASK, group: GROUP_NAME,
                description: 'Deletes the generated karma config file and removes the dependencies') {
            doLast {
                KARMA_CONFIG.delete()
                project.file(NPM_OUTPUT_PATH).listFiles().each { File file ->
                    if (file.isDirectory() && it.name.startsWith('karma')) {
                        file.deleteDir()
                    }
                }
            }
        }

        project.afterEvaluate {
            setKarmaDependencies(project, config)
            setDefaultBasePath(project, config)
            setColor(project, config)
            setTaskDependencies(project)
            finalizeConfig(project, config)
        }
    }

    private static void setKarmaDependencies(Project project, KarmaModuleExtension config) {
        def karmaDependencies = project.tasks.findByName(DEPENDENCIES_TASK)
        karmaDependencies.configure {
            args += config.dependencies
        }
    }

    private static void setupNode(Project project) {
        project.plugins.apply NodePlugin
        NodeExtension nodeConfig = project.extensions.findByName('node') as NodeExtension
        nodeConfig.download = true
        nodeConfig.version = DEFAULT_NODE_VERSION
    }

    private static void setDefaultBasePath(Project project, KarmaModuleExtension config) {
        if (config.basePath == null) {
            config.basePath = project.rootDir.absolutePath
        }
    }

    private static void setColor(Project project, KarmaModuleExtension config) {
        if (config.colors) {
            [RUN_TASK, WATCH_TASK].each { String taskName ->
                def task = project.tasks.findByName(taskName)
                task?.configure {
                    args += '--color'
                }
            }
        }
    }

    private static void setTaskDependencies(Project project) {
        def testTask = project.tasks.findByName('test')
        if (testTask) {
            testTask.dependsOn RUN_TASK
        }
    }

    private static void finalizeConfig(Project project, KarmaModuleExtension config) {
        def assetConfig = project.extensions.findByName('assets')
        if (assetConfig) {
            config.finalizeConfig(true, (String)assetConfig.assetsPath)
        }
        else {
            config.finalizeConfig(false)
        }
    }

}
