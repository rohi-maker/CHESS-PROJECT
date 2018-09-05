organization := "net.synergychess"
name         := "synergychess-server"
version      := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.6"
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

resolvers in Global += Resolver.mavenLocal

libraryDependencies += "tv.cntt" %% "nchess-server" % "1.0.0-SNAPSHOT"
libraryDependencies += "net.synergychess" %% "synergychess-engine" % "1.0.0-SNAPSHOT"

// Put config directory in classpath for easier development
unmanagedClasspath in Compile += baseDirectory.value / "config"
unmanagedClasspath in Runtime += baseDirectory.value / "config"

mainClass in (Compile, run) := Some("synergychess.Boot")

// Copy these to target/xitrum when sbt xitrum-package is run
XitrumPackage.copy("config", "public", "script")
