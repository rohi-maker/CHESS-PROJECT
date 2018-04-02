organization := "com.synergychess"
name         := "synergychess-server"
version      := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.5"
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

resolvers in Global += Resolver.mavenLocal

libraryDependencies += "tv.cntt" %% "nchess-server" % "1.0.0-SNAPSHOT"

// Put config directory in classpath for easier development
unmanagedClasspath in Compile += Attributed.blank(baseDirectory.value / "config")
unmanagedClasspath in Runtime += Attributed.blank(baseDirectory.value / "config")

mainClass in (Compile, run) := Some("synergychess.Boot")

// Copy these to target/xitrum when sbt xitrum-package is run
XitrumPackage.copy("config", "public", "script")
