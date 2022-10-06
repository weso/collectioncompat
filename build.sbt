lazy val scala212 = "2.12.16"
lazy val scala213 = "2.13.8"
lazy val scala3   = "3.1.3"
lazy val supportedScalaVersions = List(
  scala3,
  scala213,
  scala212
)

lazy val munitVersion       = "0.7.29"
lazy val munitEffectVersion = "1.0.7"

lazy val munit        = "org.scalameta" %% "munit"               % munitVersion
lazy val munitEffects = "org.typelevel" %% "munit-cats-effect-3" % munitEffectVersion

def priorTo2_13(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, minor)) if minor < 13 => true
    case _                              => false
  }

val Java11 = JavaSpec.temurin("11")

ThisBuild / githubWorkflowJavaVersions := Seq(Java11)
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

lazy val collectioncompat =
  project
    .in(file("."))
    .settings(commonSettings)

lazy val docs =
  project
    .in(file("collectioncompat-docs"))
    .settings(
      publish / skip := true,
      mdocSettings,
      ScalaUnidoc / unidoc / unidocProjectFilter := inAnyProject -- inProjects(noDocProjects: _*)
    )
    .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)

lazy val noDocProjects = Seq[ProjectReference]()

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

lazy val sharedDependencies = Seq(
  libraryDependencies ++= Seq(
    munit        % Test,
    munitEffects % Test
  ),
  testFrameworks += new TestFramework("munit.Framework")
)

val compilerOptions = Seq()

lazy val compilationSettings = Seq(
  scalacOptions ++= Seq()
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
