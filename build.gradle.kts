import java.util.*

plugins {
  id("java")
  id("org.jetbrains.changelog") version "2.5.0"
  id("org.jetbrains.kotlin.jvm") version "2.4.0"
  id("org.jetbrains.intellij.platform") version "2.16.0"
}

group = "eu.nahoj.jetbrains"
version = "1.0.1"

// Load local.properties
val localProperties: Properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
  localPropertiesFile.inputStream().use { stream ->
    localProperties.load(stream)
  }
}

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  intellijPlatform {
    // Set e.g. `localIdePath=/snap/pycharm-community/current` in `local.properties`.
    // Falls back to downloading the IDE from Maven if unset.
    val localPath = localProperties.getProperty("localIdePath")
    if (localPath != null) {
      local(localPath)
    } else {
      intellijIdea("2026.1.1")
      jetbrainsRuntime()
    }
    // We depend on the bundled Markdown plugin
    bundledPlugin("org.intellij.plugins.markdown")
    // Optional helpers during development (uncomment if you want them):
    // instrumentationTools()
    // pluginVerifier()
    // testFramework()
  }
}

intellijPlatform {
  pluginConfiguration {
    // Open-ended compatibility from 252 (2025.2) and up
    ideaVersion {
      sinceBuild = "252"
    }
  }
}

kotlin {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
    vendor.set(JvmVendorSpec.JETBRAINS)
  }
}

// Speed up CI builds; no searchable options to generate
tasks.named("buildSearchableOptions").configure { enabled = false }
