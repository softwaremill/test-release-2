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