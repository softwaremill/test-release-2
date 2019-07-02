import sbt._, Keys._
import com.softwaremill.Publish.Release._

import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._

import com.typesafe.sbt.pgp.PgpKeys._

object Release {
  // release entry points

  val commitRelease = taskKey[Unit]("Update version.sbt, change docs, create git tag, commit and push changes")
  val publishRelease = taskKey[Unit]("Publish the current release (basing on version.sbt) to sonatype")

  //

  val isCommitRelease = settingKey[Boolean]("A hacky way to differentiate between commitRelease and publishRelease invocations.")

  val settings = Seq(
    isCommitRelease := true,
    useGpg := false, // use the gpg implementation from the sbt-pgp plugin
    pgpSecretRing := baseDirectory.value / "secring.asc", // unpacked from secrets.tar.enc
    pgpPublicRing := baseDirectory.value / "pubring.asc", // unpacked from secrets.tar.enc
    commands += Command.command("commitRelease") { state =>
      "set Release.isCommitRelease := true" ::
        "release" ::
        state
    },
    commands += Command.command("publishRelease") { state =>
      "set Release.isCommitRelease := false" ::
        "release" ::
        state
    },
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
    }
  )
}


