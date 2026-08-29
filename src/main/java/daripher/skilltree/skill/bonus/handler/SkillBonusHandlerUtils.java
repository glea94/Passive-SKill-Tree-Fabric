package daripher.skilltree.skill.bonus.handler;
import daripher.skilltree.entity.persistentdata.PersistentDataProvider;
import daripher.skilltree.event.LivingHurtPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.util.event.EventPriority;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.UUID;
public class SkillBonusHandlerUtils {
    public static final String LAST_ATTACK_TARGET_TAG_NAME = "LastAttackTarget";
    public static void register() {
        PSTEvents.LIVING_HURT.register(EventPriority.HIGH, SkillBonusHandlerUtils::setLastHurtEntity);
    }
    private static void setLastHurtEntity(LivingHurtPSTEvent event) {
        Player attacker = null;
        if (event.getSource().getEntity() instanceof Player player) {
            attacker = player;
        } else if (event.getSource().getDirectEntity() instanceof Player player) {
            attacker = player;
        }
        if (attacker == null) {
            return;
        }
        setLastPlayerAttackTarget(attacker, event.getEntity());
    }
    private static void setLastPlayerAttackTarget(Player player, LivingEntity target) {
        CompoundTag dataTag = PersistentDataProvider.get(player);
        dataTag.store(LAST_ATTACK_TARGET_TAG_NAME, UUIDUtil.CODEC, target.getUUID());
    }
    public static @Nullable Entity getLastPlayerAttackTarget(Player player) {
        CompoundTag playerPersistentData = PersistentDataProvider.get(player);
        Optional<UUID> lastTargetUUIDOpt = playerPersistentData.read(LAST_ATTACK_TARGET_TAG_NAME, UUIDUtil.CODEC);
        if (lastTargetUUIDOpt.isEmpty()) {
            return null;
        }
        UUID lastTargetUUID = lastTargetUUIDOpt.get();
        MinecraftServer minecraftServer = player.level().getServer();
        if (minecraftServer == null) {
            return null;
        }
        ResourceKey<Level> dimension = player.level().dimension();
        ServerLevel serverLevel = minecraftServer.getLevel(dimension);
        if (serverLevel == null) {
            return null;
        }
        return serverLevel.getEntity(lastTargetUUID);
    }
    public static void hurtIgnoringInvulnerabilityTime(LivingEntity livingEntity, DamageSource damageSource, float amount) {
        MinecraftServer minecraftServer = livingEntity.level().getServer();
        if (minecraftServer == null) {
            return;
        }
        TickTask delayedDamageTask = new TickTask(minecraftServer.getTickCount() + 1, () -> {
            if (livingEntity.isDeadOrDying()) {
                return;
            }
            livingEntity.invulnerableTime = 0;
            livingEntity.hurt(damageSource, amount);
        });
        minecraftServer.schedule(delayedDamageTask);
    }
}