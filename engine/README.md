## Build for JVM

```sh
sbt "project engineJVM" publish-local
```

## Build for JS

```sh
sbt fullOptJS
```

Link the library for other projects to use:

```sh
cd js/synergychess-engine
npm link
```

At other projects:

```sh
npm link synergychess-engine
```

## Unit tests

To run unit tests:

```sh
sbt test
```

Note that you need to add to [~/.sbt/0.13/global.sbt](https://stackoverflow.com/questions/47456328/unresolved-dependency-com-artima-supersafesupersafe-2-12-41-1-3-not-found):

```txt
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
```
