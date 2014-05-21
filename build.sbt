name := "LetsChai"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "com.restfb" % "restfb" % "1.6.14",
  "de.undercouch" % "bson4jackson" % "2.1.0" force(),
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.0" force(),
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.1.0" force(),
  "com.fasterxml.jackson.core" % "jackson-core" % "2.1.0" force(),
  "org.mongodb" % "mongo-java-driver" % "2.11.3",
  "org.jongo" % "jongo" % "1.0",
  "uk.co.panaxiom" %% "play-jongo" % "0.6.0-jongo1.0",
  "com.google.guava" % "guava" % "17.0",
  "leodagdag" %% "play2-morphia-plugin" % "0.0.17",
  "joda-time" % "joda-time" % "2.3"
)

// resolver for play2-morphia plugin
resolvers += "Mike Nikles repository" at "http://mikenikles.github.io/repository/"

play.Project.playJavaSettings

