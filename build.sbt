name := "liftutils"

version := "0.1.13"

organization := "com.github.david04"

scalaVersion := "2.11.1"

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Java.Net" at "http://download.java.net/maven/2/",
  "sonatype" at "https://oss.sonatype.org/content/repositories/releases/",
  "Scala-Tools" at "https://oss.sonatype.org/content/groups/scala-tools/",
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases" at "http://oss.sonatype.org/content/repositories/releases",
  "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.6-SNAPSHOT"
  Seq(
    "net.liftweb" %% "lift-webkit" % "2.6-SNAPSHOT" % "compile",
    "net.liftweb" %% "lift-mapper" % "2.6-SNAPSHOT" % "compile",
    "com.typesafe.akka" %% "akka-actor" % "2.3.3",
    "javax.servlet" % "javax.servlet-api" % "3.0.1",
    "javax.servlet" % "servlet-api" % "2.5" % "provided",
    "ch.qos.logback" % "logback-classic" % "1.0.6",
    "org.postgresql" % "postgresql" % "9.3-1101-jdbc41",
    "org.squeryl" %% "squeryl" % "0.9.5-7",
    "com.zaxxer" % "HikariCP" % "1.3.8" % "compile",
    "com.googlecode.usc" % "jdbcdslog" % "1.0.6.2",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.0-rc2",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.4.0-rc3",
    "com.github.nscala-time" %% "nscala-time" % "1.2.0",
    "org.apache.commons" % "commons-lang3" % "3.3.2"
  )
}

publishMavenStyle := true

publishTo := Some(Resolver.file("file",  new File( "/home/david/Dropbox/Public/maven" )) )


