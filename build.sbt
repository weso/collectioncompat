lazy val scala212 = "2.12.16"
lazy val scala213 = "2.13.8"
lazy val scala3   = "3.1.3"
lazy val supportedScalaVersions = List(
  scala3,
  scala213,
  scala212
)

// Dependency versions
lazy val catsVersion        = "2.8.0"
lazy val catsEffectVersion  = "3.3.13"
lazy val circeVersion       = "0.14.2"
lazy val fs2Version         = "3.2.8"
lazy val munitVersion       = "0.7.29"
lazy val munitEffectVersion = "1.0.7"
lazy val pprintVersion      = "0.7.3"

lazy val catsCore     = "org.typelevel" %% "cats-core"           % catsVersion
lazy val catsKernel   = "org.typelevel" %% "cats-kernel"         % catsVersion
lazy val catsEffect   = "org.typelevel" %% "cats-effect"         % catsEffectVersion
lazy val circeCore    = "io.circe"      %% "circe-core"          % circeVersion
lazy val circeGeneric = "io.circe"      %% "circe-generic"       % circeVersion
lazy val circeParser  = "io.circe"      %% "circe-parser"        % circeVersion
lazy val fs2          = "co.fs2"        %% "fs2-core"            % fs2Version
lazy val fs2io        = "co.fs2"        %% "fs2-io"              % fs2Version
lazy val munit        = "org.scalameta" %% "munit"               % munitVersion
lazy val munitEffects = "org.typelevel" %% "munit-cats-effect-3" % munitEffectVersion
lazy val pprint       = "com.lihaoyi"   %% "pprint"              % pprintVersion

def priorTo2_13(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, minor)) if minor < 13 => true
    case _                              => false
  }

val Java11 = JavaSpec.temurin("11")

ThisBuild / githubWorkflowJavaVersions := Seq(Java11)

lazy val collectionCompat =
  project
    .in(file("."))
    .settings(commonSettings)
    .settings(
      ThisBuild / turbo  := true,
      crossScalaVersions := Nil,
      publish / skip     := true,
      ThisBuild / githubWorkflowBuild := Seq(
        WorkflowStep.Sbt(
          List(
            "clean",
            "test"
          ),
          id = None,
          name = Some("Test")
        )
      )
    )

lazy val docs =
  project
    .in(file("collectioncompat-docs"))
    .settings(
      noPublishSettings,
      mdocSettings,
      ScalaUnidoc / unidoc / unidocProjectFilter := inAnyProject -- inProjects(noDocProjects: _*)
    )
    .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)

lazy val mdocSettings = Seq(
  mdocVariables := Map(
    "VERSION" -> version.value
  ),
  ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(),
  ScalaUnidoc / unidoc / target              := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
  cleanFiles += (ScalaUnidoc / unidoc / target).value,
  docusaurusCreateSite := docusaurusCreateSite.dependsOn(Compile / unidoc).value,
  docusaurusPublishGhpages :=
    docusaurusPublishGhpages.dependsOn(Compile / unidoc).value,
  ScalaUnidoc / unidoc / scalacOptions ++= Seq(
    "-doc-source-url",
    s"https://github.com/weso/collectioncompat/tree/v${(ThisBuild / version).value}€{FILE_PATH}.scala",
    "-sourcepath",
    (LocalRootProject / baseDirectory).value.getAbsolutePath,
    "-doc-title",
    "collectioncompat",
    "-doc-version",
    s"v${(ThisBuild / version).value}"
  )
)

lazy val ghPagesSettings = Seq(
  git.remoteRepo := "git@github.com:weso/collectioncompat.git"
)

lazy val noDocProjects = Seq[ProjectReference](
)

lazy val noPublishSettings = publish / skip := true

lazy val sharedDependencies = Seq(
  libraryDependencies ++= Seq(
    munit        % Test,
    munitEffects % Test
  ),
  testFrameworks += new TestFramework("munit.Framework")
)

val compilerOptions = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Yno-predef",
  "-Ywarn-unused-import"
)

lazy val compilationSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8", // Specify character encoding used by source files.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.  "-encoding", "UTF-8",
    "-unchecked" // Enable additional warnings where generated code depends on assumptions.
  )
  // format: on
)

lazy val commonSettings = compilationSettings ++ sharedDependencies ++ Seq(
  organization        := "es.weso",
  sonatypeProfileName := "es.weso",
  homepage            := Some(url("https://github.com/weso/collectioncompat")),
  licenses            := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  scmInfo := Some(
    ScmInfo(url("https://github.com/weso/collectioncompat"), "scm:git:git@github.com:weso/collectioncompat.git")
  ),
  autoAPIMappings := true,
  apiURL          := Some(url("http://weso.github.io/collection/latest/api/")),
  autoAPIMappings := true,
  developers := List(
    Developer(
      id = "labra",
      name = "Jose Emilio Labra Gayo",
      email = "jelabra@gmail.com",
      url = url("https://labra.weso.es")
    )
  )
)
