package daripher.skilltree.skill.bonus.handler;

import com.mojang.serialization.Codec;
import daripher.skilltree.entity.persistentdata.PersistentDataProvider;
import daripher.skilltree.event.LivingHurtPSTEvent;
import daripher.skilltree.event.PSTEvents;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.CheatDeathBonus;
import daripher.skilltree.util.event.EventPriority;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class CheatDeathBonusHandler {
    private static final String LAST_TRIGGER_TAG_NAME = "BulwarkResolveLastTrigger";

    public static void register() {
        PSTEvents.LIVING_HURT.register(EventPriority.LOWEST, CheatDeathBonusHandler::preventFatalDamage);
    }

    private static void preventFatalDamage(LivingHurtPSTEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }
        float remainingHealth = player.getHealth() - event.getAmount();
        if (remainingHealth > 0f) {
            return;
        }
        List<CheatDeathBonus> bonuses = SkillBonusProvider.getSkillBonuses(player, CheatDeathBonus.class);
        if (bonuses.isEmpty()) {
            return;
        }
        int shortestCooldown = bonuses.stream().mapToInt(CheatDeathBonus::getCooldownTicks).min().orElse(Integer.MAX_VALUE);
        long currentTime = player.level().getGameTime();
        long lastTrigger = getLastTrigger(player);
        if (currentTime - lastTrigger < shortestCooldown) {
            return;
        }
        if (player.getHealth() <= 1f) {
            return;
        }
        event.setAmount(player.getHealth() - 1f);
        setLastTrigger(player, currentTime);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.level().broadcastEntityEvent(player, (byte) 35);
    }

    private static long getLastTrigger(Player player) {
        CompoundTag data = PersistentDataProvider.get(player);
        return data.read(LAST_TRIGGER_TAG_NAME, Codec.LONG).orElse(Long.MIN_VALUE / 2);
    }

    private static void setLastTrigger(Player player, long gameTime) {
        CompoundTag data = PersistentDataProvider.get(player);
        data.store(LAST_TRIGGER_TAG_NAME, Codec.LONG, gameTime);
    }
}