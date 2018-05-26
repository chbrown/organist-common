# [org.anist](http://anist.org).common

Personal library of useful things.


## Publishing and consuming

Publish locally (to the local Ivy cache, `~/.ivy2/local/org.anist/...`):

    sbt +publish-local

Publish to [Bintray](https://bintray.com/):

    sbt +publish

Consume from [Bintray](https://bintray.com/) in your `build.sbt`:

    resolvers += Resolver.bintrayRepo("chbrown", "maven")
    libraryDependencies += "org.anist" %% "common" % "0.1.0"


## License

Copyright © 2016–2018 Christopher Brown.
[MIT Licensed](https://chbrown.github.io/licenses/MIT/#2016-2018)
