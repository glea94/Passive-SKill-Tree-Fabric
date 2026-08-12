package daripher.skilltree.entity.persistentdata;

import daripher.skilltree.SkillTreeMod;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class PersistentDataProvider implements EntityComponentInitializer {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public static final ComponentKey<IPersistentData> KEY = ComponentRegistry.getOrCreate(
            new ResourceLocation(SkillTreeMod.MOD_ID, "persistent_data"), IPersistentData.class);
=======
    // Aligned 1.21.4: Using Identifier.fromNamespaceAndPath is standard and type-safe for component capabilities
    public static final ComponentKey<IPersistentData> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "persistent_data"), IPersistentData.class);
>>>>>>> Stashed changes
=======
    // Aligned 1.21.4: Using Identifier.fromNamespaceAndPath is standard and type-safe for component capabilities
    public static final ComponentKey<IPersistentData> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "persistent_data"), IPersistentData.class);
>>>>>>> Stashed changes

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // Hooks capability trackers directly onto every base Entity and player instance safely
        registry.registerFor(Entity.class, KEY, entity -> new PersistentData());
        registry.registerForPlayers(KEY, player -> new PersistentData(), RespawnCopyStrategy.ALWAYS_COPY);
    }

    public static CompoundTag get(Entity entity) {
        return KEY.get(entity).getTag();
    }
}
