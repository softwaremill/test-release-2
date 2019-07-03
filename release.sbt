useGpg := false, // use the gpg implementation from the sbt-pgp plugin
pgpSecretRing := baseDirectory.value / "secring.asc", // unpacked from secrets.tar.enc
pgpPublicRing := baseDirectory.value / "pubring.asc", // unpacked from secrets.tar.enc

Release.settings