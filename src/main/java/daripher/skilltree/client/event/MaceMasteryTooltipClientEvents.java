package daripher.skilltree.client.event;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.event.ItemTooltipPSTEvent;
import daripher.skilltree.event.MaceMasteryEvents;
import daripher.skilltree.event.PSTEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.List;
public class MaceMasteryTooltipClientEvents {
    private record SpecialAbility(Identifier nodeSkillId, String translationKeySuffix) {}
    private static final int TITLE_WIDTH_PADDING = 4;
    private static final Identifier NODE_6 = Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_6");
    private static final Identifier NODE_7 = Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_7");
    private static final Identifier NODE_8 = Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_8");
    private static final Identifier NODE_9 = Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_9");
    private static final Identifier NODE_10 = Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_10");
    private static final Identifier NODE_11 = Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_11");
    private static final List<SpecialAbility> SPECIAL_ABILITIES = List.of(
            new SpecialAbility(NODE_6, "haste"),
            new SpecialAbility(NODE_8, "speed"),
            new SpecialAbility(NODE_10, "fire_resistance"),
            new SpecialAbility(NODE_10, "dash"),
            new SpecialAbility(NODE_7, "strength"),
            new SpecialAbility(NODE_7, "light_foot"),
            new SpecialAbility(NODE_9, "resistance"),
            new SpecialAbility(NODE_9, "stun"),
            new SpecialAbility(NODE_11, "regeneration"),
            new SpecialAbility(NODE_11, "god")
    );
    public static void register() {
        PSTEvents.ITEM_TOOLTIP.register(MaceMasteryTooltipClientEvents::addSpecialAbilitiesTooltip);
    }
    private static void addSpecialAbilitiesTooltip(ItemTooltipPSTEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (!MaceMasteryEvents.isMaceMasteryMace(itemStack)) {
            return;
        }
        Player player = event.getEntity();
        if (player == null) {
            return;
        }
        List<SpecialAbility> unlockedAbilities = SPECIAL_ABILITIES.stream()
                .filter(ability -> MaceMasteryEvents.hasLearnedSkill(player, ability.nodeSkillId()))
                .toList();
        if (unlockedAbilities.isEmpty()) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        List<Component> tooltip = event.getToolTip();
        List<MutableComponent> titles = unlockedAbilities.stream()
                .map(MaceMasteryTooltipClientEvents::buildTitle)
                .toList();
        int titlesMaxWidth = titles.stream().mapToInt(title -> font.width(title.getString())).max().orElse(0) + TITLE_WIDTH_PADDING;
        int existingLinesMaxWidth = tooltip.stream().mapToInt(line -> font.width(line.getString())).max().orElse(0);
        int wrapWidth = Math.max(titlesMaxWidth, existingLinesMaxWidth);
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("skilltree.mace_mastery_tooltip.header").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));
        for (int i = 0; i < unlockedAbilities.size(); i++) {
            tooltip.add(titles.get(i));
            MutableComponent description = buildDescription(unlockedAbilities.get(i));
            tooltip.addAll(TooltipHelper.split(description, font, wrapWidth));
        }
    }
    private static MutableComponent buildTitle(SpecialAbility ability) {
        int color = MaceMasteryEvents.getTierTintColor(ability.nodeSkillId());
        Style titleStyle = Style.EMPTY.withColor(TextColor.fromRgb(color)).withBold(true);
        return Component.translatable("skilltree.mace_mastery_tooltip." + ability.translationKeySuffix() + ".name").withStyle(titleStyle);
    }
    private static MutableComponent buildDescription(SpecialAbility ability) {
        return Component.translatable("skilltree.mace_mastery_tooltip." + ability.translationKeySuffix() + ".description").withStyle(ChatFormatting.GRAY);
    }
}