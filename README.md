# [org.anist](http://anist.org).common

Personal library of useful things.

Run `sbt +publish-local` in this directory to "publish" the `org.anist.common` package to the local Ivy/Maven cache, specifically, `~/.ivy2/local/org.anist/common_2.11/0.1.0/...`

This repository is haphazardly published to [Bintray](https://bintray.com/). To publish:

    sbt +publish

To install:

    # Add this to your `build.sbt`:
    resolvers += Resolver.bintrayRepo("chbrown", "maven")
    libraryDependencies += "org.anist" %% "common" % "0.1.0"

## License

Copyright 2016 Christopher Brown. [MIT Licensed](http://chbrown.github.io/licenses/MIT/#2016)
