package daripher.skilltree.capability.skill;

import daripher.skilltree.SkillTreeMod;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.resources.ResourceLocation;
=======
=======
>>>>>>> Stashed changes
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.resources.Identifier;
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerSkillsProvider implements EntityComponentInitializer {
    // Utilisation stricte de la méthode standard fromNamespaceAndPath recommandée en 1.21.4
    public static final ComponentKey<IPlayerSkills> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "player_skills"), IPlayerSkills.class);
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // Enregistrement de l'implémentation PlayerSkills pour chaque joueur avec stratégie de copie permanente au respawn
        registry.registerForPlayers(KEY, player -> new PlayerSkills(), RespawnCopyStrategy.ALWAYS_COPY);
    }

    public static @NotNull IPlayerSkills get(Player player) {
        return KEY.get(player);
    }

    public static boolean hasSkills(@NotNull Player player) {
        return KEY.isProvidedBy(player);
    }
}
