package com.craigburke.gradle

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.task.NodeTask
import com.moowork.gradle.node.task.NpmTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete

class KarmaPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.plugins.apply NodePlugin
        NodeExtension nodeConfig = project.extensions.findByName('node')
        nodeConfig.download = true

        final String NPM_OUTPUT_PATH = project.file(nodeConfig.nodeModulesDir).absolutePath.replace(File.separator, '/') + '/node_modules/'
        final File KARMA_EXEC = project.file(NPM_OUTPUT_PATH + '/karma/bin/karma')
        final File KARMA_CONFIG = project.file("${project.buildDir.absolutePath}/karma.conf.js")

        KarmaModuleExtension config = project.extensions.create('karma', KarmaModuleExtension)
        boolean karmaDebug = project.hasProperty('karmaDebug') ? project.property('karmaDebug') : false
        
        project.task('karmaDependencies', type: NpmTask, description: 'Installs dependencies needed for running karma tests.', group: null)

        project.task('karmaGenerateConfig', description: 'Generates the karma config file', group: null) {
            outputs.file KARMA_CONFIG
            doLast {
                KARMA_CONFIG.parentFile.mkdirs()
                KARMA_CONFIG.text = config.configJavaScript
            }
            shouldRunAfter 'karmaClean'
        }

        project.task('karmaRefresh', group: 'Karma', dependsOn: ['karmaClean', 'karmaGenerateConfig'],
                description: 'Refreshes the generated karma config file')

        project.task('karmaRun', type: NodeTask, dependsOn: ['karmaDependencies', 'karmaGenerateConfig'], group: 'Karma',
                 description: 'Executes karma tests') {
            script = KARMA_EXEC
            args = ['start', KARMA_CONFIG.absolutePath, '--single-run']
        }

        project.task('karmaWatch', type: NodeTask, dependsOn: ['karmaDependencies', 'karmaGenerateConfig'], group: 'Karma',
                description: 'Executes karma tests in watch mode') {
            script = KARMA_EXEC
            args = ['start', KARMA_CONFIG.absolutePath, '--auto-watch']
        }

        project.task('karmaClean', type: Delete, group: 'Karma',
                description: 'Deletes the generated karma config file and removes the dependencies') {
            delete KARMA_CONFIG
            delete getDependencyPaths(NPM_OUTPUT_PATH, config.dependencies)
        }

        project.afterEvaluate {
            project.tasks.findByName('karmaDependencies').configure {
                args = ['install'] + config.dependencies
                outputs.files getDependencyPaths(NPM_OUTPUT_PATH, config.dependencies)
                execOverrides {
                    OutputStream out = new ByteArrayOutputStream()
                    if (!karmaDebug) {
                        it.standardOutput = out
                        it.errorOutput = out
                    }
                }
            }

            if (config.basePath == null) {
                config.basePath = project.rootDir.absolutePath
            }

            def assetConfig = project.extensions.findByName('assets')
            if (assetConfig) {
                config.finalizeConfig(true, (String)assetConfig.assetsPath, (String)assetConfig.compileDir)
                ['karmaRun', 'karmaWatch'].each {
                    project.tasks.findByName(it).dependsOn 'assetCompile'
                }
            }
            else {
                config.finalizeConfig(false)
            }

            def testTask = project.tasks.findByName('test')
            if (testTask) {
                testTask.dependsOn 'karmaRun'
            }
        }
    }
    
    String[] getDependencyPaths(String npmPath, dependencies) {
        dependencies.collect { "${npmPath}/${it.split('@')[0]}" }
    }

}