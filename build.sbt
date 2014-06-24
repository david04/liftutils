name := "liftutils"

version := "0.1.14"

organization := "com.github.david04"

scalaVersion := "2.10.4"

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

crossScalaVersions := Seq("2.10.4", "2.11.1")

libraryDependencies ++= {
  val liftVersion = "2.6-SNAPSHOT"
  Seq(
    "net.liftweb"                     %% "lift-webkit"                  % liftVersion        % "compile",
    "net.liftweb"                     %% "lift-mapper"                  % liftVersion        % "compile",
    "net.liftweb"                     %% "lift-util"                    % liftVersion        % "compile",
    "net.liftmodules"                 % "lift-jquery-module_2.5_2.10"   % "2.4",
    "com.googlecode.json-simple"      % "json-simple"                   % "1.1.1",
    "javax.servlet"                   % "javax.servlet-api"             % "3.0.1",
    "ch.qos.logback"                  % "logback-classic"               % "1.0.6",
    "log4j"                           % "log4j"                         % "1.2.17",
    "com.typesafe.akka"               %% "akka-actor"                   % "2.3.3",
    "joda-time"                       % "joda-time"                     % "2.3",
    "com.fasterxml.jackson.module"    %% "jackson-module-scala"         % "2.4.0-rc2",
    "com.fasterxml.jackson.datatype"  % "jackson-datatype-joda"         % "2.4.0-rc3",
    "commons-lang"                    % "commons-lang"                  % "2.6",
    "com.github.nscala-time"          %% "nscala-time"                  % "1.0.0",
    "org.apache.commons"              % "commons-lang3"                 % "3.3.2"
  )
}

publishMavenStyle := true

publishTo := Some(Resolver.file("file",  new File( "/home/david/Dropbox/Public/maven" )) )


