import play.sbt.routes.RoutesKeys
import scoverage.ScoverageKeys

val appName = "books_library_2_7"

libraryDependencies += guice

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= AppDependencies())
  .configs(IntegrationTest)
  .settings(
    name := appName,
    scalaVersion := "2.12.8"
  )
  .settings(inConfig(IntegrationTest)(itSettings): _*)
  .settings(scoverageSettings : _*)

  .settings(PlayKeys.playDefaultPort := 9010)
  .settings(resolvers += Resolver.jcenterRepo,
    resolvers += Resolver.typesafeRepo("releases"),
    resolvers += Resolver.bintrayRepo("emueller", "maven"))
  .settings(RoutesKeys.routesImport ++= Seq(
    "models._", "java.time.LocalDate"
  ))

lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageExcludedPackages  := "<empty>;Reverse.*;model.*;config.*;.*(BuildInfo|Routes).*",
  ScoverageKeys.coverageMinimum           := 80,
  ScoverageKeys.coverageFailOnMinimum     := false,
  ScoverageKeys.coverageHighlighting      := true
)

lazy val itSettings = Defaults.itSettings ++ Seq(
  unmanagedSourceDirectories   := Seq(
    baseDirectory.value / "it",
    baseDirectory.value / "test-utils"
  ),
  unmanagedResourceDirectories := Seq(
    baseDirectory.value / "it" / "resources"
  ),
  parallelExecution            := false,
  fork                         := true
)


scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)
