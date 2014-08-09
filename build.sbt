name := "LetsChai"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  // javaJdbc,
  // javaEbean,
  cache,
  "com.restfb" % "restfb" % "1.6.14",
  "uk.co.panaxiom" %% "play-jongo" % "0.6.0-jongo1.0",
  "com.google.guava" % "guava" % "17.0",
  "joda-time" % "joda-time" % "2.3",
  "com.google.code.geocoder-java" % "geocoder-java" % "0.16",
  "org.igniterealtime.smack" % "smack-core" % "4.0.1",
  "org.igniterealtime.smack" % "smack-tcp" % "4.0.1",
  "com.amazonaws" % "aws-java-sdk" % "1.8.3",
  "org.apache.commons" % "commons-email" % "1.3.3"
)

play.Project.playJavaSettings

