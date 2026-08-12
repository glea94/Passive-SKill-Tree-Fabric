package daripher.skilltree.init;

import daripher.skilltree.config.ServerConfig;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class PSTLootModifiers {
    // Fix 1.21.5 : BuiltInLootTables ne contient plus de constantes pour les entités (dragon inclus),
    // seulement chests/gameplay/archaeology/shearing — clé reconstruite via ResourceKey.create,
    // même méthode utilisée en interne par BuiltInLootTables.register() (confirmé par décompilation)
    private static final ResourceKey<LootTable> ENDER_DRAGON = ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("entities/ender_dragon"));

    public static void register() {
        // en attente, voir le commentaire de classe
    }
}
