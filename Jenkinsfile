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
        archiveArtifacts(artifacts: '**/target/*.jar', onlyIfSuccessful: true, excludes: '**/target/orginal.*.jar')
      }
    }
  }
  tools {
    maven 'maven3'
    jdk 'oraclejdk8'
  }
}