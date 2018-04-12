// https://www.scala-js.org/doc/project/cross-build.html

scalaVersion in ThisBuild := "2.12.4"

lazy val root = project.in(file(".")).
  aggregate(engineJS, engineJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val engine = crossProject.in(file(".")).
  settings(
    name := "synergychess-engine",
    version := "1.0.0-SNAPSHOT",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.5" % "test"
  ).
  jsSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided",

    // https://www.scala-js.org/doc/project/module.html
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
  )

lazy val engineJVM = engine.jvm
lazy val engineJS = engine.js
