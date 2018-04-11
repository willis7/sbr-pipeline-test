#!/bin/groovy
@Library('jenkins-pipeline-shared') _
import uk.gov.ons.*

onsPipeline {
  pipelineType = "scala"
  runTests = true
  testCommand = "sbt test"
  deployUponTestSuccess = true
  deploymentEnvironment = "test"
}
