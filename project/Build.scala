import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "TaskScheduler"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "log4j" % "log4j" % "1.2.17",
    "mysql" % "mysql-connector-java" % "5.1.18",
    "org.quartz-scheduler" % "quartz" % "2.1.7"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
