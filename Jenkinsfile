pipeline {
    agent any
    tools { jdk "java17" }
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
            when { expression { return env.SONAR_HOST_URL && env.SONAR_TOKEN } }
            steps { sh "./gradlew sonar" }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: "build/test-results/**/*.xml"
            archiveArtifacts allowEmptyArchive: true, artifacts: "build/reports/**,build/libs/*.jar"
        }
    }
}
