import com.typesafe.sbt.pgp.PgpKeys.{pgpPublicRing, pgpSecretRing}

commands += Command.command("commitRelease") { state =>
  "set Release.isCommitRelease := true" ::
    "release" ::
    state
}
commands += Command.command("publishRelease") { state =>
  "set Release.isCommitRelease := false" ::
    "release" ::
    state
}

pgpSecretRing := baseDirectory.value / "secring.asc", // unpacked from secrets.tar.enc
pgpPublicRing := baseDirectory.value / "pubring.asc", // unpacked from secrets.tar.enc

Release.settings