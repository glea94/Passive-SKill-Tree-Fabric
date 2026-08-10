package daripher.skilltree.network;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectPredicate;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NetworkHelper {
    public static void writePassiveSkill(RegistryFriendlyByteBuf buf, PassiveSkill skill) {
        buf.writeResourceLocation(skill.getId());
        buf.writeInt(skill.getSkillSize());
        buf.writeResourceLocation(skill.getFrameTexture());
        buf.writeResourceLocation(skill.getIconTexture());
        buf.writeResourceLocation(skill.getTooltipFrameTexture());
        buf.writeBoolean(skill.isStartingPoint());
        buf.writeBoolean(skill.isAlwaysStartingPoint());
        buf.writeFloat(skill.getPositionX());
        buf.writeFloat(skill.getPositionY());
        buf.writeUtf(skill.getTitle());
        buf.writeUtf(skill.getTitleColor());
        writeResourceLocations(buf, skill.getDirectConnections());
        writeSkillBonuses(buf, skill.getBonuses());
        writeSkillRequirements(buf, skill.getRequirements());
        writeResourceLocations(buf, skill.getLongConnections());
        writeResourceLocations(buf, skill.getOneWayConnections());
        writeTags(buf, skill.getTags());
        writeDescription(buf, skill.getDescription());
    }

    public static PassiveSkill readPassiveSkill(RegistryFriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        int size = buf.readInt();
        ResourceLocation background = buf.readResourceLocation();
        ResourceLocation icon = buf.readResourceLocation();
        ResourceLocation border = buf.readResourceLocation();
        boolean startingPoint = buf.readBoolean();
        boolean alwaysStartingPoint = buf.readBoolean();
        PassiveSkill skill = new PassiveSkill(id, size, background, icon, border, startingPoint);
        skill.setAlwaysStartingPoint(alwaysStartingPoint);
        skill.setPosition(buf.readFloat(), buf.readFloat());
        skill.setTitle(buf.readUtf());
        skill.setTitleColor(buf.readUtf());
        skill.getDirectConnections().addAll(readResourceLocations(buf));
        skill.getBonuses().addAll(readSkillBonuses(buf));
        skill.getRequirements().addAll(readSkillRequirements(buf));
        skill.getLongConnections().addAll(readResourceLocations(buf));
        skill.getOneWayConnections().addAll(readResourceLocations(buf));
        skill.getTags().addAll(readTags(buf));
        skill.setDescription(readDescription(buf));
        return skill;
    }

    public static void writeAttribute(RegistryFriendlyByteBuf buf, Attribute attribute) {
        ResourceLocation attributeKey = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        Objects.requireNonNull(attributeKey);
        buf.writeResourceLocation(attributeKey);
    }

    public static @Nullable Attribute readAttribute(RegistryFriendlyByteBuf buf) {
        ResourceLocation attributeId = buf.readResourceLocation();
        // Factual Fix 1.21.4: Registry get() returns Optional<Holder.Reference<Attribute>>, unwrap with map()
        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeId)
                .map(Holder::value)
                .orElse(null);
        if (attribute == null) {
            SkillTreeMod.LOGGER.error("Attribute {} does not exist", attributeId);
        }
        return attribute;
    }

    public static void writeAttributeModifier(RegistryFriendlyByteBuf buf, AttributeModifier modifier) {
        buf.writeResourceLocation(modifier.id());
        buf.writeDouble(modifier.amount());
        writeOperation(buf, modifier.operation());
    }

    @NotNull
    public static AttributeModifier readAttributeModifier(RegistryFriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        double amount = buf.readDouble();
        AttributeModifier.Operation operation = readOperation(buf);
        return new AttributeModifier(id, amount, operation);
    }

    public static void writeResourceLocations(RegistryFriendlyByteBuf buf, List<ResourceLocation> locations) {
        buf.writeInt(locations.size());
        locations.forEach(buf::writeResourceLocation);
    }

    public static List<ResourceLocation> readResourceLocations(RegistryFriendlyByteBuf buf) {
        int count = buf.readInt();
        List<ResourceLocation> locations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            locations.add(buf.readResourceLocation());
        }
        return locations;
    }

    private static void writeTags(RegistryFriendlyByteBuf buf, List<String> tags) {
        buf.writeInt(tags.size());
        for (String tag : tags) {
            buf.writeUtf(tag);
        }
    }

    private static List<String> readTags(RegistryFriendlyByteBuf buf) {
        List<String> tags = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            // Factual Fix 1.21.4: Correct method call to parse data from the byte buffer is readUtf()
            tags.add(buf.readUtf());
        }
        return tags;
    }

    public static void writeSkillBonuses(RegistryFriendlyByteBuf buf, List<SkillBonus<?>> bonuses) {
        buf.writeInt(bonuses.size());
        bonuses.forEach(bonus -> writeSkillBonus(buf, bonus));
    }

    public static List<SkillBonus<?>> readSkillBonuses(RegistryFriendlyByteBuf buf) {
        int count = buf.readInt();
        List<SkillBonus<?>> bonuses = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            bonuses.add(readSkillBonus(buf));
        }
        return bonuses;
    }

    public static void writeSkillRequirements(RegistryFriendlyByteBuf buf, List<SkillRequirement<?>> requirements) {
        buf.writeInt(requirements.size());
        requirements.forEach(requirement -> writeSkillRequirement(buf, requirement));
    }

    public static List<SkillRequirement<?>> readSkillRequirements(RegistryFriendlyByteBuf buf) {
        int count = buf.readInt();
        List<SkillRequirement<?>> requirements = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            requirements.add(readSkillRequirement(buf));
        }
        return requirements;
    }
    public static void writePassiveSkills(RegistryFriendlyByteBuf buf, Collection<PassiveSkill> skills) {
        buf.writeInt(skills.size());
        skills.forEach(skill -> writePassiveSkill(buf, skill));
    }

    public static List<PassiveSkill> readPassiveSkills(RegistryFriendlyByteBuf buf) {
        int count = buf.readInt();
        List<PassiveSkill> skills = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            skills.add(readPassiveSkill(buf));
        }
        return skills;
    }

    public static void writeSkillBonus(RegistryFriendlyByteBuf buf, SkillBonus<?> bonus) {
        SkillBonus.Serializer serializer = bonus.getSerializer();
        ResourceLocation serializerId = PSTRegistries.SKILL_BONUSES.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        buf.writeResourceLocation(serializerId);
        serializer.serialize(buf, bonus);
    }

    public static SkillBonus<?> readSkillBonus(RegistryFriendlyByteBuf buf) {
        ResourceLocation serializerId = buf.readResourceLocation();
        SkillBonus.Serializer serializer = PSTRegistries.SKILL_BONUSES.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return serializer.deserialize(buf);
    }

    public static void writeSkillRequirement(RegistryFriendlyByteBuf buf, SkillRequirement<?> requirement) {
        SkillRequirement.Serializer serializer = requirement.getSerializer();
        ResourceLocation serializerId = PSTRegistries.SKILL_REQUIREMENTS.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        buf.writeResourceLocation(serializerId);
        serializer.serialize(buf, requirement);
    }

    public static SkillRequirement<?> readSkillRequirement(RegistryFriendlyByteBuf buf) {
        ResourceLocation serializerId = buf.readResourceLocation();
        SkillRequirement.Serializer serializer = PSTRegistries.SKILL_REQUIREMENTS.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return serializer.deserialize(buf);
    }

    public static void writeDescription(RegistryFriendlyByteBuf buf, @Nullable List<MutableComponent> description) {
        buf.writeBoolean(description != null);
        if (description == null) {
            return;
        }
        buf.writeInt(description.size());
        for (MutableComponent component : description) {
            writeChatComponent(buf, component);
        }
    }

    public static @Nullable List<MutableComponent> readDescription(RegistryFriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return null;
        }
        int size = buf.readInt();
        List<MutableComponent> description = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            description.add(readChatComponent(buf));
        }
        return description;
    }

    public static void writeChatComponent(RegistryFriendlyByteBuf buf, MutableComponent component) {
        // Factual Fix 1.21.4: Use modern ComponentSerialization stream codec layout targeting registry friendly buffers
        net.minecraft.network.chat.ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, component);
    }

    public static MutableComponent readChatComponent(RegistryFriendlyByteBuf buf) {
        // Factual Fix 1.21.4: Use modern ComponentSerialization stream codec layout targeting registry friendly buffers
        return (MutableComponent) net.minecraft.network.chat.ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buf);
    }

    public static void writePassiveSkillTrees(RegistryFriendlyByteBuf buf, Collection<PassiveSkillTree> skillTrees) {
        buf.writeInt(skillTrees.size());
        skillTrees.forEach(skillTree -> writePassiveSkillTree(buf, skillTree));
    }

    public static List<PassiveSkillTree> readPassiveSkillTrees(RegistryFriendlyByteBuf buf) {
        int count = buf.readInt();
        List<PassiveSkillTree> skillTrees = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            skillTrees.add(readPassiveSkillTree(buf));
        }
        return skillTrees;
    }

    public static void writePassiveSkillTree(RegistryFriendlyByteBuf buf, PassiveSkillTree skillTree) {
        buf.writeResourceLocation(skillTree.getId());
        writeResourceLocations(buf, skillTree.getSkillIds());
        writeTagLimits(buf, skillTree.getSkillLimitations());
    }

    public static PassiveSkillTree readPassiveSkillTree(RegistryFriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        PassiveSkillTree skillTree = new PassiveSkillTree(id);
        readResourceLocations(buf).forEach(skillTree.getSkillIds()::add);
        readTagLimits(buf).forEach(skillTree.getSkillLimitations()::put);
        return skillTree;
    }

    private static void writeTagLimits(RegistryFriendlyByteBuf buf, Map<String, Integer> limits) {
        buf.writeInt(limits.size());
        for (Map.Entry<String, Integer> entry : limits.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeInt(entry.getValue());
        }
    }

    private static Map<String, Integer> readTagLimits(RegistryFriendlyByteBuf buf) {
        Map<String, Integer> limits = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            limits.put(buf.readUtf(), buf.readInt());
        }
        return limits;
    }

    public static void writeLivingMultiplier(RegistryFriendlyByteBuf buf, @NotNull LivingMultiplier multiplier) {
        LivingMultiplier.Serializer serializer = multiplier.getSerializer();
        ResourceLocation serializerId = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(serializer);
        buf.writeResourceLocation(Objects.requireNonNull(serializerId));
        serializer.serialize(buf, multiplier);
    }
    public static @NotNull LivingMultiplier readLivingMultiplier(RegistryFriendlyByteBuf buf) {
        ResourceLocation serializerId = buf.readResourceLocation();
        LivingMultiplier.Serializer serializer = PSTRegistries.LIVING_MULTIPLIERS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeLivingCondition(RegistryFriendlyByteBuf buf, @NotNull LivingEntityPredicate condition) {
        LivingEntityPredicate.Serializer serializer = condition.getSerializer();
        ResourceLocation serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey(serializer);
        buf.writeResourceLocation(Objects.requireNonNull(serializerId));
        serializer.serialize(buf, condition);
    }

    public static @NotNull LivingEntityPredicate readLivingCondition(RegistryFriendlyByteBuf buf) {
        ResourceLocation serializerId = buf.readResourceLocation();
        LivingEntityPredicate.Serializer serializer = PSTRegistries.LIVING_CONDITIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeMobEffectCondition(RegistryFriendlyByteBuf buf, @NotNull MobEffectPredicate condition) {
        MobEffectPredicate.Serializer serializer = condition.getSerializer();
        ResourceLocation serializerId = PSTRegistries.MOB_EFFECT_PREDICATES.get().getKey(serializer);
        buf.writeResourceLocation(Objects.requireNonNull(serializerId));
        serializer.serialize(buf, condition);
    }

    public static @NotNull MobEffectPredicate readMobEffectCondition(RegistryFriendlyByteBuf buf) {
        ResourceLocation serializerId = buf.readResourceLocation();
        MobEffectPredicate.Serializer serializer = PSTRegistries.MOB_EFFECT_PREDICATES.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeDamageCondition(RegistryFriendlyByteBuf buf, @NotNull DamageCondition condition) {
        DamageCondition.Serializer serializer = condition.getSerializer();
        ResourceLocation serializerId = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        buf.writeResourceLocation(serializerId);
        serializer.serialize(buf, condition);
    }

    public static @NotNull DamageCondition readDamageCondition(RegistryFriendlyByteBuf buf) {
        ResourceLocation serializerId = buf.readResourceLocation();
        DamageCondition.Serializer serializer = PSTRegistries.DAMAGE_CONDITIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeItemPredicate(RegistryFriendlyByteBuf buf, @NotNull ItemStackPredicate condition) {
        ItemStackPredicate.Serializer serializer = condition.getSerializer();
        ResourceLocation serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey(serializer);
        buf.writeResourceLocation(Objects.requireNonNull(serializerId));
        serializer.serialize(buf, condition);
    }

    public static @NotNull ItemStackPredicate readItemPredicate(RegistryFriendlyByteBuf buf) {
        ResourceLocation serializerId = buf.readResourceLocation();
        ItemStackPredicate.Serializer serializer = PSTRegistries.ITEM_CONDITIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeEventListener(RegistryFriendlyByteBuf buf, @NotNull SkillEventListener condition) {
        SkillEventListener.Serializer serializer = condition.getSerializer();
        ResourceLocation serializerId = PSTRegistries.EVENT_LISTENERS.get().getKey(serializer);
        buf.writeResourceLocation(Objects.requireNonNull(serializerId));
        serializer.serialize(buf, condition);
    }

    public static @NotNull SkillEventListener readEventListener(RegistryFriendlyByteBuf buf) {
        ResourceLocation serializerId = buf.readResourceLocation();
        SkillEventListener.Serializer serializer = PSTRegistries.EVENT_LISTENERS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeMobEffect(RegistryFriendlyByteBuf buf, MobEffect effect) {
        ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(effect);
        buf.writeResourceLocation(Objects.requireNonNull(effectId));
    }

    public static @Nullable MobEffect readMobEffect(RegistryFriendlyByteBuf buf) {
        ResourceLocation effectId = buf.readResourceLocation();
        // Factual Fix 1.21.4: Registry get() returns Optional<Holder.Reference<MobEffect>>, unwrap with map()
        return BuiltInRegistries.MOB_EFFECT.get(effectId)
                .map(net.minecraft.core.Holder::value)
                .orElse(null);
    }

    public static <T extends Enum<T>> void writeEnum(RegistryFriendlyByteBuf buf, T anEnum) {
        buf.writeInt(anEnum.ordinal());
    }

    public static <T extends Enum<T>> @Nullable T readEnum(RegistryFriendlyByteBuf buf, Class<T> type) {
        return type.getEnumConstants()[(buf.readInt())];
    }

    public static void writeOperation(RegistryFriendlyByteBuf buf, AttributeModifier.Operation operation) {
        AttributeModifier.Operation.STREAM_CODEC.encode(buf, operation);
    }
    @NotNull
    public static AttributeModifier.Operation readOperation(RegistryFriendlyByteBuf buf) {
        return AttributeModifier.Operation.STREAM_CODEC.decode(buf);
    }

    public static void writeEffectInstance(RegistryFriendlyByteBuf buf, MobEffectInstance effect) {
        writeMobEffect(buf, effect.getEffect().value());
        buf.writeInt(effect.getDuration());
        buf.writeInt(effect.getAmplifier());
    }

    @NotNull
    public static MobEffectInstance readEffectInstance(RegistryFriendlyByteBuf buf) {
        MobEffect effect = readMobEffect(buf);
        Objects.requireNonNull(effect);
        // Uses modern standard native buffer primitives matching the write pass sequence
        return new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), buf.readInt(), buf.readInt());
    }

    public static void writeValueProvider(RegistryFriendlyByteBuf buf, FloatFunction<?> provider) {
        FloatFunction.Serializer serializer = provider.getSerializer();
        ResourceLocation serializerId = PSTRegistries.FLOAT_FUNCTIONS.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        buf.writeResourceLocation(serializerId);
        serializer.serialize(buf, provider);
    }

    public static FloatFunction<?> readValueProvider(RegistryFriendlyByteBuf buf) {
        ResourceLocation serializerId = buf.readResourceLocation();
        FloatFunction.Serializer serializer = PSTRegistries.FLOAT_FUNCTIONS.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return serializer.deserialize(buf);
    }

    public static void writeItemBonus(RegistryFriendlyByteBuf buf, ItemBonus<?> itemBonus) {
        ItemBonus.Serializer serializer = itemBonus.getSerializer();
        ResourceLocation serializerId = PSTRegistries.ITEM_BONUSES.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        buf.writeResourceLocation(serializerId);
        serializer.serialize(buf, itemBonus);
    }

    public static ItemBonus<?> readItemBonus(RegistryFriendlyByteBuf buf) {
        ResourceLocation serializerId = buf.readResourceLocation();
        ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return serializer.deserialize(buf);
    }
}