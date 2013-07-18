name := "snappy-utils"

version := "0.1.0"

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-core" % "1.1.2",
  "commons-logging" % "commons-logging" % "1.1.1"
)

resolvers ++= Seq(
  "MVN Repository" at "http://mvnrepository.com"
)
