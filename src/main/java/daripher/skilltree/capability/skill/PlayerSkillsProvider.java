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

/**
 * Portage Fabric de la capacité PlayerSkills, via Cardinal Components API.
 * <p>
 * Équivalences avec la version Forge :
 * - @AutoRegisterCapability + AttachCapabilitiesEvent -> registerForPlayers() ci-dessous :
 *   Cardinal attache le composant à CHAQUE joueur automatiquement, pas besoin d'event.
 * - PlayerEvent.Clone (persistThroughDeath, copie manuelle des NBT) -> RespawnCopyStrategy.ALWAYS_COPY,
 *   mécanisme intégré à Cardinal Components pour ce cas précis (conserver les données à la mort).
 * - get(Player) / hasSkills(Player) : mêmes noms et signatures que la version Forge, pour que les
 *   fichiers du mod qui les appellent (PlayerSkillsProvider.get(player) / .hasSkills(player))
 *   continuent de compiler sans modification.
 * <p>
 * Reste volontairement HORS de cette classe (fera partie de l'étape "events" / réseau) :
 * - syncSkills / syncPlayerSkills / sendTreeResetMessage : ce sont des listeners d'event
 *   (PlayerLoggedInEvent, EntityJoinLevelEvent côté Forge) qui envoient des packets réseau.
 *   Ce ne sont pas des mécanismes de capacité, mais des events + réseau : à porter avec le
 *   reste du système de packets, pas ici.
 * - restoreSkillsAttributeModifiers : ré-applique les bonus au (re)join, dépend elle aussi du
 *   portage des events, pas de la capacité elle-même.
 */
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
