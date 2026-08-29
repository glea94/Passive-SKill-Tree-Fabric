package daripher.skilltree.mixin;

import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(ClientAdvancements.class)
public interface ClientAdvancementsAccessor {
<<<<<<< Updated upstream
    
=======
>>>>>>> Stashed changes
    @Accessor("progress")
    Map<AdvancementHolder, AdvancementProgress> getProgress();
}
