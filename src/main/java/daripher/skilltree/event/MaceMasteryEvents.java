package daripher.skilltree.event;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.init.PSTStats;
import daripher.skilltree.skill.PassiveSkill;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
public class MaceMasteryEvents {
    public static final String MACE_MASTERY_TAG_NAME = "skilltree_mace_mastery";
    public static final String MACE_MASTERY_KILLS_TAG_NAME = "skilltree_mace_mastery_kills";
    private static final String DASH_COOLDOWN_TAG_NAME = "skilltree_mace_mastery_dash_cooldown_until";
    private static final String GOD_COOLDOWN_TAG_NAME = "skilltree_mace_mastery_god_cooldown_until";
    private static final Random RANDOM = new Random();
    private static final int EFFECT_DURATION_TICKS = 60;
    private static final int EFFECT_REFRESH_INTERVAL_TICKS = 20;
    private static final int DASH_COOLDOWN_TICKS = 50;
    private static final int GOD_COOLDOWN_TICKS = 1200;
    private static final double DASH_STRENGTH = 1.6;
    private static final float STUN_CHANCE = 0.15f;
    private static final int STUN_DURATION_TICKS = 60;
    private static final double GOD_RADIUS = 5.0;
    private static final float GOD_DAMAGE = 12.0f;
    private static final int GOD_STUN_DURATION_TICKS = 100;
    private static final Map<Mob, Long> STUNNED_MOBS = new IdentityHashMap<>();
    private static final Identifier LIGHTNING_BOLT_ID = Identifier.fromNamespaceAndPath("minecraft", "lightning_bolt");
    private record MaceMasteryNode(
            Identifier skillId, int requiredKills,
            int windBurstLevel, int densityLevel, int breachLevel, int fireAspectLevel,
            int hasteLevel, int speedLevel, int fireResistanceLevel,
            int strengthLevel, int resistanceLevel, int regenerationLevel,
            boolean lightFoot, boolean stun, boolean dash, boolean god) {}
    private static final List<MaceMasteryNode> NODES = List.of(
            new MaceMasteryNode(Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_1"), 5, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, false, false, false, false),
            new MaceMasteryNode(Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_2"), 20, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, false, false, false, false),
            new MaceMasteryNode(Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_3"), 40, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, false, false, false, false),
            new MaceMasteryNode(Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_4"), 60, 3, 4, 4, 0, 0, 0, 0, 0, 0, 0, false, false, false, false),
            new MaceMasteryNode(Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_5"), 85, 3, 5, 5, 1, 0, 0, 0, 0, 0, 0, false, false, false, false),
            new MaceMasteryNode(Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_6"), 100, 3, 5, 6, 1, 1, 0, 0, 0, 0, 0, false, false, false, false),
            new MaceMasteryNode(Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_8"), 105, 3, 6, 6, 1, 0, 1, 0, 0, 0, 0, false, false, false, false),
            new MaceMasteryNode(Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_10"), 120, 3, 6, 6, 2, 0, 0, 1, 0, 0, 0, false, false, true, false),
            new MaceMasteryNode(Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_7"), 130, 3, 6, 6, 2, 1, 0, 0, 1, 0, 0, true, false, false, false),
            new MaceMasteryNode(Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_9"), 135, 3, 6, 6, 2, 0, 1, 0, 0, 1, 0, false, true, false, false),
            new MaceMasteryNode(Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_11"), 150, 3, 6, 6, 3, 1, 1, 1, 1, 1, 1, true, true, true, true)
    );
    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(MaceMasteryEvents::onEntityKilled);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(MaceMasteryEvents::onAllowDamage);
        ServerLivingEntityEvents.AFTER_DAMAGE.register(MaceMasteryEvents::onAfterDamage);
        ServerTickEvents.END_SERVER_TICK.register(MaceMasteryEvents::onServerTick);
        UseItemCallback.EVENT.register(MaceMasteryEvents::onUseItem);
    }
    private static void onEntityKilled(LivingEntity killedEntity, DamageSource damageSource) {
        if (!(damageSource.getEntity() instanceof Player player)) {
            return;
        }
        if (killedEntity instanceof Player) {
            return;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (!isMaceMasteryMace(mainHandItem)) {
            return;
        }
        incrementKills(mainHandItem);
        player.awardStat(PSTStats.maceMasteryKills(), 1);
        refreshMaceMasteryTier(player, mainHandItem);
    }
    public static void onSkillLearned(Player player, Identifier learnedSkillId) {
        boolean isMaceMasterySkill = NODES.stream().map(MaceMasteryNode::skillId).anyMatch(learnedSkillId::equals);
        if (!isMaceMasterySkill) {
            return;
        }
        ItemStack mace = findMaceMasteryMace(player);
        if (mace.isEmpty()) {
            return;
        }
        refreshMaceMasteryTier(player, mace);
    }
    private static void refreshMaceMasteryTier(Player player, ItemStack mace) {
        MaceMasteryNode activeNode = getActiveNode(player, getKills(mace));
        if (activeNode != null) {
            applyMaceMasteryEnchantments(player, mace, activeNode, getCumulativeEffects(player, getKills(mace)));
        }
    }
    private static @Nullable MaceMasteryNode getActiveNode(Player player, int kills) {
        MaceMasteryNode active = null;
        for (MaceMasteryNode node : NODES) {
            if (kills >= node.requiredKills() && hasLearnedSkill(player, node.skillId())) {
                active = node;
            }
        }
        return active;
    }
    private static CumulativeEffects getCumulativeEffects(Player player, int kills) {
        int windBurst = 0, density = 0, breach = 0, fireAspect = 0;
        int haste = 0, speed = 0, fireResistance = 0, strength = 0, resistance = 0, regeneration = 0;
        boolean lightFoot = false, stun = false, dash = false, god = false;
        for (MaceMasteryNode node : NODES) {
            if (kills < node.requiredKills() || !hasLearnedSkill(player, node.skillId())) {
                continue;
            }
            windBurst = Math.max(windBurst, node.windBurstLevel());
            density = Math.max(density, node.densityLevel());
            breach = Math.max(breach, node.breachLevel());
            fireAspect = Math.max(fireAspect, node.fireAspectLevel());
            haste = Math.max(haste, node.hasteLevel());
            speed = Math.max(speed, node.speedLevel());
            fireResistance = Math.max(fireResistance, node.fireResistanceLevel());
            strength = Math.max(strength, node.strengthLevel());
            resistance = Math.max(resistance, node.resistanceLevel());
            regeneration = Math.max(regeneration, node.regenerationLevel());
            lightFoot |= node.lightFoot();
            stun |= node.stun();
            dash |= node.dash();
            god |= node.god();
        }
        return new CumulativeEffects(windBurst, density, breach, fireAspect, haste, speed, fireResistance,
                strength, resistance, regeneration, lightFoot, stun, dash, god);
    }
    private record CumulativeEffects(
            int windBurstLevel, int densityLevel, int breachLevel, int fireAspectLevel,
            int hasteLevel, int speedLevel, int fireResistanceLevel,
            int strengthLevel, int resistanceLevel, int regenerationLevel,
            boolean lightFoot, boolean stun, boolean dash, boolean god) {}
    public static @Nullable Identifier getActiveMaceMasteryNodeId(Player player) {
        MaceMasteryNode node = getActiveNode(player, getMaceMasteryKills(player));
        return node != null ? node.skillId() : null;
    }
    public static boolean hasLearnedSkill(Player player, Identifier skillId) {
        if (!PlayerSkillsProvider.hasSkills(player)) {
            return false;
        }
        return PlayerSkillsProvider.get(player).getPlayerSkills().stream()
                .map(PassiveSkill::getId)
                .anyMatch(skillId::equals);
    }
    private static void applyMaceMasteryEnchantments(Player player, ItemStack mace, MaceMasteryNode node, CumulativeEffects effects) {
        var enchantmentRegistry = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        ItemEnchantments current = mace.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(current);
        mutable.set(enchantmentRegistry.getOrThrow(Enchantments.WIND_BURST), effects.windBurstLevel());
        mutable.set(enchantmentRegistry.getOrThrow(Enchantments.DENSITY), effects.densityLevel());
        mutable.set(enchantmentRegistry.getOrThrow(Enchantments.BREACH), effects.breachLevel());
        if (effects.fireAspectLevel() > 0) {
            mutable.set(enchantmentRegistry.getOrThrow(Enchantments.FIRE_ASPECT), effects.fireAspectLevel());
        }
        mace.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
        applyMaceMasteryTint(mace, node);
        applyMaceMasteryName(mace, node);
    }
    private static void applyMaceMasteryName(ItemStack mace, MaceMasteryNode node) {
        int index = NODES.indexOf(node);
        int color = TIER_TINT_COLORS[index + 1];
        boolean isFinalNode = index == NODES.size() - 1;
        setMaceMasteryName(mace, color, isFinalNode ? "Netherite Master Mace" : "Master's Mace");
    }
    private static void setMaceMasteryName(ItemStack mace, int color, String name) {
        Style tierStyle = Style.EMPTY.withColor(TextColor.fromRgb(color)).withBold(true);
        Component coloredName = Component.literal(name).withStyle(tierStyle);
        mace.set(DataComponents.CUSTOM_NAME, coloredName);
    }
    private static final int[] TIER_TINT_COLORS = {
            0xFFFFFF,
            0xAAAAAA,
            0x55FF55,
            0x5599FF,
            0xAA44FF,
            0xFF4422,
            0xFFAA00,
            0xCC361B,
            0xA32B16,
            0xFFD700,
            0x832311,
            0x2F2B2C
    };
    public static int getTierTintColor(Identifier nodeSkillId) {
        for (int i = 0; i < NODES.size(); i++) {
            if (NODES.get(i).skillId().equals(nodeSkillId)) {
                return TIER_TINT_COLORS[i + 1];
            }
        }
        return TIER_TINT_COLORS[0];
    }
    private static void applyMaceMasteryTint(ItemStack mace, MaceMasteryNode node) {
        try {
            int color = TIER_TINT_COLORS[NODES.indexOf(node) + 1];
            CustomModelData customModelData = new CustomModelData(List.of(), List.of(), List.of(), List.of(color));
            mace.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
        } catch (Throwable t) {
            SkillTreeMod.LOGGER.error("[MaceMastery] ÉCHEC de l'écriture de la teinte pour le nœud {}", node.skillId(), t);
        }
    }
    private static void onServerTick(MinecraftServer server) {
        tickStunnedMobs();
        if (server.getTickCount() % EFFECT_REFRESH_INTERVAL_TICKS != 0) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ItemStack mace = findMaceMasteryMace(player);
            if (mace.isEmpty()) {
                continue;
            }
            CumulativeEffects effects = getCumulativeEffects(player, getKills(mace));
            applyPassiveEffect(player, MobEffects.HASTE, effects.hasteLevel());
            applyPassiveEffect(player, MobEffects.SPEED, effects.speedLevel());
            applyPassiveEffect(player, MobEffects.FIRE_RESISTANCE, effects.fireResistanceLevel());
            applyPassiveEffect(player, MobEffects.STRENGTH, effects.strengthLevel());
            applyPassiveEffect(player, MobEffects.RESISTANCE, effects.resistanceLevel());
            applyPassiveEffect(player, MobEffects.REGENERATION, effects.regenerationLevel());
        }
    }
    private static void applyPassiveEffect(ServerPlayer player, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect, int level) {
        if (level <= 0) {
            return;
        }
        player.addEffect(new MobEffectInstance(effect, EFFECT_DURATION_TICKS, level - 1, true, false, true));
    }
    private static boolean onAllowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (!(entity instanceof Player player)) {
            return true;
        }
        if (!source.is(DamageTypes.FALL)) {
            return true;
        }
        ItemStack mace = findMaceMasteryMace(player);
        if (mace.isEmpty()) {
            return true;
        }
        return !getCumulativeEffects(player, getKills(mace)).lightFoot();
    }
    private static void onAfterDamage(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken, boolean blocked) {
        if (!(source.getEntity() instanceof Player attacker)) {
            return;
        }
        ItemStack mainHandItem = attacker.getMainHandItem();
        if (!isMaceMasteryMace(mainHandItem)) {
            return;
        }
        if (!getCumulativeEffects(attacker, getKills(mainHandItem)).stun()) {
            return;
        }
        if (RANDOM.nextFloat() >= STUN_CHANCE) {
            return;
        }
        if (entity instanceof Mob mob) {
            stunMob(mob, STUN_DURATION_TICKS);
        }
    }
    private static void stunMob(Mob mob, int durationTicks) {
        if (mob.level().isClientSide()) {
            return;
        }
        long expiresAt = mob.level().getGameTime() + durationTicks;
        STUNNED_MOBS.put(mob, expiresAt);
        mob.setNoAi(true);
        mob.setDeltaMovement(Vec3.ZERO);
        mob.hurtMarked = true;
        mob.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, durationTicks, 6, false, true, true));
        mob.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, durationTicks, 3, false, true, true));
    }
    private static void tickStunnedMobs() {
        if (STUNNED_MOBS.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Mob, Long>> iterator = STUNNED_MOBS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Mob, Long> entry = iterator.next();
            Mob mob = entry.getKey();
            if (!mob.isAlive()) {
                iterator.remove();
                continue;
            }
            if (mob.level().getGameTime() >= entry.getValue()) {
                mob.setNoAi(false);
                iterator.remove();
            }
        }
    }
    private static InteractionResult onUseItem(Player player, Level level, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        ItemStack mace = player.getItemInHand(hand);
        if (!isMaceMasteryMace(mace)) {
            return InteractionResult.PASS;
        }
        CumulativeEffects effects = getCumulativeEffects(player, getKills(mace));
        if (!effects.god() && !effects.dash()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }
        if (effects.god() && player.isCrouching()) {
            return tryUseGod(player, level, mace);
        }
        if (effects.dash()) {
            return tryUseDash(player, mace);
        }
        return InteractionResult.PASS;
    }
    private static InteractionResult tryUseDash(Player player, ItemStack mace) {
        CompoundTag itemTag = getOrCreateCustomTag(mace);
        long now = player.level().getGameTime();
        long readyAt = itemTag.getLongOr(DASH_COOLDOWN_TAG_NAME, 0L);
        if (now < readyAt) {
            return InteractionResult.FAIL;
        }
        itemTag.putLong(DASH_COOLDOWN_TAG_NAME, now + DASH_COOLDOWN_TICKS);
        setCustomTag(mace, itemTag);
        var look = player.getLookAngle();
        player.setDeltaMovement(look.x * DASH_STRENGTH, Math.max(look.y * DASH_STRENGTH, 0.15), look.z * DASH_STRENGTH);
        player.hurtMarked = true; // force la sync de vélocité au client
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.2f);
        return InteractionResult.SUCCESS;
    }
    private static InteractionResult tryUseGod(Player player, Level level, ItemStack mace) {
        CompoundTag itemTag = getOrCreateCustomTag(mace);
        long now = level.getGameTime();
        long readyAt = itemTag.getLongOr(GOD_COOLDOWN_TAG_NAME, 0L);
        if (now < readyAt) {
            return InteractionResult.FAIL;
        }
        itemTag.putLong(GOD_COOLDOWN_TAG_NAME, now + GOD_COOLDOWN_TICKS);
        setCustomTag(mace, itemTag);
        player.getCooldowns().addCooldown(mace, GOD_COOLDOWN_TICKS);
        if (level instanceof ServerLevel serverLevel) {
            AABB area = player.getBoundingBox().inflate(GOD_RADIUS);
            List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, area,
                    e -> e != player && e.isAlive() && !(e instanceof Player));
            DamageSource lightningDamage = player.damageSources().source(DamageTypes.LIGHTNING_BOLT, player);
            for (LivingEntity nearby : targets) {
                nearby.invulnerableTime = 0;
                nearby.hurtServer(serverLevel, lightningDamage, GOD_DAMAGE);
                if (nearby instanceof Mob mob) {
                    stunMob(mob, GOD_STUN_DURATION_TICKS);
                }
                spawnVisualLightningStrike(serverLevel, nearby.position());
            }
            spawnVisualLightningStrike(serverLevel, player.position());
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.5f, 1.0f);
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.2f, 0.9f);
        }
        return InteractionResult.SUCCESS;
    }
    private static void spawnVisualLightningStrike(ServerLevel level, Vec3 pos) {
        EntityType<?> lightningBoltType = BuiltInRegistries.ENTITY_TYPE.get(LIGHTNING_BOLT_ID).map(Holder::value).orElse(null);
        if (lightningBoltType == null) {
            return;
        }
        Entity spawned = lightningBoltType.create(level, EntitySpawnReason.TRIGGERED);
        if (!(spawned instanceof LightningBolt bolt)) {
            return;
        }
        bolt.setPos(pos.x, pos.y, pos.z);
        bolt.setVisualOnly(true);
        level.addFreshEntity(bolt);
    }
    public static boolean isMaceMasteryMace(ItemStack itemStack) {
        if (!itemStack.is(Items.MACE)) {
            return false;
        }
        if (!itemStack.has(DataComponents.CUSTOM_DATA)) {
            return false;
        }
        CompoundTag itemTag = getOrCreateCustomTag(itemStack);
        return itemTag.getBooleanOr(MACE_MASTERY_TAG_NAME, false);
    }
    private static final Identifier MACE_MASTERY_ITEM_MODEL_ID =
            Identifier.fromNamespaceAndPath("skilltree", "mace_mastery_mace");
    public static void markAsMaceMasteryMace(ItemStack itemStack) {
        CompoundTag itemTag = getOrCreateCustomTag(itemStack);
        itemTag.putBoolean(MACE_MASTERY_TAG_NAME, true);
        itemTag.putInt(MACE_MASTERY_KILLS_TAG_NAME, 0);
        setCustomTag(itemStack, itemTag);
        itemStack.set(DataComponents.ITEM_MODEL, MACE_MASTERY_ITEM_MODEL_ID);
        setMaceMasteryName(itemStack, TIER_TINT_COLORS[0], "Master's Mace");
    }
    public static boolean hasMaceMasteryMace(Player player) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (isMaceMasteryMace(inventory.getItem(slot))) {
                return true;
            }
        }
        PlayerEnderChestContainer enderChestInventory = player.getEnderChestInventory();
        for (int slot = 0; slot < enderChestInventory.getContainerSize(); slot++) {
            if (isMaceMasteryMace(enderChestInventory.getItem(slot))) {
                return true;
            }
        }
        return false;
    }
    public static ItemStack findMaceMasteryMace(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (isMaceMasteryMace(mainHand)) {
            return mainHand;
        }
        ItemStack offHand = player.getOffhandItem();
        if (isMaceMasteryMace(offHand)) {
            return offHand;
        }
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (isMaceMasteryMace(stack)) {
                return stack;
            }
        }
        PlayerEnderChestContainer enderChestInventory = player.getEnderChestInventory();
        for (int slot = 0; slot < enderChestInventory.getContainerSize(); slot++) {
            ItemStack stack = enderChestInventory.getItem(slot);
            if (isMaceMasteryMace(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
    public static int getMaceMasteryKills(Player player) {
        return getKills(findMaceMasteryMace(player));
    }
    public static int getKills(ItemStack itemStack) {
        if (!itemStack.has(DataComponents.CUSTOM_DATA)) {
            return 0;
        }
        CompoundTag itemTag = getOrCreateCustomTag(itemStack);
        return itemTag.getIntOr(MACE_MASTERY_KILLS_TAG_NAME, 0);
    }
    private static void incrementKills(ItemStack itemStack) {
        CompoundTag itemTag = getOrCreateCustomTag(itemStack);
        int kills = itemTag.getIntOr(MACE_MASTERY_KILLS_TAG_NAME, 0) + 1;
        itemTag.putInt(MACE_MASTERY_KILLS_TAG_NAME, kills);
        setCustomTag(itemStack, itemTag);
    }
    private static CompoundTag getOrCreateCustomTag(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }
    private static void setCustomTag(ItemStack itemStack, CompoundTag tag) {
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}