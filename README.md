# 🌳 Passive Skill Tree — Fabric Port (1.20.1 now) (WIP for 1.21.x + 26.1, 26.2)

![Minecraft Version](https://shields.io)
![Loader](https://shields.io)
![Status](https://shields.io)

Ce projet est un portage non officiel pour **Fabric 1.20.1** du célèbre mod d'arbre de compétences passives. Il permet aux joueurs de débloquer des bonus statistiques uniques au fur et à mesure de leur progression en jeu via une interface visuelle complète.

---

## 👤 Crédits & Mentions Légales

* **Auteur d'origine :** Un immense merci à **000000000_00** (et l'équipe de développement initiale) pour la création de ce mod incroyable.
* **Mod d'origine (Forge) :** Retrouvez le projet original sur sa page officielle [CurseForge - Passive Skill Tree](https://www.curseforge.com/minecraft/mc-mods/passive-skill-tree).

*Ce portage a été réalisé à des fins de compatibilité et de développement personnel au sein de l'écosystème Fabric.*

---

## 🛠️ Dépendances Requises (Dependencies)

Pour faire fonctionner ce mod en jeu ou dans votre environnement de développement, les bibliothèques suivantes sont **strictement obligatoires** :

| Mod | Utilité | Lien de Téléchargement |
| :--- | :--- | :--- |
| <img src="https://shields.io🦺-yellow"> | Base essentielle pour l'écosystème Fabric | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api) |
| <img src="https://shields.io⚙️-purple"> | Supprime les limites imposées par Minecraft sur les attributs | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/attributefix) |
| <img src="https://shields.io❤️-red"> | Gère proprement les modifications de barres de vie maximales | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/max-health-fix) |

---

## 🚀 Fonctionnalités Prises en Charge

* 📊 **Système d'XP Synchrone :** Gagnez des niveaux et attribuez vos points directement depuis l'interface utilisateur.
* 🌐 **Datagen Intégré :** Génération automatique des fichiers de langue (`en_us`, `ru_ru`) et structures de données.
* 📦 **Compatibilité Prism Launcher :** Testé avec succès dans des modpacks complexes en environnement de jeu réel.

---

## 💻 Instructions pour le Développement (Gradle)

Si vous souhaitez cloner ce projet et travailler dessus :

1. Ouvrez le projet dans **IntelliJ IDEA**.
2. Synchronisez le projet avec Gradle (bouton de l'éléphant bleu).
3. Utilisez la commande suivante pour compiler votre propre fichier `.jar` final :
   ```bash
   ./gradlew build
   ```

*Note : Ne relancez pas la commande `runDatagen` sans avoir sécurisé vos fichiers du dossier `src/main/resources`, au risque de voir vos fichiers écrasés par le processus de nettoyage automatique de Fabric Loom.*
