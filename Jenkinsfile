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
}