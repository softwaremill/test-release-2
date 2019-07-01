import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import com.softwaremill.Publish.Release._

//

val commitRelease = taskKey[Unit]("Update version.sbt, change docs, create git tag, commit and push changes")
val publishRelease = taskKey[Unit]("Publish the current release (basing on version.sbt) to sonatype")

val isCommitRelease = settingKey[Boolean]("A hacky way to differentiate between commitRelease and publishRelease invocations.")

commands += Command.command("commitRelease") { state =>
  "set isCommitRelease := true" ::
    "release" ::
    state
}

commands += Command.command("publishRelease") { state =>
  "set isCommitRelease := false" ::
    "release" ::
    state
}

//

lazy val commonSettings = commonSmlBuildSettings ++ ossPublishSettings ++ Seq(
  organization := "com.softwaremill.testrelease2",
  scalaVersion := "2.12.8",
  isCommitRelease := true
)

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false, name := "root")
  .settings(
    releaseProcess := {
      if (isCommitRelease.value) {
        Seq(
          checkSnapshotDependencies,
          inquireVersions,
          runClean,
          runTest,
          setReleaseVersion,
          updateVersionInDocs(organization.value),
          commitReleaseVersion,
          tagRelease,
          setNextVersion,
          commitNextVersion,
          pushChanges
        )
      } else {
        Seq(
          publishArtifacts,
          releaseStepCommand("sonatypeReleaseAll"),
        )
      }
      Release.steps(organization.value)
    }
  )
  .aggregate(core)

lazy val core: Project = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      scalaTest
    )
  )
