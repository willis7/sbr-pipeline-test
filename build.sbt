import play.sbt.PlayScala
import sbtbuildinfo.BuildInfoPlugin.autoImport._
import sbtassembly.AssemblyPlugin.autoImport._


lazy val publishTrigger = settingKey[Boolean]("publishTrigger")
lazy val publishRepo = settingKey[String]("publishRepo")
lazy val artHost = settingKey[String]("artHost")
lazy val artUser = settingKey[String]("artUser")
lazy val artPassword = settingKey[String]("artPassword")

publishTrigger := sys.props.get("publish.trigger") exists (_ equalsIgnoreCase "true")
publishRepo := sys.props.getOrElse("publish.repo", default = "https://Unused/transient/repository")
artHost := sys.props.getOrElse("art.host", default = "Unknown-Artifactory-host")
artUser := sys.props.getOrElse("art.user", default = "Unknown username")
artPassword := sys.props.getOrElse("art.password", default = "Unknown password")

// key-bindings
lazy val ITest = config("it") extend Test


lazy val Versions = new {
  val scala = "2.11.11"
  val scapegoatVersion = "1.1.0"
  val util = "0.27.8"
  val hbaseVersion = "1.3.1"
  val hadoopVersion = "2.5.1"
  val sparkVersion = "2.2.0"
}

lazy val Constant = new {
  val local = "mac"
  val projectStage = "alpha"
  val team = "sbr"
  val apacheHBase = "org.apache.hbase"
  val apacheHadoop = "org.apache.hadoop"
  val apacheSpark = "org.apache.spark"
}

lazy val Resolvers = Seq[Resolver](
  "cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
  Resolver.typesafeRepo("releases")
)


lazy val testSettings = Seq(
  sourceDirectory in ITest := baseDirectory.value / "/test/it",
  resourceDirectory in ITest := baseDirectory.value / "/test/resources",
  scalaSource in ITest := baseDirectory.value / "test/it",
  // test setup
  parallelExecution in Test := false
)

lazy val publishingSettings = Seq(
  publishArtifact := publishTrigger.value,
  publishMavenStyle := false,
  checksums in publish := Nil,
  publishArtifact in Test := false,
  publishArtifact in Compile := false,
  publishTo := {
    if (System.getProperty("os.name").toLowerCase.startsWith(Constant.local) )
      Some(Resolver.file("file", new File(s"${System.getProperty("user.home").toLowerCase}/Desktop/")))
    else
      Some("Artifactory Realm" at publishRepo.value)
  },
  artifact in (Compile, assembly) ~= { art =>
    art.copy(`type` = "jar", `classifier` = Some("assembly"))
  },
  credentials += Credentials("Artifactory Realm", artHost.value, artUser.value, artPassword.value),
  releaseTagComment := s"Releasing $name ${(version in ThisBuild).value}",
  releaseCommitMessage := s"Setting Release tag to ${(version in ThisBuild).value}",
  // no commit - ignore zip and other package files
  releaseIgnoreUntrackedFiles := true
)

lazy val commonSettings = Seq (
  scalaVersion := Versions.scala,
  scalacOptions in ThisBuild ++= Seq(
    "-language:experimental.macros",
    "-target:jvm-1.8",
    "-encoding", "UTF-8",
    "-language:reflectiveCalls",
    "-language:experimental.macros",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:postfixOps",
    "-deprecation", // warning and location for usages of deprecated APIs
    "-feature", // warning and location for usages of features that should be imported explicitly
    "-unchecked", // additional warnings where generated code depends on assumptions
    "-Xlint", // recommended additional warnings
    "-Xcheckinit", // runtime error when a val is not initialized due to trait hierarchies (instead of NPE somewhere else)
    "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
    //"-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver
    "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures
    "-Ywarn-dead-code", // Warn when dead code is identified
    "-Ywarn-unused", // Warn when local and private vals, vars, defs, and types are unused
    "-Ywarn-unused-import", //  Warn when imports are unused (don't want IntelliJ to do it automatically)
    "-Ywarn-numeric-widen" // Warn when numerics are widened
  ),
  resolvers ++= Resolvers ++ Seq("Artifactory" at s"${publishRepo.value}"),
  coverageExcludedPackages := ".*Routes.*;.*ReverseRoutes.*;.*javascript.*"
)

lazy val hadoopDeps: Seq[ModuleID] = Seq(
  // HBase
  Constant.apacheHBase   % "hbase-common"                      % Versions.hbaseVersion,
  Constant.apacheHBase   % "hbase-common"                      % Versions.hbaseVersion   classifier "tests",
  Constant.apacheHBase   % "hbase-client"                      % Versions.hbaseVersion   exclude ("org.slf4j", "slf4j-api"),
  Constant.apacheHBase   % "hbase-hadoop-compat"               % Versions.hbaseVersion,
  Constant.apacheHBase   % "hbase-hadoop-compat"               % Versions.hbaseVersion   classifier "tests",
  Constant.apacheHBase   % "hbase-hadoop2-compat"              % Versions.hbaseVersion,
  Constant.apacheHBase   % "hbase-hadoop2-compat"              % Versions.hbaseVersion   classifier "tests",
  Constant.apacheHBase   % "hbase-server"                      % Versions.hbaseVersion,
  Constant.apacheHBase   % "hbase-server"                      % Versions.hbaseVersion   classifier "tests",

  // Hadoop
  Constant.apacheHadoop  % "hadoop-common"                     % Versions.hadoopVersion,
  Constant.apacheHadoop  % "hadoop-common"                     % Versions.hadoopVersion  classifier "tests",
  Constant.apacheHadoop  % "hadoop-hdfs"                       % Versions.hadoopVersion,
  Constant.apacheHadoop  % "hadoop-hdfs"                       % Versions.hadoopVersion  classifier "tests",
  Constant.apacheHadoop  % "hadoop-mapreduce-client-core"      % Versions.hadoopVersion,
  Constant.apacheHadoop  % "hadoop-mapreduce-client-jobclient" % Versions.hadoopVersion

).map(_.excludeAll ( ExclusionRule("log4j", "log4j"), ExclusionRule ("org.slf4j", "slf4j-log4j12")))


