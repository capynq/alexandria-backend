pipeline {
    agent any
    tools { jdk "java17" }
    parameters {
        string(name: "SONAR_URL", defaultValue: "", description: "Optional SonarQube server URL")
        string(name: "SONAR_CREDENTIAL_ID", defaultValue: "", description: "Optional Jenkins secret text credential ID")
    }
    stages {
        stage("Clean") {
            steps { sh "./gradlew clean" }
        }
        stage("Build") {
            steps { sh "./gradlew assemble" }
        }
        stage("Unit tests") {
            steps { sh "./gradlew unitTest" }
        }
        stage("Integration tests") {
            steps { sh "./gradlew integrationTest" }
        }
        stage("Quality and coverage") {
            steps { sh "./gradlew check jacocoTestReport" }
        }
        stage("SonarQube") {
            when { expression { return params.SONAR_URL?.trim() && params.SONAR_CREDENTIAL_ID?.trim() } }
            steps {
                withCredentials([string(credentialsId: params.SONAR_CREDENTIAL_ID, variable: "SONAR_TOKEN")]) {
                    withEnv(["SONAR_HOST_URL=${params.SONAR_URL}"]) {
                        sh "./gradlew sonar"
                    }
                }
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: "build/test-results/**/*.xml"
            archiveArtifacts allowEmptyArchive: true, artifacts: "build/reports/**,build/libs/*.jar"
        }
    }
}
