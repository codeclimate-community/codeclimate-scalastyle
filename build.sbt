name := "codeclimate-scalastyle"
organization in ThisBuild := "codeclimate"
version in ThisBuild := "0.1.0"
scalaVersion in ThisBuild := "2.12.4"

concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)
parallelExecution in Global := false

lazy val `engine-core` = project settings (
  libraryDependencies ++= Seq(
    "org.scalastyle" %% "scalastyle" % "1.0.0",
    "io.circe" %% "circe-parser" % "0.8.0",
    "io.circe" %% "circe-generic" % "0.8.0",
    "org.scalactic" %% "scalactic" % "3.0.4",
    "org.scalatest" %% "scalatest" % "3.0.4" % "test"
  )
)

lazy val `codeclimate-scalastyle` = project in file(".") dependsOn `engine-core`

resolvers in ThisBuild ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)

enablePlugins(sbtdocker.DockerPlugin)

imageNames in docker := Seq(
  // Sets the latest tag
  ImageName(s"codeclimate/${name.value}:latest"),

  // Sets a name with a tag that contains the project version
  ImageName(
    namespace = Some("codeclimate"),
    repository = name.value,
    tag = Some("v" + version.value)
  )
)

dockerfile in docker := {
  val dockerFiles = {
    val resources = (unmanagedResources in Runtime).value
    val dockerFilesDir = resources.find(_.getPath.endsWith("/docker")).get
    resources.filter(_.getPath.contains("/docker/")).map { r =>
      (dockerFilesDir.toURI.relativize(r.toURI).getPath, r)
    }.toMap
  }

  new Dockerfile {
    from("openjdk:alpine")

    // add all dependencies to docker image instead of assembly (layers the dependencies instead of huge assembly)
    val dependencies = {
      ((dependencyClasspath in Runtime) in `engine-core`).value
    }.map(_.data).toSet + ((packageBin in Compile) in `engine-core`).value

    add(dependencies.toSeq, "/usr/src/app/dependencies/")
    add(((packageBin in Compile) in `engine-core`).value, "/usr/src/app/engine-core.jar")
    add(dockerFiles("scalastyle_config.xml"), "/usr/src/app/")
    add(dockerFiles("engine.json"), "/usr/src/app/")
    add(dockerFiles("bin/scalastyle"), "/usr/src/app/bin/")

    runRaw("apk update && apk upgrade")

    runRaw("addgroup -g 9000 -S code && adduser -S -G code app")

    user("app")

    workDir("/code")
    volume("/code")

    cmd("/usr/src/app/bin/scalastyle")
  }
}