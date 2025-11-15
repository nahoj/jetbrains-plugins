plugins {
  kotlin("jvm") version "2.2.20"
  // Version is provided in settings via 'org.jetbrains.intellij.platform.settings'
  id("org.jetbrains.intellij.platform")
}

kotlin {
  jvmToolchain(21) // 2024.2+ requires Java 21
}

group = "eu.nahoj.jetbrains"
version = "1.0.0"

dependencies {
  intellijPlatform {
    // Target the latest stable IntelliJ IDEA release
    intellijIdeaCommunity("2025.2.4")
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
    name = "Collapse Markdown Details"
    id = "eu.nahoj.jetbrains.mddetails"
    description = "Automatically folds HTML <details> sections in Markdown and shows <summary> as the placeholder."
    // Open-ended compatibility from 252 (2025.2) and up
    ideaVersion {
      sinceBuild = "252"
    }
  }
}

// Speed up CI builds; no searchable options to generate
tasks.named("buildSearchableOptions").configure { it.enabled = false }
