# Synergy Chess Engine

## Build for JVM

```sh
sbt "project engineJVM" publishLocal
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
