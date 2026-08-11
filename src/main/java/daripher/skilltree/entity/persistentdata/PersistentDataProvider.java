package daripher.skilltree.entity.persistentdata;

import daripher.skilltree.SkillTreeMod;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

/**
 * Portage Fabric de Entity.getPersistentData() (Forge), via Cardinal Components API (déjà
 * utilisée pour PlayerSkills). Enregistré pour Entity.class de façon générale : ce composant
 * est utilisé sur des joueurs, des LivingEntity (mobs) et des Projectile dans le reste du mod,
 * couvrir Entity.class directement évite d'avoir à enregistrer un composant par sous-type.
 */
public class PersistentDataProvider implements EntityComponentInitializer {
<<<<<<< Updated upstream
    public static final ComponentKey<IPersistentData> KEY = ComponentRegistry.getOrCreate(
            new ResourceLocation(SkillTreeMod.MOD_ID, "persistent_data"), IPersistentData.class);
=======
    // Aligned 1.21.4: Using Identifier.fromNamespaceAndPath is standard and type-safe for component capabilities
    public static final ComponentKey<IPersistentData> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "persistent_data"), IPersistentData.class);
>>>>>>> Stashed changes

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, KEY, entity -> new PersistentData());
        registry.registerForPlayers(KEY, player -> new PersistentData(), RespawnCopyStrategy.ALWAYS_COPY);
    }

    /** Portage Fabric de {@code entity.getPersistentData()} - remplace directement les appels de ce nom. */
    public static CompoundTag get(Entity entity) {
        return KEY.get(entity).getTag();
    }
}
