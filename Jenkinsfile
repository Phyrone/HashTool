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
  }
  tools {
    maven 'maven3'
    jdk 'oraclejdk8'
  }
    
}
