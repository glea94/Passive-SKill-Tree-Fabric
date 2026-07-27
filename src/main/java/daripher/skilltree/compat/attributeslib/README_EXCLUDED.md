# Compat AttributesLib (Apothic Attributes) - exclue

Ce mod optionnel (dev.shadowsoffire.attributeslib, alias "Apothic Attributes") n'a **aucune
version Fabric pour Minecraft 1.20.1** (Forge/NeoForge uniquement pour cette version ; une
version Fabric n'existe que depuis sa release 26.1.2, une version de Minecraft bien plus récente
que celle ciblée par ce portage).

Décision : exclusion de cette compat, comme Iron's Spellbooks. C'est une dépendance optionnelle
(`ModList.isLoaded("attributeslib")` côté Forge) : le mod fonctionne entièrement sans elle. Elle
n'ajoutait qu'un affichage visuel (icônes de bonus de compétence dans l'écran d'attributs
d'AttributesLib), aucune fonctionnalité du coeur du mod n'en dépend.
