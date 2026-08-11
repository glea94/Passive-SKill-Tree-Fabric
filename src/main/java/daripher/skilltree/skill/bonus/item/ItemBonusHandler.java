package daripher.skilltree.skill.bonus.item;

import com.google.common.collect.ImmutableList;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.event.*;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import daripher.skilltree.skill.bonus.player.ItemUpgradeLimitBonusesBonus;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Portage Fabric 1.21.4.
 *
 * Régression corrigée : une version précédente de ce fichier enregistrait ici un second
 * abonnement à PSTEvents.ITEM_USE_FINISH (onPlayerConsumeItem) qui scannait TOUS les
 * SkillBonus du joueur par réflexion, à la recherche d'une méthode nommée "itemEaten" ou
 * "onItemEaten". Cette méthode n'existe nulle part dans le mod : ce bloc ne faisait donc
 * strictement rien (à part avaler silencieusement une exception à chaque item consommé) et
 * dupliquait, en le cassant, un mécanisme déjà porté correctement ailleurs. Le vrai système
 * "chance d'obtenir un effet en mangeant" est ItemUseEventListener (skill.bonus.event), déclenché
 * par EventListenerBonusHandler.triggerItemUsedEvents(...), lui-même déjà abonné à
 * PSTEvents.ITEM_USE_FINISH. ItemBonusHandler n'a donc pas à s'abonner à cet event : son rôle se
 * limite aux bonus PORTÉS PAR L'OBJET lui-même (bonus d'équipement, tooltips, bonus de craft/
 * amélioration stockés sur l'ItemStack), pas aux bonus déclenchés par un évènement de gameplay.
 */
public class ItemBonusHandler {
    public static final String UPGRADE_BONUSES_TAG_NAME = "UpgradeBonuses";
    public static final String CRAFTING_BONUSES_TAG_NAME = "CraftingBonuses";

    public static void register() {
        PSTEvents.LIVING_EQUIPMENT_CHANGE.register(ItemBonusHandler::addEquipmentAttributeBonuses);
        PSTEvents.ITEM_TOOLTIP.register(ItemBonusHandler::addItemBonusTooltips);
    }

    private static void addItemBonusTooltips(ItemTooltipPSTEvent event) {
        List<Component> toolTip = event.getToolTip();
        List<ItemBonus<?>> itemBonuses = getItemBonuses(event.getItemStack(), ItemBonus.class);
        if (itemBonuses.isEmpty()) {
            return;
        }
        toolTip.add(Component.empty());
        List<ItemBonus<?>> mergedItemBonuses = mergeItemBonuses(itemBonuses);
        for (ItemBonus<?> itemBonus : mergedItemBonuses) {
            Style style = TooltipHelper.getItemUpgradeStyle();
            for (MutableComponent mutableComponent : itemBonus.getFullTooltip()) {
                toolTip.add(mutableComponent.withStyle(style));
            }
        }
    }

    private static void addEquipmentAttributeBonuses(LivingEquipmentChangePSTEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        for (ItemBonus<?> itemBonus : getItemBonuses(event.getFrom(), EquipmentBonus.class)) {
            EquipmentBonus bonus = (EquipmentBonus) itemBonus;
            if (!(bonus.getSkillBonus() instanceof AttributeBonus attributeBonus)) {
                continue;
            }
            AttributeInstance attributeInstance = entity.getAttribute(attributeBonus.getAttribute());
            if (attributeInstance == null) {
                continue;
            }
            // Aligned 1.21.4: Retain the clean .id() record evaluation contract
            attributeInstance.removeModifier(attributeBonus.getModifier().id());
        }
        for (ItemBonus<?> itemBonus : getItemBonuses(event.getTo(), EquipmentBonus.class)) {
            EquipmentBonus bonus = (EquipmentBonus) itemBonus;
            if (!(bonus.getSkillBonus() instanceof AttributeBonus attributeBonus)) {
                continue;
            }
            if (attributeBonus.isDynamic()) {
                continue;
            }
            AttributeInstance attributeInstance = entity.getAttribute(attributeBonus.getAttribute());
            if (attributeInstance == null) {
                continue;
            }
            if (attributeInstance.hasModifier(attributeBonus.getModifier().id())) {
                continue;
            }
            attributeInstance.addTransientModifier(attributeBonus.getModifier());
        }
    }
    public static void addCraftingBonuses(ItemStack itemStack, GroupedItemBonus itemBonuses) {
        List<ItemBonus<?>> craftingBonuses = getBonusesFromTag(itemStack, CRAFTING_BONUSES_TAG_NAME);
        craftingBonuses.add(itemBonuses);
        setCraftingBonuses(itemStack, craftingBonuses);
    }

    public static List<ItemBonus<?>> getItemBonuses(ItemStack itemStack) {
        // Aligned 1.21.4: Direct presence evaluation for CustomData component layout elements
        if (!itemStack.has(DataComponents.CUSTOM_DATA)) {
            return ImmutableList.of();
        }
        List<ItemBonus<?>> list = new ArrayList<>();
        list.addAll(getUpgradeBonuses(itemStack));
        list.addAll(getCraftingBonuses(itemStack));
        return list;
    }

    private static List<ItemBonus<?>> getCraftingBonuses(ItemStack itemStack) {
        return getBonusesFromTag(itemStack, CRAFTING_BONUSES_TAG_NAME);
    }

    private static List<ItemBonus<?>> getUpgradeBonuses(ItemStack itemStack) {
        return getBonusesFromTag(itemStack, UPGRADE_BONUSES_TAG_NAME);
    }

