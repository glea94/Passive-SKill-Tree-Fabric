package daripher.skilltree.event;

import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.List;

/**
 * Portage Fabric : logique identique, seule la souscription à LivingAttackEvent change.
 * <p>
 * CORRECTION 1.21.1 : ItemStack a perdu toute l'ancienne API NBT libre
 * (getTag()/getOrCreateTag()/hasTag()) au profit des Data Components. Le NBT personnalisé
 * (ce que ce fichier stockait auparavant directement sur l'ItemStack) doit désormais transiter
 * par le composant standard DataComponents.CUSTOM_DATA (classe CustomData), via les méthodes
 * utilitaires getOrCreateCustomTag()/setCustomTag()/hasCustomTag() ajoutées ci-dessous.
 */
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
        getPoisonedWeaponEffects(mainHandItem).forEach(pEffectInstance -> event.getEntity().addEffect(pEffectInstance, player));
        if (!hasInfinitePoisonUses(mainHandItem)) {
            consumePoisonStack(mainHandItem);
        }
    }

    public static void setPoisonedWeaponEffects(ItemStack itemStack, ItemStack potionStack, int maxUses) {
        // CORRECTION 1.21.1 : On extrait les effets depuis le nouveau Data Component POTION_CONTENTS
        PotionContents contents = potionStack.get(DataComponents.POTION_CONTENTS);
        // CORRECTION 1.21.1 : PotionContents#getAllEffects() renvoie Iterable<MobEffectInstance>,
        // plus List<MobEffectInstance> ; on copie donc explicitement dans une List.
        List<MobEffectInstance> potionEffects = new ArrayList<>();
        if (contents != null) {
            contents.getAllEffects().forEach(potionEffects::add);
        }

        ListTag effectsTagList = new ListTag();

        // CORRECTION 1.21.1 : MobEffectInstance#save() ne prend plus de CompoundTag en paramètre ;
        // il en construit et renvoie désormais un nouveau lui-même.
        potionEffects.stream().map(MobEffectInstance::save).forEach(effectsTagList::add);

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
        if (!itemTag.contains(WEAPON_EFFECTS_TAG_NAME, Tag.TAG_LIST)) {
            return false;
        }
        if (itemTag.getList(WEAPON_EFFECTS_TAG_NAME, Tag.TAG_COMPOUND).isEmpty()) {
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
        return itemTag.getInt(POISON_USES_LEFT_TAG_NAME);
    }

    public static List<MobEffectInstance> getPoisonedWeaponEffects(ItemStack itemStack) {
        if (!hasPoison(itemStack)) {
            return List.of();
        }
        CompoundTag itemTag = getOrCreateCustomTag(itemStack);
        List<MobEffectInstance> effects = new ArrayList<>();
        ListTag effectsListTag = itemTag.getList(WEAPON_EFFECTS_TAG_NAME, Tag.TAG_COMPOUND);
        // CORRECTION 1.21.1 (v2) : contrairement à ce qu'indiquait le premier passage de
        // migration, MobEffectInstance#load(CompoundTag) ne renvoie PAS un Optional mais un
        // MobEffectInstance potentiellement null (nullable) en cas d'échec de parsing.
        for (Tag tag : effectsListTag) {
            MobEffectInstance instance = MobEffectInstance.load((CompoundTag) tag);
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

    // --- CORRECTION 1.21.1 : helpers de remplacement pour l'ancienne API NBT libre d'ItemStack ---

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