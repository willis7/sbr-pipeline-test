#!/bin/groovy
@Library('jenkins-pipeline-shared') _

onsPipeline {
  pipelineType = "scala"
  runTests = true
  testCommand = "sbt test"
  deployUponTestSuccess = true
  deploymentEnvironment = "test"
}