lazy val hbaseSpark: Seq[ModuleID] = Seq(
  // Spark
  Constant.apacheSpark   %% "spark-core"                       % Versions.sparkVersion,
  Constant.apacheSpark   %% "spark-sql"                        % Versions.sparkVersion,
  Constant.apacheSpark   %% "spark-mllib"                      % Versions.sparkVersion,
  Constant.apacheSpark   %% "spark-streaming"                  % Versions.sparkVersion,
  Constant.apacheSpark   %% "spark-hive"                       % Versions.sparkVersion,

  Constant.apacheHBase   %  "hbase-spark"                      % "1.2.0-cdh5.10.1"
)

lazy val patchDeps: Seq[ModuleID] = Seq(
  // fix java.lang.ClassNotFoundException: de.unkrig.jdisasm.Disassembler -> caused by Janino compiler
  "org.codehaus.janino"  %  "janino"                           % "3.0.7"
)


lazy val api = (project in file("."))
  .enablePlugins(BuildInfoPlugin, GitVersioning, GitBranchPrompt, PlayScala)
  .configs(ITest)
  .settings(inConfig(ITest)(Defaults.testSettings) : _*)
  .settings(commonSettings: _*)
  .settings(testSettings:_*)
  .settings(publishingSettings:_*)
  // add the assembly jar to current publish arts
  .settings(addArtifact(artifact in (Compile, assembly), assembly).settings: _*)
  .settings(
    developers := List(Developer("Adrian Harris (Tech Lead)", "SBR", "ons-sbr-team@ons.gov.uk", new java.net.URL(s"https://${artHost.value}/v1/home"))),
    moduleName := "sbr-pipeline",
    organizationName := "ons",
    description := "<description>",
    version := (version in ThisBuild).value,
    name := s"${organizationName.value}_${moduleName.value}",
    licenses := Seq("MIT-License" -> url("https://github.com/ONSdigital/sbr-control-api/blob/master/LICENSE")),
    startYear := Some(2017),
//    entryApiURL :=,
//    homepage := Some(new java.net.URL(s"https://${artHost.value}/v1/home"))),
    buildInfoPackage := "controllers",
    // gives us last compile time and tagging info
    buildInfoKeys := Seq[BuildInfoKey](
      organizationName,
      moduleName,
      name,
      description,
      developers,
      version,
      scalaVersion,
      sbtVersion,
      startYear,
//      entryApiURL,
//      homepage,
      BuildInfoKey.action("gitVersion") {
        git.formattedShaVersion.?.value.getOrElse(Some("Unknown")).getOrElse("Unknown") +"@"+ git.formattedDateVersion.?.value.getOrElse("")
      },
      BuildInfoKey.action("codeLicenses"){ licenses.value },
      BuildInfoKey.action("projectTeam"){ Constant.team },
      BuildInfoKey.action("projectStage"){ Constant.projectStage },
      BuildInfoKey.action("repositoryAddress"){ Some(scmInfo.value.get.browseUrl).getOrElse("REPO_ADDRESS_NOT_FOUND")}
    ),
    // di router -> swagger
    routesGenerator := InjectedRoutesGenerator,
    buildInfoOptions += BuildInfoOption.ToMap,
    buildInfoOptions += BuildInfoOption.ToJson,
    buildInfoOptions += BuildInfoOption.BuildTime,
    libraryDependencies ++= Seq (
      filters,
      "org.webjars"                  %%    "webjars-play"        %    "2.5.0-3",
      "com.typesafe.scala-logging"   %%    "scala-logging"       %    "3.5.0",
      "com.outworkers"               %%    "util-parsers-cats"   %    Versions.util,
      "com.outworkers"               %%    "util-play"           %    Versions.util,
      "org.scalatestplus.play"       %%    "scalatestplus-play"  %    "2.0.0"           % Test,
      "io.swagger"                   %%    "swagger-play2"       %    "1.5.3",
      "org.webjars"                  %     "swagger-ui"          %    "2.2.10-1",
      "com.typesafe"                 %      "config"             %    "1.3.1"
        excludeAll ExclusionRule("commons-logging", "commons-logging")
    ) ++ hbaseSpark ++ hadoopDeps ++ patchDeps,
    // assembly
    assemblyJarName in assembly := s"${organizationName}_$moduleName-assembly-${(version in ThisBuild).value}.jar",
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs @ _*)                         => MergeStrategy.last
      case PathList("org", "apache", xs @ _*)                            => MergeStrategy.last
      case PathList("org", "slf4j", xs @ _*)                             => MergeStrategy.first
      case PathList("META-INF", "io.netty.versions.properties", xs @ _*) => MergeStrategy.last
      case PathList("org", "slf4j", xs @ _*)                             => MergeStrategy.first
      case "application.conf"                                            => MergeStrategy.first
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    mainClass in assembly := Some("play.core.server.ProdServerStart"),
    fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)
  )
