package daripher.skilltree.init;

import daripher.skilltree.config.ServerConfig;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class PSTLootModifiers {
    // Fix 1.21.5 : BuiltInLootTables ne contient plus de constantes pour les entités (dragon inclus),
    // seulement chests/gameplay/archaeology/shearing — clé reconstruite via ResourceKey.create,
    // même méthode utilisée en interne par BuiltInLootTables.register() (confirmé par décompilation)
    private static final ResourceKey<LootTable> ENDER_DRAGON = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("entities/ender_dragon"));

    public static void register() {
        // Alignment 1.21.4: Use Fabric Loot API v3 to dynamically inject mod items into vanilla drop lists
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // Check if it's the Ender Dragon loot table and configuration allows the drop
            if (source.isBuiltin() && ENDER_DRAGON.equals(key) && ServerConfig.dragon_drops_amnesia_scroll) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(PSTItems.AMNESIA_SCROLL.get()));

                tableBuilder.withPool(poolBuilder);
            }
        });
    }
}