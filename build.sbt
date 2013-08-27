name := "liftutils"

version := "0.1.0"

organization := "com.github.david04"

scalaVersion := "2.10.0"

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Java.Net" at "http://download.java.net/maven/2/",
  "sonatype" at "https://oss.sonatype.org/content/repositories/releases/",
  "Scala-Tools" at "https://oss.sonatype.org/content/groups/scala-tools/",
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases" at "http://oss.sonatype.org/content/repositories/releases"
)

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.5-RC5"
  Seq(
    "net.liftweb"       %% "lift-webkit"        % liftVersion        % "compile",
    "net.liftweb"       %% "lift-mapper"        % liftVersion        % "compile",
    "net.liftweb"       %% "lift-util"        % liftVersion        % "compile",
    "net.liftmodules"   %% "lift-jquery-module" % "2.5-RC4-2.3",
    "com.googlecode.json-simple" % "json-simple" % "1.1.1",
    "javax.servlet" % "javax.servlet-api" % "3.0.1",
    "ch.qos.logback"    % "logback-classic"     % "1.0.6",
    "com.h2database"    % "h2"                  % "1.3.167",
    "postgresql" % "postgresql" % "9.1-901.jdbc3",
    "log4j" % "log4j" % "1.2.17"
  )
}

publishMavenStyle := true

publishTo := Some(Resolver.file("file",  new File( "/home/david/Dropbox/Public/maven" )) )
