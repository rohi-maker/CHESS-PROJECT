## Build for JVM

```
sbt

project engineJVM

publish-local
```

## Build for JS

```
sbt fullOptJS
```

## Unit tests

To run unit tests:

```
sbt test
```

Note that you need to add to [~/.sbt/0.13/global.sbt](https://stackoverflow.com/questions/47456328/unresolved-dependency-com-artima-supersafesupersafe-2-12-41-1-3-not-found):

```
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
```
