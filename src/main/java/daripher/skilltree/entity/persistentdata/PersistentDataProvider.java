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
    public static final ComponentKey<IPersistentData> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "persistent_data"), IPersistentData.class);
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, KEY, entity -> new PersistentData());
        registry.registerForPlayers(KEY, player -> new PersistentData(), RespawnCopyStrategy.ALWAYS_COPY);
    }
    public static CompoundTag get(Entity entity) {
        return KEY.get(entity).getTag();
    }
}
