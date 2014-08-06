name := "LetsChai"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  // javaJdbc,
  // javaEbean,
  cache,
  "com.restfb" % "restfb" % "1.6.14",
//  "de.undercouch" % "bson4jackson" % "2.1.0" force(),
//  "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.0" force(),
//  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.1.0" force(),
//  "com.fasterxml.jackson.core" % "jackson-core" % "2.1.0" force(),
//  "org.mongodb" % "mongo-java-driver" % "2.11.3",
//  "org.jongo" % "jongo" % "1.0",
  "uk.co.panaxiom" %% "play-jongo" % "0.6.0-jongo1.0",
  "com.google.guava" % "guava" % "17.0",
  "joda-time" % "joda-time" % "2.3",
  "com.google.code.geocoder-java" % "geocoder-java" % "0.16",
  "org.igniterealtime.smack" % "smack-core" % "4.0.1",
  "org.igniterealtime.smack" % "smack-tcp" % "4.0.1",
  "com.amazonaws" % "aws-java-sdk" % "1.8.3"
)

play.Project.playJavaSettings

