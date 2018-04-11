#!/bin/groovy
@Library('jenkins-pipeline-shared') _

scalaPipeline {
  runTests = true
  testCommand = "sbt test"
  deployUponTestSuccess = true
  deploymentEnvironment = "test"
}
