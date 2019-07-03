useGpg := false // use the gpg implementation from the sbt-pgp plugin
pgpSecretRing := baseDirectory.value / "secring.asc" // unpacked from secrets.tar.enc
pgpPublicRing := baseDirectory.value / "pubring.asc" // unpacked from secrets.tar.enc

Release.isCommitRelease := true
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

Release.settings