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
    private static final ResourceKey<LootTable> ENDER_DRAGON = ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("entities/ender_dragon"));
    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (source.isBuiltin() && ENDER_DRAGON.equals(key) && ServerConfig.dragon_drops_amnesia_scroll) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(PSTItems.AMNESIA_SCROLL.get()));
                tableBuilder.withPool(poolBuilder);
            }
        });
    }
}