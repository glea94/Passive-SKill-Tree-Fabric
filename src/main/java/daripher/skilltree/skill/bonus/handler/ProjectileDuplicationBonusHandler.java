// Fichier : src/main/java/daripher/skilltree/skill/bonus/handler/ProjectileDuplicationBonusHandler.java
package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.entity.persistentdata.PersistentDataProvider;
import daripher.skilltree.event.LivingHurtPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.ProjectileDuplicationBonus;
import daripher.skilltree.util.event.EntityLoadHelper;
import daripher.skilltree.util.event.EventPriority;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Portage Fabric : EntityJoinLevelEvent -> ServerEntityEvents.ENTITY_LOAD (voir EntityLoadHelper). */
public class ProjectileDuplicationBonusHandler {
    public static final String IS_DUPLICATED_TAG_NAME = "IS_DUPLICATED";

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (!(entity instanceof Projectile projectile)) {
                return;
            }
            ServerLevel serverLevel = level;
            if (!(projectile.getOwner() instanceof Player player)) {
                return;
            }
            if (!EntityLoadHelper.isFreshlySpawned(projectile)) {
                return;
            }
            duplicateProjectiles(projectile, serverLevel, player);
        });
        PSTEvents.LIVING_HURT.register(EventPriority.HIGH, ProjectileDuplicationBonusHandler::removeInvulnerabilityTicksForDupedProjectiles);
    }

    private static void duplicateProjectiles(Projectile projectile, ServerLevel level, Player player) {
        CompoundTag projectileTag = PersistentDataProvider.get(projectile);
        if (projectileTag.getBooleanOr(IS_DUPLICATED_TAG_NAME, false)) {
            return;
        }
        List<ProjectileDuplicationBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, ProjectileDuplicationBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        float duplicationChance = 0f;
        for (ProjectileDuplicationBonus skillBonus : skillBonuses) {
            duplicationChance += skillBonus.getDuplicationChance(player);
        }
        if (duplicationChance == 0) {
            return;
        }
        projectileTag.putBoolean(IS_DUPLICATED_TAG_NAME, true);
        int projectileAmount = (int) duplicationChance;
        duplicationChance -= projectileAmount;
        RandomSource random = player.getRandom();
        if (random.nextFloat() < duplicationChance) {
            projectileAmount++;
        }
        spawnDuplicateProjectiles(projectile, level, player, projectileAmount);
    }

    private static void spawnDuplicateProjectiles(Projectile originalProjectile, ServerLevel level, Player player, int projectileAmount) {
        float spreadAngle = 5f;
        for (int i = 0; i < projectileAmount; i++) {
            int side = (i % 2 == 0 ? 1 : -1);
            int projectileNumber = i / 2 + 1;
            float angleOffset = projectileNumber * side * spreadAngle;
            spawnDuplicateProjectileWithOffset(originalProjectile, player, level, angleOffset);
        }
    }

    private static void spawnDuplicateProjectileWithOffset(Projectile original, Player player, ServerLevel level, float angleOffset) {
        EntityType<?> projectileType = original.getType();
        Projectile duplicate = (Projectile) projectileType.create(level, EntitySpawnReason.TRIGGERED);
        if (duplicate == null) {
            return;
        }
        PersistentDataProvider.get(duplicate).merge(PersistentDataProvider.get(original));
        Vec3 movementVector = original.getDeltaMovement();
        Vec3 rotatedDirection = rotateVector(movementVector, angleOffset);
        Vec3 originalPos = original.position();
        Vec3 duplicatePos = originalPos.add(rotatedDirection.normalize());
        duplicate.setPos(duplicatePos.x, duplicatePos.y, duplicatePos.z);
        duplicate.setDeltaMovement(rotatedDirection);
        duplicate.setOwner(player);
        CompoundTag projectileTag = PersistentDataProvider.get(duplicate);
        projectileTag.putBoolean(IS_DUPLICATED_TAG_NAME, true);
        if (duplicate instanceof AbstractArrow duplicateArrow) {
            AbstractArrow originalArrow = (AbstractArrow) original;
            duplicateArrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            ItemStack weaponItem = originalArrow.getWeaponItem();

            // Aligned 1.21.4: Map the item context safely over the native EnchantmentHelper initialization logic
            EnchantmentHelper.onProjectileSpawned(level, weaponItem != null ? weaponItem : ItemStack.EMPTY, duplicateArrow, item -> {});

            CompoundTag originalArrowTag = new CompoundTag();
            originalArrow.addAdditionalSaveData(originalArrowTag);
            duplicateArrow.setBaseDamage(originalArrowTag.getDoubleOr("damage", 2.0));
        } else if (duplicate instanceof AbstractThrownPotion potion) {
            AbstractThrownPotion originalPotion = (AbstractThrownPotion) original;
            potion.setItem(originalPotion.getItem());
        }
        level.addFreshEntity(duplicate);
    }

    private static Vec3 rotateVector(Vec3 vector, double angleDegrees) {
        double angleRadians = Math.toRadians(angleDegrees);
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);
        double x = vector.x * cos - vector.z * sin;
        double z = vector.x * sin + vector.z * cos;
        return new Vec3(x, vector.y, z);
    }

    private static void removeInvulnerabilityTicksForDupedProjectiles(LivingHurtPSTEvent event) {
        DamageSource damageSource = event.getSource();
        if (!(damageSource.getDirectEntity() instanceof Projectile projectile)) {
            return;
        }
        if (!(projectile.getOwner() instanceof Player)) {
            return;
        }
        CompoundTag projectileTag = PersistentDataProvider.get(projectile);
        if (!(projectileTag.getBooleanOr(IS_DUPLICATED_TAG_NAME, false))) {
            return;
        }
        LivingEntity target = event.getEntity();
        target.invulnerableTime = 0;
    }
}