# [org.anist](http://anist.org).lib

Personal library of useful things.


## Publishing and consuming

Publish locally (to the local Ivy cache, `~/.ivy2/local/org.anist/...`):

    sbt +publishLocal

Publish to [Bintray](https://bintray.com/):

    sbt +publish

Consume from [Bintray](https://bintray.com/) in your `build.sbt`:

    resolvers += Resolver.bintrayRepo("chbrown", "maven")
    libraryDependencies ++= Seq("common", "io", "stats", "text").map("org.anist" %% _ % "0.2.0")


## License

Copyright © 2016–2018 Christopher Brown.
[MIT Licensed](https://chbrown.github.io/licenses/MIT/#2016-2018)
