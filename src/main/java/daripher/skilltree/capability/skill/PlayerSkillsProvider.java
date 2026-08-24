package daripher.skilltree.capability.skill;

import daripher.skilltree.SkillTreeMod;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerSkillsProvider implements EntityComponentInitializer {
    
    public static final ComponentKey<IPlayerSkills> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "player_skills"), IPlayerSkills.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        
        registry.registerForPlayers(KEY, player -> new PlayerSkills(), RespawnCopyStrategy.ALWAYS_COPY);
    }

    public static @NotNull IPlayerSkills get(Player player) {
        return KEY.get(player);
    }

    public static boolean hasSkills(@NotNull Player player) {
        return KEY.isProvidedBy(player);
    }
}
