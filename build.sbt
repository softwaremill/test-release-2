import com.softwaremill.PublishTravis.publishTravisSettings
import com.typesafe.sbt.pgp.PgpKeys.{pgpPublicRing, pgpSecretRing}

val travisBuildDir = sys.env("$TRAVIS_BUILD_DIR")
lazy val commonSettings = commonSmlBuildSettings ++ publishTravisSettings ++ Seq(
  organization := "com.softwaremill.testrelease2",
  scalaVersion := "2.12.8",
  crossScalaVersions := Seq("2.12.8", "2.13.0"),
  pgpSecretRing := file(s"$travisBuildDir/secring.asc"), // unpacked from secrets.tar.enc
  pgpPublicRing := file(s"$travisBuildDir/pubring.asc"), // unpacked from secrets.tar.enc
)

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8" % "test"

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false, name := "root")
  .aggregate(core)

lazy val core: Project = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      scalaTest
    )
  )
