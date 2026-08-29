package daripher.skilltree.event;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Locale;
public class ShieldBashEvents {
    private static final String BASHING_SHIELD_TAG_NAME = "skilltree_bashing_shield";
    private static final String BASH_COOLDOWN_TAG_NAME = "skilltree_bash_cooldown_until";
    public static final Identifier BASHING_SHIELD_RECIPE_ID =
            Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "blacksmith_bashing_shield_crafting");
    private record BashTier(Identifier skillId, float damage, double range,
                            int durabilityCost, int cooldownTicks, double knockback) {}
    private static final List<BashTier> TIERS = List.of(
            new BashTier(Identifier.fromNamespaceAndPath("skilltree", "blacksmith_6"), 4.0f, 2.5, 2, 40, 0.6),
            new BashTier(Identifier.fromNamespaceAndPath("skilltree", "blacksmith_9"), 7.0f, 3.0, 3, 35, 0.8),
            new BashTier(Identifier.fromNamespaceAndPath("skilltree", "blacksmith_12"), 10.0f, 3.5, 4, 30, 1.0)
    );
    public static void register() {
        UseItemCallback.EVENT.register(ShieldBashEvents::onUseItem);
    }
    private static InteractionResult onUseItem(Player player, Level level, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!isBashingShield(stack)) {
            return InteractionResult.PASS;
        }
        if (!player.isCrouching()) {
            // Clic droit normal sans accroupissement : on laisse le blocage vanilla du
            // shield se déclencher normalement.
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }
        BashTier tier = getActiveTier(player);
        if (tier == null) {
            return InteractionResult.PASS;
        }
        if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }
        return tryBash(serverPlayer, serverLevel, stack, hand, tier);
    }
    private static InteractionResult tryBash(ServerPlayer player, ServerLevel level, ItemStack shield,
                                             InteractionHand hand, BashTier tier) {
        CompoundTag itemTag = getOrCreateCustomTag(shield);
        long now = level.getGameTime();
        long readyAt = itemTag.getLongOr(BASH_COOLDOWN_TAG_NAME, 0L);
        if (now < readyAt) {
            return InteractionResult.FAIL;
        }
        itemTag.putLong(BASH_COOLDOWN_TAG_NAME, now + tier.cooldownTicks());
        setCustomTag(shield, itemTag);
        player.getCooldowns().addCooldown(shield, tier.cooldownTicks());
        int hits = bashEntities(player, level, tier);
        damageShield(shield, player, hand, tier.durabilityCost());
        level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK.value(), SoundSource.PLAYERS, 1.0f, 0.8f);
        SkillTreeMod.LOGGER.debug("[ShieldBash] {} déclenche un bash palier {} : {} cible(s)",
                player.getName().getString(), tier.skillId(), hits);
        return InteractionResult.SUCCESS;
    }
    private static int bashEntities(ServerPlayer player, ServerLevel level, BashTier tier) {
        Vec3 look = player.getLookAngle();
        AABB area = player.getBoundingBox().expandTowards(look.scale(tier.range())).inflate(0.6);
        DamageSource damageSource = level.damageSources().playerAttack(player);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive() && !(e instanceof Player));
        for (LivingEntity target : targets) {
            target.invulnerableTime = 0;
            target.hurt(damageSource, tier.damage());
            Vec3 knockDir = target.position().subtract(player.position()).normalize();
            target.knockback(tier.knockback(), -knockDir.x, -knockDir.z, damageSource, 1.0f);
        }
        return targets.size();
    }
    private static void damageShield(ItemStack shield, ServerPlayer player, InteractionHand hand, int cost) {
        if (player.getAbilities().instabuild) {
            return;
        }
        int newDamage = shield.getDamageValue() + cost;
        if (newDamage >= shield.getMaxDamage()) {
            shield.shrink(1);
            player.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
        } else {
            shield.setDamageValue(newDamage);
        }
    }
    private static BashTier getActiveTier(Player player) {
        if (!PlayerSkillsProvider.hasSkills(player)) {
            return null;
        }
        List<Identifier> learned = PlayerSkillsProvider.get(player).getPlayerSkills().stream()
                .map(PassiveSkill::getId).toList();
        BashTier active = null;
        for (BashTier tier : TIERS) {
            if (learned.contains(tier.skillId())) {
                active = tier;
            }
        }
        return active;
    }
    public static boolean isBashingShield(ItemStack stack) {
        if (!EquipmentPredicate.isShield(stack)) {
            return false;
        }
        if (!stack.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA)) {
            return false;
        }
        return getOrCreateCustomTag(stack).getBooleanOr(BASHING_SHIELD_TAG_NAME, false);
    }
    public static void markAsBashingShield(ItemStack stack) {
        CompoundTag tag = getOrCreateCustomTag(stack);
        tag.putBoolean(BASHING_SHIELD_TAG_NAME, true);
        setCustomTag(stack, tag);
    }
    private static CompoundTag getOrCreateCustomTag(ItemStack stack) {
        return stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }
    private static void setCustomTag(ItemStack stack, CompoundTag tag) {
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}