    private static List<ItemBonus<?>> getBonusesFromTag(ItemStack itemStack, String subTagName) {
        CompoundTag stackTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        List<ItemBonus<?>> itemBonuses = new ArrayList<>();
        Optional<ListTag> bonusesTagListOpt = stackTag.getList(subTagName);
        if (bonusesTagListOpt.isEmpty()) {
            return new ArrayList<>();
        }
        ListTag bonusesTagList = bonusesTagListOpt.get();
        bonusesTagList.stream().map(CompoundTag.class::cast).forEach(bonusTag -> itemBonuses.add(deserializeBonus(bonusTag)));
        return itemBonuses;
    }

    public static List<ItemBonus<?>> getItemBonuses(ItemStack stack, Class<?> type) {
        List<ItemBonus<?>> bonuses = new ArrayList<>();
        for (ItemBonus<?> bonus : getItemBonuses(stack)) {
            if (bonus instanceof GroupedItemBonus listBonus) {
                bonuses.addAll(getItemBonuses(listBonus));
            } else {
                bonuses.add(bonus);
            }
        }
        return bonuses.stream().filter(type::isInstance).toList();
    }

    private static List<? extends ItemBonus<?>> getItemBonuses(GroupedItemBonus listBonus) {
        List<ItemBonus<?>> bonuses = new ArrayList<>();
        for (ItemBonus<?> bonus : listBonus.getInnerBonuses()) {
            if (bonus instanceof GroupedItemBonus innerListBonus) {
                List<? extends ItemBonus<?>> innerBonuses = getItemBonuses(innerListBonus);
                bonuses.addAll(innerBonuses);
            } else {
                bonuses.add(bonus);
            }
        }
        return bonuses;
    }

    public static void setUpgradeBonuses(ItemStack stack, List<ItemBonus<?>> bonuses) {
        setTagBonuses(stack, bonuses, UPGRADE_BONUSES_TAG_NAME);
    }

    public static void setCraftingBonuses(ItemStack stack, List<ItemBonus<?>> bonuses) {
        setTagBonuses(stack, bonuses, CRAFTING_BONUSES_TAG_NAME);
    }

    private static void setTagBonuses(ItemStack stack, List<ItemBonus<?>> bonuses, String tagName) {
        ListTag bonusesTagList = new ListTag();
        for (ItemBonus<?> itemBonus : bonuses) {
            bonusesTagList.add(serializeBonus(itemBonus));
        }
        // Updates the CustomData component mapping layer securely without allocating raw outer tags
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(tagName, bonusesTagList));
    }
    private static CompoundTag serializeBonus(ItemBonus<? extends ItemBonus<?>> bonus) {
        ItemBonus.Serializer serializer = bonus.getSerializer();
        CompoundTag bonusTag = serializer.serialize(bonus);
        ResourceLocation id = PSTRegistries.ITEM_BONUSES.get().getKey(serializer);
        bonusTag.putString("type", Objects.requireNonNull(id).toString());
        return bonusTag;
    }

    private static ItemBonus<?> deserializeBonus(CompoundTag tag) {
        if (!tag.contains("type")) {
            return null;
        }
<<<<<<< Updated upstream
        ResourceLocation id = new ResourceLocation(tag.getString("type"));
=======
        // Aligned 1.21.4: Using type-safe Identifier parsing matching modern Mojang conventions
        Identifier id = Identifier.parse(tag.getString("type").orElseThrow());
>>>>>>> Stashed changes
        ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(id);
        if (serializer == null) {
            return null;
        }
        try {
            return serializer.deserialize(tag);
        } catch (Exception exception) {
            String errorMessage = "Couldn't deserialize item bonus from " + tag;
            SkillTreeMod.LOGGER.error(errorMessage, exception);
            return null;
        }
    }

    public static int getCraftedBonusLimit(ItemStack itemStack, @Nullable Player player) {
        int limit = 1;
        if (player != null) {
            limit += SkillBonusProvider.getSkillBonuses(player, ItemUpgradeLimitBonusesBonus.class).stream()
                    .filter(bonus -> bonus.getItemCondition().test(itemStack)).map(ItemUpgradeLimitBonusesBonus::getAmount)
                    .reduce(Integer::sum).orElse(0);
        }
        return limit;
    }

    @NotNull
    @SuppressWarnings({"rawtypes", "unchecked", "SuspiciousMethodCalls"})
    public static <T> List<T> mergeItemBonuses(List<T> bonuses) {
        List<T> mergedBonuses = new ArrayList<>();
        for (T bonus : bonuses) {
            ItemBonus itemBonus = (ItemBonus) bonus;
            Optional<ItemBonus> mergeTarget = mergedBonuses.stream()
                    .map(ItemBonus.class::cast)
                    .filter(itemBonus::canMerge)
                    .findAny();

            if (mergeTarget.isPresent()) {
                mergedBonuses.remove(mergeTarget.get());
                mergedBonuses.add((T) mergeTarget.get().copy().merge(itemBonus));
            } else {
                mergedBonuses.add((T) itemBonus);
            }
        }
        return mergedBonuses;
    }

    public static GroupedItemBonus mergeGroupedItemBonuses(GroupedItemBonus itemBonus1, GroupedItemBonus itemBonus2) {
        ArrayList<ItemBonus<?>> innerBonuses = new ArrayList<>();
        innerBonuses.addAll(itemBonus1.getInnerBonuses());
        innerBonuses.addAll(itemBonus2.getInnerBonuses());
        ItemBonusHandler.mergeItemBonuses(innerBonuses);
        return new GroupedItemBonus(innerBonuses);
    }
}