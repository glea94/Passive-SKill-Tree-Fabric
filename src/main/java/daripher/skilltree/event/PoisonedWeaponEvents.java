package daripher.skilltree.event;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import java.util.ArrayList;
import java.util.List;
public class PoisonedWeaponEvents {
    public static final String WEAPON_EFFECTS_TAG_NAME = "poisoned_weapon_effects";
    public static final String POISON_USES_LEFT_TAG_NAME = "poisoned_weapon_uses_left";
    public static void register() {
        PSTEvents.LIVING_ATTACK.register(PoisonedWeaponEvents::applyPoisonedWeaponEffect);
    }
    private static void applyPoisonedWeaponEffect(LivingAttackPSTEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (!EquipmentPredicate.isMeleeWeapon(mainHandItem)) {
            return;
        }
        if (!hasPoison(mainHandItem)) {
            return;
        }
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
        HolderLookup.Provider registryLookup = event.getEntity().level().registryAccess();
        getPoisonedWeaponEffects(mainHandItem, registryLookup).forEach(pEffectInstance -> event.getEntity().addEffect(pEffectInstance, player));
        if (!hasInfinitePoisonUses(mainHandItem)) {
            consumePoisonStack(mainHandItem);
        }
    }
    public static void setPoisonedWeaponEffects(ItemStack itemStack, ItemStack potionStack, int maxUses) {
        PotionContents contents = potionStack.get(DataComponents.POTION_CONTENTS);
        List<MobEffectInstance> potionEffects = new ArrayList<>();
        if (contents != null) {
            contents.getAllEffects().forEach(potionEffects::add);
        }
        ListTag effectsTagList = new ListTag();
<<<<<<< Updated upstream


        if (itemStack.getComponents().isEmpty()) return;





=======
        if (itemStack.getComponents().isEmpty()) return; 
>>>>>>> Stashed changes
        HolderLookup.Provider lookup = net.minecraft.client.Minecraft.getInstance().level != null ?
                net.minecraft.client.Minecraft.getInstance().level.registryAccess() : null;
        if (lookup != null) {
            potionEffects.stream()
                    .map(instance -> (Tag) MobEffectInstance.CODEC.encodeStart(lookup.createSerializationContext(NbtOps.INSTANCE), instance).getOrThrow())
                    .forEach(effectsTagList::add);
        }
        CompoundTag itemTag = getOrCreateCustomTag(itemStack);
        itemTag.put(WEAPON_EFFECTS_TAG_NAME, effectsTagList);
        itemTag.putInt(POISON_USES_LEFT_TAG_NAME, maxUses);
        setCustomTag(itemStack, itemTag);
    }
    public static boolean hasPoison(ItemStack itemStack) {
        if (!hasCustomTag(itemStack)) {
            return false;
        }
        CompoundTag itemTag = getOrCreateCustomTag(itemStack);
        if (!itemTag.contains(WEAPON_EFFECTS_TAG_NAME)) {
            return false;
        }
        if (itemTag.getListOrEmpty(WEAPON_EFFECTS_TAG_NAME).isEmpty()) {
            return false;
        }
        return hasInfinitePoisonUses(itemStack) || getPoisonUses(itemStack) > 0;
    }
    public static boolean hasInfinitePoisonUses(ItemStack itemStack) {
        int usesLeft = getPoisonUses(itemStack);
        return usesLeft == -1;
    }
    public static int getPoisonUses(ItemStack itemStack) {
        CompoundTag itemTag = getOrCreateCustomTag(itemStack);
        return itemTag.getIntOr(POISON_USES_LEFT_TAG_NAME, 0);
    }
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
    public static List<MobEffectInstance> getPoisonedWeaponEffects(ItemStack itemStack) {
        HolderLookup.Provider lookup = net.minecraft.client.Minecraft.getInstance().level != null ?
                net.minecraft.client.Minecraft.getInstance().level.registryAccess() : null;
        if (lookup == null) return List.of();
        return getPoisonedWeaponEffects(itemStack, lookup);
    }
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
    public static List<MobEffectInstance> getPoisonedWeaponEffects(ItemStack itemStack, HolderLookup.Provider registryLookup) {
        if (!hasPoison(itemStack)) {
            return List.of();
        }
        CompoundTag itemTag = getOrCreateCustomTag(itemStack);
        List<MobEffectInstance> effects = new ArrayList<>();
        ListTag effectsListTag = itemTag.getListOrEmpty(WEAPON_EFFECTS_TAG_NAME);
        for (Tag tag : effectsListTag) {
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
            MobEffectInstance instance = MobEffectInstance.CODEC.parse(registryLookup.createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(null);
            if (instance != null) {
                effects.add(instance);
            }
        }
        return effects;
    }
    private static void consumePoisonStack(ItemStack itemStack) {
        if (!hasPoison(itemStack)) {
            return;
        }
        CompoundTag itemTag = getOrCreateCustomTag(itemStack);
        int usesLeft = getPoisonUses(itemStack) - 1;
        if (usesLeft == 0) {
            clearWeaponEffects(itemStack);
            return;
        }
        itemTag.putInt(POISON_USES_LEFT_TAG_NAME, usesLeft);
        setCustomTag(itemStack, itemTag);
    }
    private static void clearWeaponEffects(ItemStack itemStack) {
        CompoundTag itemTag = getOrCreateCustomTag(itemStack);
        itemTag.remove(WEAPON_EFFECTS_TAG_NAME);
        itemTag.remove(POISON_USES_LEFT_TAG_NAME);
        setCustomTag(itemStack, itemTag);
    }
    private static boolean hasCustomTag(ItemStack itemStack) {
        return itemStack.has(DataComponents.CUSTOM_DATA);
    }
    private static CompoundTag getOrCreateCustomTag(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }
    private static void setCustomTag(ItemStack itemStack, CompoundTag tag) {
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}