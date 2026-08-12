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
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NetworkHelper {
<<<<<<< Updated upstream
    public static void writePassiveSkill(FriendlyByteBuf buf, PassiveSkill skill) {
        buf.writeUtf(skill.getId().toString());
        buf.writeInt(skill.getSkillSize());
        buf.writeUtf(skill.getFrameTexture().toString());
        buf.writeUtf(skill.getIconTexture().toString());
        buf.writeUtf(skill.getTooltipFrameTexture().toString());
=======
    public static void writePassiveSkill(RegistryFriendlyByteBuf buf, PassiveSkill skill) {
        buf.writeIdentifier(skill.getId());
        buf.writeInt(skill.getSkillSize());
        buf.writeIdentifier(skill.getFrameTexture());
        buf.writeIdentifier(skill.getIconTexture());
        buf.writeIdentifier(skill.getTooltipFrameTexture());
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
    public static PassiveSkill readPassiveSkill(FriendlyByteBuf buf) {
        ResourceLocation id = new ResourceLocation(buf.readUtf());
        int size = buf.readInt();
        ResourceLocation background = new ResourceLocation(buf.readUtf());
        ResourceLocation icon = new ResourceLocation(buf.readUtf());
        ResourceLocation border = new ResourceLocation(buf.readUtf());
=======
    public static PassiveSkill readPassiveSkill(RegistryFriendlyByteBuf buf) {
        Identifier id = buf.readIdentifier();
        int size = buf.readInt();
        Identifier background = buf.readIdentifier();
        Identifier icon = buf.readIdentifier();
        Identifier border = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
    public static void writeAttribute(FriendlyByteBuf buf, Attribute attribute) {
        String attributeId = Objects.requireNonNull(BuiltInRegistries.ATTRIBUTE.getKey(attribute)).toString();
        buf.writeUtf(attributeId);
    }

    public static @Nullable Attribute readAttribute(FriendlyByteBuf buf) {
        String attributeId = buf.readUtf();
        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(attributeId));
=======
    public static void writeAttribute(RegistryFriendlyByteBuf buf, Attribute attribute) {
        Identifier attributeKey = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        Objects.requireNonNull(attributeKey);
        buf.writeIdentifier(attributeKey);
    }

    public static @Nullable Attribute readAttribute(RegistryFriendlyByteBuf buf) {
        Identifier attributeId = buf.readIdentifier();
        // Factual Fix 1.21.4: Registry get() returns Optional<Holder.Reference<Attribute>>, unwrap with map()
        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeId)
                .map(Holder::value)
                .orElse(null);
>>>>>>> Stashed changes
        if (attribute == null) {
            SkillTreeMod.LOGGER.error("Attribute {} does not exist", attributeId);
        }
        return attribute;
    }

<<<<<<< Updated upstream
    public static void writeAttributeModifier(FriendlyByteBuf buf, AttributeModifier modifier) {
        buf.writeLong(modifier.getId().getMostSignificantBits());
        buf.writeLong(modifier.getId().getLeastSignificantBits());
        buf.writeUtf(modifier.getName());
        buf.writeDouble(modifier.getAmount());
        writeOperation(buf, modifier.getOperation());
    }

    @NotNull
    public static AttributeModifier readAttributeModifier(FriendlyByteBuf buf) {
        UUID id = new UUID(buf.readLong(), buf.readLong());
        String name = buf.readUtf();
=======
    public static void writeAttributeModifier(RegistryFriendlyByteBuf buf, AttributeModifier modifier) {
        buf.writeIdentifier(modifier.id());
        buf.writeDouble(modifier.amount());
        writeOperation(buf, modifier.operation());
    }

    @NotNull
    public static AttributeModifier readAttributeModifier(RegistryFriendlyByteBuf buf) {
        Identifier id = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        double amount = buf.readDouble();
        AttributeModifier.Operation operation = readOperation(buf);
        return new AttributeModifier(id, amount, operation);
    }

<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public static void writeResourceLocations(FriendlyByteBuf buf, List<ResourceLocation> locations) {
        buf.writeInt(locations.size());
        locations.forEach(location -> buf.writeUtf(location.toString()));
    }

    public static List<ResourceLocation> readResourceLocations(FriendlyByteBuf buf) {
=======
=======
>>>>>>> Stashed changes
    public static void writeResourceLocations(RegistryFriendlyByteBuf buf, List<Identifier> locations) {
        buf.writeInt(locations.size());
        locations.forEach(buf::writeIdentifier);
    }

    public static List<Identifier> readResourceLocations(RegistryFriendlyByteBuf buf) {
<<<<<<< Updated upstream
>>>>>>> Stashed changes
        int count = buf.readInt();
        List<Identifier> locations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
<<<<<<< Updated upstream
            locations.add(new ResourceLocation(buf.readUtf()));
=======
            locations.add(buf.readIdentifier());
>>>>>>> Stashed changes
=======
        int count = buf.readInt();
        List<Identifier> locations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            locations.add(buf.readIdentifier());
>>>>>>> Stashed changes
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
        Identifier serializerId = PSTRegistries.SKILL_BONUSES.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        buf.writeUtf(serializerId.toString());
        serializer.serialize(buf, bonus);
    }

    public static SkillBonus<?> readSkillBonus(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
=======
=======
>>>>>>> Stashed changes
        buf.writeIdentifier(serializerId);
        serializer.serialize(buf, bonus);
    }

    public static SkillBonus<?> readSkillBonus(RegistryFriendlyByteBuf buf) {
        Identifier serializerId = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        SkillBonus.Serializer serializer = PSTRegistries.SKILL_BONUSES.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return serializer.deserialize(buf);
    }

    public static void writeSkillRequirement(RegistryFriendlyByteBuf buf, SkillRequirement<?> requirement) {
        SkillRequirement.Serializer serializer = requirement.getSerializer();
        Identifier serializerId = PSTRegistries.SKILL_REQUIREMENTS.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        buf.writeUtf(serializerId.toString());
        serializer.serialize(buf, requirement);
    }

    public static SkillRequirement<?> readSkillRequirement(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
=======
=======
>>>>>>> Stashed changes
        buf.writeIdentifier(serializerId);
        serializer.serialize(buf, requirement);
    }

    public static SkillRequirement<?> readSkillRequirement(RegistryFriendlyByteBuf buf) {
        Identifier serializerId = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
    public static void writePassiveSkillTree(FriendlyByteBuf buf, PassiveSkillTree skillTree) {
        buf.writeUtf(skillTree.getId().toString());
=======
    public static void writePassiveSkillTree(RegistryFriendlyByteBuf buf, PassiveSkillTree skillTree) {
        buf.writeIdentifier(skillTree.getId());
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        writeResourceLocations(buf, skillTree.getSkillIds());
        writeTagLimits(buf, skillTree.getSkillLimitations());
    }

<<<<<<< Updated upstream
    public static PassiveSkillTree readPassiveSkillTree(FriendlyByteBuf buf) {
        ResourceLocation id = new ResourceLocation(buf.readUtf());
=======
    public static PassiveSkillTree readPassiveSkillTree(RegistryFriendlyByteBuf buf) {
        Identifier id = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        ResourceLocation serializerId = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(serializer);
        buf.writeUtf(Objects.requireNonNull(serializerId).toString());
        serializer.serialize(buf, multiplier);
    }

    public static @NotNull LivingMultiplier readLivingMultiplier(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
=======
=======
>>>>>>> Stashed changes
        Identifier serializerId = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(serializer);
        buf.writeIdentifier(Objects.requireNonNull(serializerId));
        serializer.serialize(buf, multiplier);
    }
    public static @NotNull LivingMultiplier readLivingMultiplier(RegistryFriendlyByteBuf buf) {
        Identifier serializerId = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        LivingMultiplier.Serializer serializer = PSTRegistries.LIVING_MULTIPLIERS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeLivingCondition(RegistryFriendlyByteBuf buf, @NotNull LivingEntityPredicate condition) {
        LivingEntityPredicate.Serializer serializer = condition.getSerializer();
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        ResourceLocation serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey(serializer);
        buf.writeUtf(Objects.requireNonNull(serializerId).toString());
        serializer.serialize(buf, condition);
    }

    public static @NotNull LivingEntityPredicate readLivingCondition(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
=======
=======
>>>>>>> Stashed changes
        Identifier serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey(serializer);
        buf.writeIdentifier(Objects.requireNonNull(serializerId));
        serializer.serialize(buf, condition);
    }

    public static @NotNull LivingEntityPredicate readLivingCondition(RegistryFriendlyByteBuf buf) {
        Identifier serializerId = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        LivingEntityPredicate.Serializer serializer = PSTRegistries.LIVING_CONDITIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeMobEffectCondition(RegistryFriendlyByteBuf buf, @NotNull MobEffectPredicate condition) {
        MobEffectPredicate.Serializer serializer = condition.getSerializer();
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        ResourceLocation serializerId = PSTRegistries.MOB_EFFECT_PREDICATES.get().getKey(serializer);
        buf.writeUtf(Objects.requireNonNull(serializerId).toString());
        serializer.serialize(buf, condition);
    }

    public static @NotNull MobEffectPredicate readMobEffectCondition(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
=======
=======
>>>>>>> Stashed changes
        Identifier serializerId = PSTRegistries.MOB_EFFECT_PREDICATES.get().getKey(serializer);
        buf.writeIdentifier(Objects.requireNonNull(serializerId));
        serializer.serialize(buf, condition);
    }

    public static @NotNull MobEffectPredicate readMobEffectCondition(RegistryFriendlyByteBuf buf) {
        Identifier serializerId = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        MobEffectPredicate.Serializer serializer = PSTRegistries.MOB_EFFECT_PREDICATES.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeDamageCondition(RegistryFriendlyByteBuf buf, @NotNull DamageCondition condition) {
        DamageCondition.Serializer serializer = condition.getSerializer();
        Identifier serializerId = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        buf.writeUtf(serializerId.toString());
        serializer.serialize(buf, condition);
    }

    public static @NotNull DamageCondition readDamageCondition(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
=======
=======
>>>>>>> Stashed changes
        buf.writeIdentifier(serializerId);
        serializer.serialize(buf, condition);
    }

    public static @NotNull DamageCondition readDamageCondition(RegistryFriendlyByteBuf buf) {
        Identifier serializerId = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        DamageCondition.Serializer serializer = PSTRegistries.DAMAGE_CONDITIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeItemPredicate(RegistryFriendlyByteBuf buf, @NotNull ItemStackPredicate condition) {
        ItemStackPredicate.Serializer serializer = condition.getSerializer();
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        ResourceLocation serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey(serializer);
        buf.writeUtf(Objects.requireNonNull(serializerId).toString());
        serializer.serialize(buf, condition);
    }

    public static @NotNull ItemStackPredicate readItemPredicate(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
=======
=======
>>>>>>> Stashed changes
        Identifier serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey(serializer);
        buf.writeIdentifier(Objects.requireNonNull(serializerId));
        serializer.serialize(buf, condition);
    }

    public static @NotNull ItemStackPredicate readItemPredicate(RegistryFriendlyByteBuf buf) {
        Identifier serializerId = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        ItemStackPredicate.Serializer serializer = PSTRegistries.ITEM_CONDITIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeEventListener(RegistryFriendlyByteBuf buf, @NotNull SkillEventListener condition) {
        SkillEventListener.Serializer serializer = condition.getSerializer();
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        ResourceLocation serializerId = PSTRegistries.EVENT_LISTENERS.get().getKey(serializer);
        buf.writeUtf(Objects.requireNonNull(serializerId).toString());
        serializer.serialize(buf, condition);
    }

    public static @NotNull SkillEventListener readEventListener(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
=======
=======
>>>>>>> Stashed changes
        Identifier serializerId = PSTRegistries.EVENT_LISTENERS.get().getKey(serializer);
        buf.writeIdentifier(Objects.requireNonNull(serializerId));
        serializer.serialize(buf, condition);
    }

    public static @NotNull SkillEventListener readEventListener(RegistryFriendlyByteBuf buf) {
        Identifier serializerId = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        SkillEventListener.Serializer serializer = PSTRegistries.EVENT_LISTENERS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(buf);
    }

<<<<<<< Updated upstream
    public static void writeMobEffect(FriendlyByteBuf buf, MobEffect effect) {
        ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(effect);
        buf.writeUtf(Objects.requireNonNull(effectId).toString());
    }

    public static @Nullable MobEffect readMobEffect(FriendlyByteBuf buf) {
        ResourceLocation effectId = new ResourceLocation(buf.readUtf());
        return BuiltInRegistries.MOB_EFFECT.get(effectId);
    }

    public static <T extends Enum<T>> void writeEnum(FriendlyByteBuf buf, T anEnum) {
=======
    public static void writeMobEffect(RegistryFriendlyByteBuf buf, MobEffect effect) {
        Identifier effectId = BuiltInRegistries.MOB_EFFECT.getKey(effect);
        buf.writeIdentifier(Objects.requireNonNull(effectId));
    }

    public static @Nullable MobEffect readMobEffect(RegistryFriendlyByteBuf buf) {
        Identifier effectId = buf.readIdentifier();
        // Factual Fix 1.21.4: Registry get() returns Optional<Holder.Reference<MobEffect>>, unwrap with map()
        return BuiltInRegistries.MOB_EFFECT.get(effectId)
                .map(net.minecraft.core.Holder::value)
                .orElse(null);
    }

    public static <T extends Enum<T>> void writeEnum(RegistryFriendlyByteBuf buf, T anEnum) {
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
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
        Identifier serializerId = PSTRegistries.FLOAT_FUNCTIONS.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        buf.writeUtf(serializerId.toString());
        serializer.serialize(buf, provider);
    }

    public static FloatFunction<?> readValueProvider(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
=======
=======
>>>>>>> Stashed changes
        buf.writeIdentifier(serializerId);
        serializer.serialize(buf, provider);
    }

    public static FloatFunction<?> readValueProvider(RegistryFriendlyByteBuf buf) {
        Identifier serializerId = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        FloatFunction.Serializer serializer = PSTRegistries.FLOAT_FUNCTIONS.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return serializer.deserialize(buf);
    }

    public static void writeItemBonus(RegistryFriendlyByteBuf buf, ItemBonus<?> itemBonus) {
        ItemBonus.Serializer serializer = itemBonus.getSerializer();
        Identifier serializerId = PSTRegistries.ITEM_BONUSES.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        buf.writeUtf(serializerId.toString());
        serializer.serialize(buf, itemBonus);
    }

    public static ItemBonus<?> readItemBonus(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.readUtf());
=======
=======
>>>>>>> Stashed changes
        buf.writeIdentifier(serializerId);
        serializer.serialize(buf, itemBonus);
    }

    public static ItemBonus<?> readItemBonus(RegistryFriendlyByteBuf buf) {
        Identifier serializerId = buf.readIdentifier();
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return serializer.deserialize(buf);
    }
}