val Http4sVersion = "0.20.9"
val CirceVersion = "0.11.1"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "com.github.damdev.increase.bookkeeper",
    name := "increase-bookkeeper",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server"  % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client"  % Http4sVersion,
      "org.http4s"      %% "http4s-circe"         % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"           % Http4sVersion,
      "io.circe"        %% "circe-generic"        % CirceVersion,
      "io.circe"        %% "circe-generic-extras" % CirceVersion,
      "org.specs2"      %% "specs2-core"          % Specs2Version % "test",
      "org.tpolecat"    %% "doobie-core"          % "0.8.6",
      "org.mariadb.jdbc"        %   "mariadb-java-client"     % "1.5.9",
      "ch.qos.logback"  %  "logback-classic"      % LogbackVersion,
      "org.tpolecat"    %% "atto-core"            % "0.7.0",
      "org.tpolecat"    %% "atto-refined"         % "0.7.0",
      "io.circe"        %% "circe-config"         % "0.6.1"
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
//  "-Xfatal-warnings",
)
