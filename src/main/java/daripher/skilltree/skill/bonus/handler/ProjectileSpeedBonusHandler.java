package daripher.skilltree.skill.bonus.handler;

import daripher.skilltree.entity.persistentdata.PersistentDataProvider;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.ProjectileSpeedBonus;
import daripher.skilltree.util.event.EntityLoadHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Portage Fabric : EntityJoinLevelEvent -> ServerEntityEvents.ENTITY_LOAD (voir EntityLoadHelper pour loadedFromDisk()). */
public class ProjectileSpeedBonusHandler {
    public static final String IS_SPED_UP_TAG_NAME = "IS_SPED_UP";

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (!(entity instanceof Projectile projectile)) {
                return;
            }
            if (!(projectile.getOwner() instanceof Player player)) {
                return;
            }
            if (!EntityLoadHelper.isFreshlySpawned(projectile)) {
                return;
            }
            applyProjectileSpeedBonus(projectile, player);
        });
    }

    private static void applyProjectileSpeedBonus(Projectile projectile, Player player) {
        CompoundTag projectileTag = PersistentDataProvider.get(projectile);
        if (projectileTag.getBoolean(IS_SPED_UP_TAG_NAME).orElse(false)) {
            return;
        }
        float speedMultiplier = 1f;
        List<ProjectileSpeedBonus> skillBonuses = SkillBonusProvider.getSkillBonuses(player, ProjectileSpeedBonus.class);
        if (skillBonuses.isEmpty()) {
            return;
        }
        for (ProjectileSpeedBonus skillBonus : skillBonuses) {
            speedMultiplier += skillBonus.getProjectileSpeedModifier(player);
        }
        if (speedMultiplier == 1f) {
            return;
        }
        speedMultiplier = Math.max(0f, speedMultiplier);
        projectileTag.putBoolean(IS_SPED_UP_TAG_NAME, true);
        Vec3 projectileMovementVec = projectile.getDeltaMovement();
        projectile.setDeltaMovement(projectileMovementVec.scale(speedMultiplier));
    }
}