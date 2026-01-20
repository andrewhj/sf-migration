ThisBuild / version      := "1.0.0"
ThisBuild / scalaVersion := "3.3.5"
ThisBuild / scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature"
)

ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTtestFramework")

// We can add this project if requiring access to the github graphql api
// lazy val githubGraphqlDomain = (project in file("domain-github-graphql"))
//   .settings(libraryDependencies ++= commonDeps)
//   .enablePlugins(CalibanPlugin)

lazy val `common-config` = (project in file("common-config"))
  .settings(
    libraryDependencies ++= commonDeps
  )

lazy val `domain-sourceforge` = (project in file("domain-sourceforge"))
  .settings(
    libraryDependencies ++= commonDeps
  )
lazy val `integration-sourceforge` = (project in file("integration-sourceforge"))
  .settings(
    libraryDependencies ++= commonDeps
  )
  .dependsOn(`common-config`, `domain-sourceforge`)

lazy val `integration-github` = (project in file("integration-github"))
  .settings(
    libraryDependencies ++= commonDeps
  )

lazy val `integration-db` = (project in file("integration-db"))
  .settings(
    libraryDependencies ++= commonDeps
  )
  .dependsOn(`common-config`, `domain-sourceforge`)

lazy val reconciliation = (project in file("reconciliation-service"))
  .settings(libraryDependencies ++= commonDeps)
  .dependsOn(`integration-github`, `integration-sourceforge`)

lazy val app = (project in file("app"))
  .settings(
    libraryDependencies ++= commonDeps
  )
  .dependsOn(
    `common-config`,
    `integration-github`,
    `integration-sourceforge`,
    `integration-db`,
    reconciliation
  )
  // .aggregate(
  //   `common-domain`,
  //   `common-config`,
  //   `domain-sourceforge`,
  //   `integration-github`,
  //   `integration-sourceforge`,
  //   `integration-db`,
  //   reconciliation
  // )

val zioVersion        = "2.1.24"
val zioHttpVersion    = "3.7.4"
val zioJsonVersion    = "0.8.0"
val zioConfigVersion  = "4.0.6"
val zioLoggingVersion = "2.5.3"
val zioSchemaVersion  = "1.7.6"
val zioQueryVersion   = "0.7.7"
val sttpVersion       = "3.11.0"
val calibanVersion    = "2.9.1"
val quillVersion      = "4.8.6"
val tranzactIOVersion = "5.6.0"

val commonDeps = Seq(
  "dev.zio" %% "zio"               % zioVersion,
  "dev.zio" %% "zio-logging"       % zioLoggingVersion,
  "dev.zio" %% "zio-logging-slf4j" % zioLoggingVersion,
  "dev.zio" %% "zio-schema"        % zioSchemaVersion,
  "dev.zio" %% "zio-schema-json"   % zioSchemaVersion,
  "dev.zio" %% "zio-http"          % zioHttpVersion,
  "dev.zio" %% "zio-query"         % zioQueryVersion,
//  "dev.zio"                     %% "zio-json"  % zioJsonVersion,
  // "dev.zio" %% "zio-mock"            % zioVersion % "test",
  "dev.zio" %% "zio-config"          % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
// Alternative persistence options. We've avoided quill due to the out of date
// compilation/library issues with things like zio-json. I've elected to use anorm over
// Doobie which avoids pulling in another library as well. Slick seems nice but I haven't
// worked with it in a ZIO environment to determine if it's feasible and worthwhile to use.
//  "io.getquill" %% "quill-jdbc-zio"      % quillVersion,
//   "io.github.gaelrenoux" %% "tranzactio-doobie" % tranzactIOVersion,
//  "com.typesafe.slick"   %% "slick-hikaricp"   % "3.6.1",

  "io.github.gaelrenoux" %% "tranzactio-anorm"  % tranzactIOVersion,
  "org.flywaydb"          % "flyway-core"       % "11.20.2",
  "org.xerial"            % "sqlite-jdbc"       % "3.51.1.0",
  "dev.zio"              %% "zio-test"          % zioVersion % "test",
  "dev.zio"              %% "zio-test-junit"    % zioVersion % "test",
  "dev.zio"              %% "zio-test-sbt"      % zioVersion % "test",
  "dev.zio"              %% "zio-test-magnolia" % zioVersion % "test"
)

// If needing graphql api access
// val calibanClientDeps = Seq(
//   "com.github.ghostdogpr" %% "caliban-client" % calibanVersion
// )
