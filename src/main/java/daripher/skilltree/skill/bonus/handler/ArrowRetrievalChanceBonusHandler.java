package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.entity.persistentdata.PersistentDataProvider;
import daripher.skilltree.event.LivingHurtPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.mixin.AbstractArrowAccessor;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.ArrowRetrievalBonus;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Portage Fabric. LivingDeathEvent (Forge) -> ServerLivingEntityEvents.ALLOW_DEATH (Fabric API,
 * équivalent natif direct) : on ne veut jamais annuler la mort ici, juste réagir dessus, donc on
 * exécute notre logique puis on renvoie toujours true (mort autorisée), comme le faisait
 * l'original qui ne l'annulait pas non plus.
 */
public class ArrowRetrievalChanceBonusHandler {
    public static final String STUCK_ARROWS_TAG_NAME = "StuckArrows";

    public static void register() {
        PSTEvents.LIVING_HURT.register(ArrowRetrievalChanceBonusHandler::saveStuckArrows);
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            retrieveArrows(entity);
            return true;
        });
    }

    private static void saveStuckArrows(LivingHurtPSTEvent event) {
        DamageSource damageSource = event.getSource();
        if (!(damageSource.getDirectEntity() instanceof AbstractArrow arrow)) {
            return;
        }
        if (arrow.pickup != AbstractArrow.Pickup.ALLOWED) {
            return;
        }
        if (!(arrow.getOwner() instanceof Player player)) {
            return;
        }
        AbstractArrowAccessor arrowAccessor = (AbstractArrowAccessor) arrow;
        ItemStack arrowStack = arrowAccessor.invokeGetPickupItem();
        if (arrowStack == null) {
            return;
        }
        List<ArrowRetrievalBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, ArrowRetrievalBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float retrievalChance = 0f;
        for (ArrowRetrievalBonus bonus : skillBonuses) {
            retrievalChance += bonus.getChance();
        }
        if (player.getRandom().nextFloat() >= retrievalChance) {
            return;
        }
        LivingEntity target = event.getEntity();
        CompoundTag targetPersistentData = PersistentDataProvider.get(target);
        ListTag stuckArrowsTag = targetPersistentData.getList(STUCK_ARROWS_TAG_NAME, Tag.TAG_COMPOUND);
        stuckArrowsTag.add(arrowStack.save(new CompoundTag()));
        targetPersistentData.put(STUCK_ARROWS_TAG_NAME, stuckArrowsTag);
    }

    private static void retrieveArrows(LivingEntity entity) {
        CompoundTag entityPersistentData = PersistentDataProvider.get(entity);
        ListTag arrowsTag = entityPersistentData.getList(STUCK_ARROWS_TAG_NAME, Tag.TAG_COMPOUND);
        if (arrowsTag.isEmpty()) {
            return;
        }
        for (Tag tag : arrowsTag) {
<<<<<<< Updated upstream
            ItemStack arrowStack = ItemStack.of((CompoundTag) tag);
            entity.spawnAtLocation(arrowStack);
=======
            ItemStack arrowStack = ItemStack.parse(registries, tag).orElse(ItemStack.EMPTY);
            if (arrowStack.isEmpty()) {
                continue;
            }
            entity.spawnAtLocation((ServerLevel) entity.level(), arrowStack);
>>>>>>> Stashed changes
        }
    }
}
