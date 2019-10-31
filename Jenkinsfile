pipeline {
  agent any
  stages {
    stage('Clean') {
      steps {
        sh 'mvn clean'
      }
    }
    stage('Install') {
      steps {
        sh 'mvn install'
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts(artifacts: '/target/hashmonitor-1.0-SNAPSHOT.jar', onlyIfSuccessful: true)
      }
    }
  }
  tools {
    maven 'maven3'
    jdk 'oraclejdk8'
  }
}