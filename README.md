# Collapse Markdown Details JetBrains Plugin

A simple, hacky plugin for JetBrains IDEs that makes `<details></details>` sections in Markdown files collapsed by default. 

## Installation
### From Source

- Ensure you have JDK 21 available.

```bash
cd collapse-markdown-details
./gradlew buildPlugin
```

or

- Open the project in your IDE.
- Build the plugin using the `buildPlugin` task.

Then,

- Install in IDE: Settings/Preferences > Plugins > Gear icon > Install Plugin from Disk… and choose the ZIP in build/distributions.
- Restart the IDE. Open a Markdown file containing `<details>…</details>`; the sections should appear collapsed with the summary as the hint.
