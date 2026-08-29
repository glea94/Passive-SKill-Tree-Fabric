package daripher.skilltree.client.event;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.event.ItemTooltipPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.event.PoisonedWeaponEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.List;
public class PoisonedWeaponClientEvents {
    public static void register() {
        PSTEvents.ITEM_TOOLTIP.register(PoisonedWeaponClientEvents::addPoisonedWeaponTooltip);
    }
    private static void addPoisonedWeaponTooltip(ItemTooltipPSTEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (!PoisonedWeaponEvents.hasPoison(itemStack)) {
            return;
        }
        event.getToolTip().add(Component.empty());
        MutableComponent poisonedWeaponTooltip;
        if (PoisonedWeaponEvents.hasInfinitePoisonUses(itemStack)) {
            poisonedWeaponTooltip = Component.translatable("skilltree.poisoned_weapon");
        } else {
            int usesLeft = PoisonedWeaponEvents.getPoisonUses(itemStack);
            poisonedWeaponTooltip = Component.translatable("skilltree.poisoned_weapon.uses_left", usesLeft);
        }
        event.getToolTip().add(poisonedWeaponTooltip.withStyle(ChatFormatting.LIGHT_PURPLE));
        List<MobEffectInstance> effects = PoisonedWeaponEvents.getPoisonedWeaponEffects(itemStack);
        effects.forEach(mobEffectInstance -> addEffectTooltip(event.getToolTip(), mobEffectInstance));
    }
    private static void addEffectTooltip(List<Component> tooltip, MobEffectInstance mobEffectInstance) {
        Style style = TooltipHelper.getSkillBonusStyle(true);
        Component effectNameTooltip = TooltipHelper.getEffectTooltip(mobEffectInstance);
        MutableComponent effectTooltip;
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
        if (!mobEffectInstance.getEffect().value().isInstantenous()) {
            int duration = mobEffectInstance.getDuration();
            Component durationTooltip = getDurationTooltip(duration);
            effectTooltip = Component.translatable("skilltree.poisoned_weapon.effect", effectNameTooltip, durationTooltip);
        } else {
            effectTooltip = Component.translatable("skilltree.poisoned_weapon.effect_instant", effectNameTooltip);
        }
        tooltip.add(effectTooltip.withStyle(style));
    }
    private static @NotNull Component getDurationTooltip(int duration) {
        int durationSeconds = duration / 20;
        int displayedMinutes = durationSeconds / 60;
        int displayedSeconds = durationSeconds % 60;
        return Component.literal("%s:%02d".formatted(displayedMinutes, displayedSeconds));
    }
}