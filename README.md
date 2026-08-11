# 🌳 Passive Skill Tree — Fabric Port (1.20.1, 1.21.1, 1.21.4, 1.21.5, 1.21.8, 1.21.9, 1.21.10, 1.21.11, 26.1.2)


This project is an unofficial port for **Fabric 1.20.1** of the popular passive skill tree mod. It allows players to unlock unique statistical bonuses as they progress through the game using a fully custom visual user interface.

---

## 👤 Credits & Disclaimers

* **Original Author:** A huge thank you to **Daripher** (and the initial development team) for creating this amazing mod.
* **Original Mod (Forge):** Find the original project on its official [CurseForge - Passive Skill Tree](https://www.curseforge.com/minecraft/mc-mods/passive-skill-tree) page.

*This port was made for compatibility purposes and personal development within the Fabric ecosystem.*

---

## 🛠️ Required Dependencies

To run this mod in-game or within your development environment, the following libraries are **strictly mandatory**:

| Mod | Purpose | Download Link |
| :--- | :--- | :--- |
| <img src="https://shields.io🦺-yellow"> | Essential framework for the Fabric ecosystem | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api) |
| <img src="https://shields.io⚙️-purple"> | Removes vanilla Minecraft caps on attribute values | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/attributefix) |
| <img src="https://shields.io❤️-red"> | Correctly handles dynamic max health bars and calculations | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/max-health-fix) |

---

## 🚀 Key Features

* 📊 **Synchronized XP System:** Earn levels and allocate your custom skill points directly inside the UI.
* 🌐 **Built-in Datagen:** Automatic generation of core language assets (`en_us`, `ru_ru`) and data structures.
* 📦 **Prism Launcher Compatibility:** Tested successfully within complex modpacks in a production gaming environment.

---

## 💻 Development Instructions (Gradle)

If you wish to clone this repository and work on the project:

1. Open the project inside **IntelliJ IDEA**.
2. Sync the project with Gradle (click the blue elephant icon).
3. Use the following command to compile your own final distributable `.jar` file:
   ```bash
   ./gradlew build
   ```

*Note: Avoid running the `runDatagen` task without backing up your files inside `src/main/resources`. Fabric Loom's default cleanup process will overwrite manually placed assets if not explicitly isolated.*

ToDo
Update to 1.21.4 (soon) till 26.2 (later).
