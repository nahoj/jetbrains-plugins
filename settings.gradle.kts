import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform
rootProject.name = "jetbrains-collapse-markdown-details"

plugins {
  // Configures repositories for the IntelliJ Platform Gradle Plugin 2.x
  id("org.jetbrains.intellij.platform.settings") version "2.10.4"
}

dependencyResolutionManagement {
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
  repositories {
    mavenCentral()
    intellijPlatform {
      defaultRepositories()
    }
  }
}
