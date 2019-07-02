# Test release

SBT dependency:

```scala
"com.softwaremill.testrelease2" %% "core" % "0.13"
```

# How the release process works

The release process is broken into two steps:

1. *local*: `sbt commitRelease`. This sbt command prepares the next release: runs the tests, updates `version.sbt`,
creates the git tag, commits the changes and finally asks the user to push the changes.
2. *remote*: `sbt publishRelease`. This sbt command should be run on Travis, triggered when a new tag is pushed. It
publishes the artifacts to sonatype, and invokes repository release.

See `releaseProcess` in `release.sbt` for details on how the release steps are defined in both cases.

## Configuration

To run the release, travis needs to know the keypair to sign the artifacts, as well as the username/password to
access `oss.sonatype.org`. These values all need to be prepared in file locally, then packaged, *encrypted* using
a secret that's only known to travis, and committed.

Here's how to do that:

1. create `secring.asc` and `pubring.asc` files with the PGP private/public keys
2. create a `credentials.sbt` file with the following content:

```scala
credentials += Credentials("Sonatype Nexus Repository Manager",
                           "oss.sonatype.org",
                           "SONATYPE_USERNAME",
                           "SONATYPE_PASSWORD")

pgpPassphrase := Some("KEY_PASSWORD").map(_.toArray)
```

3. create an archive: `tar cvf secrets.tar secring.asc pubring.asc credentials.sbt`
4. login to travis: `travis login` (you'll need the travis CLI to do that)
5. encrypt the archive: `travis encrypt-file secrets.tar --add`

This should have two effects:

1. an encrypted file should be created in the top-level directory: `secrets.tar.enc`. This file *should be* committed.
Take care not to commit `secrets.tar`, though :).
2. a `before_install` segment should be added to `travis.yml`. This segment decrypts the secrets file, using 
environmental variables provided by travis

In the `before_install` section, there should also be an entry unpacking the secrets file: `tar xvf secrets.tar`.
