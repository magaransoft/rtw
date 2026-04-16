import sbtrelease.ReleaseStateTransformations.*

organization := "com.magaran"
pgpSigningKey := Some("E20C31E38E557DEEC82006986DC61FDE08C36DE2")

name := "rtw"

description := "A library for separation of reads and writes in pipeline-style stages"

scalaVersion := "3.7.4"

licenses := Seq("MIT" -> url("https://github.com/magaransoft/rtw/blob/main/LICENSE"))

homepage := Some(url("https://github.com/magaransoft/rtw"))

scalacOptions ++= Seq("--deprecation", "--encoding", "UTF-8", "--feature", "--unchecked", "-Wunused:all", "-Werror")

scalafixOnCompile := true

inThisBuild(
  List(
    semanticdbEnabled := true, // enable SemanticDB
  )
)

scmInfo := Some(
  ScmInfo(browseUrl = url("https://github.com/magaransoft/rtw"), connection = "scm:git@github.com:magaransoft/rtw.git")
)

developers := List(
  Developer(
    id = "NovaMage",
    name = "Ángel Felipe Blanco Guzmán",
    email = "novamage@magaran.com",
    url = url("https://github.com/NovaMage")
  )
)

releaseUseGlobalVersion := false

ThisBuild / versionScheme := Some("semver-spec")

publishMavenStyle := true

publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

publishConfiguration := publishConfiguration.value.withOverwrite(false)

Test / test := {
  (Test / test).dependsOn(Test / scalafmt).value
}

Test / publishArtifact := false

exportJars := true

Test / parallelExecution := false

libraryDependencies += "com.magaran" %% "typed-map" % "0.1.0"

libraryDependencies += "org.scalatest"     %% "scalatest"         % "3.2.19"   % Test
libraryDependencies += "org.scalatest"     %% "scalatest-funspec" % "3.2.19"   % "test"
libraryDependencies += "org.scalatestplus" %% "mockito-4-11"      % "3.2.18.0" % "test"

pomIncludeRepository := { _ => false }

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommand("publishSigned"),
      releaseStepCommand("sonaUpload"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

ThisBuild / publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}
