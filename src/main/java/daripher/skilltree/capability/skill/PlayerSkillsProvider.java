package daripher.skilltree.capability.skill;

import daripher.skilltree.SkillTreeMod;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerSkillsProvider implements EntityComponentInitializer {
    public static final ComponentKey<IPlayerSkills> KEY = ComponentRegistry.getOrCreate(
            new ResourceLocation(SkillTreeMod.MOD_ID, "player_skills"), IPlayerSkills.class);

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
