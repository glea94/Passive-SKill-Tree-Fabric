package daripher.skilltree.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.multiplayer.ClientAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * 1.21.1 : ClientAdvancements n'expose plus aucun getter public pour la progression
 * (le champ "progress" est privé). Le type de la map est aussi passé de
 * Map<Advancement, AdvancementProgress> à Map<AdvancementHolder, AdvancementProgress>,
 * puisque Advancement seul ne porte plus d'id (c'est AdvancementHolder qui le fait).
 */
@Mixin(ClientAdvancements.class)
public interface ClientAdvancementsAccessor {
    @Accessor("progress")
    Map<AdvancementHolder, AdvancementProgress> getProgress();
}