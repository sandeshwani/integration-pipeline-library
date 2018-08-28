#!/usr/bin/groovy

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def gitUrl = config.gitUrl
    def userOwner = config.owner

    def userCheckAllDependencies = config.get('checkAllDependencies', false)

    def userRunRelease = config.get('runRelease', Boolean.valueOf("${RUN_RELEASE}"))
    println "Going to run the Release ${userRunRelease}"

    integrationNode {
        setupStage {
            setJdk {}
        }
        gitStage {
            url = gitUrl
        }
        preReleaseStage {
            runRelease = userRunRelease
            checkAllDependencies = userCheckAllDependencies
        }
        gradleStage {
            buildCommand = 'clean'
        }
        mavenStage {
            buildCommand = 'clean'
        }

        newGarStage('Gradle GAR') {
            buildTool = 'gradle'
            owner = userOwner

        }
        newGarStage('Maven GAR') {
            buildTool = 'maven'
            owner = userOwner
        }
        postReleaseStage {
            runRelease = userRunRelease
        }

    }
}
