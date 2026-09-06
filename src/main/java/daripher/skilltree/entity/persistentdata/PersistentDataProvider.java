package daripher.skilltree.entity.persistentdata;

import daripher.skilltree.SkillTreeMod;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class PersistentDataProvider implements EntityComponentInitializer {
    public static final ComponentKey<IPersistentData> KEY = ComponentRegistry.getOrCreate(
            new ResourceLocation(SkillTreeMod.MOD_ID, "persistent_data"), IPersistentData.class);

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
