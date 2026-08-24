package daripher.skilltree.data.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.skill.bonus.predicate.damage.NoneDamageCondition;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectPredicate;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectType;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectTypePredicate;
import daripher.skilltree.skill.bonus.predicate.effect.NoneMobEffectPredicate;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.item.NoneItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.item.PotionStackPredicate;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.NoneLivingEntityPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.UUID;

public class SerializationHelper {
    @NotNull
    public static Attribute deserializeAttribute(JsonObject json) {
        Identifier attributeId = Identifier.parse(json.get("attribute").getAsString());
        
        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeId)
                .map(net.minecraft.core.Holder::value)
                .orElse(null);
        if (attribute == null) {
            throw new RuntimeException("Attribute " + attributeId + " doesn't exist!");
        }
        return attribute;
    }

    public static void serializeAttribute(JsonObject json, Attribute attribute) {
        
        Identifier attributeId = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        Objects.requireNonNull(attributeId);
        json.addProperty("attribute", attributeId.toString());
    }

    @NotNull
    public static AttributeModifier deserializeAttributeModifier(JsonObject json) {
        Identifier id = Identifier.parse(json.get("id").getAsString());
        double amount = json.get("amount").getAsDouble();
        AttributeModifier.Operation operation = deserializeOperation(json);
        return new AttributeModifier(id, amount, operation);
    }

    public static void serializeAttributeModifier(JsonObject json, AttributeModifier modifier) {
        json.addProperty("id", modifier.id().toString());
        json.addProperty("amount", modifier.amount());
        serializeOperation(json, modifier.operation());
    }

    @NotNull
    public static AttributeModifier.Operation deserializeOperation(JsonObject json) {
        String opName = json.get("operation").getAsString().toUpperCase();
        try {
            return AttributeModifier.Operation.valueOf(opName);
        } catch (IllegalArgumentException e) {
            return AttributeModifier.Operation.ADD_VALUE;
        }
    }

    public static void serializeOperation(JsonObject json, AttributeModifier.Operation operation) {
        json.addProperty("operation", operation.name().toLowerCase());
    }

    public static @NotNull LivingMultiplier deserializeLivingMultiplier(JsonObject json, String name) {
        if (!json.has(name)) {
            return NoneLivingMultiplier.INSTANCE;
        }
        JsonObject multiplierJson = json.getAsJsonObject(name);
        Identifier serializerId = Identifier.parse(multiplierJson.get("type").getAsString());
        LivingMultiplier.Serializer serializer = PSTRegistries.LIVING_MULTIPLIERS.get().getValue(serializerId);
        String errorMessage = "Unknown living multiplier: " + serializerId;
        return deserializeObject(serializer, multiplierJson, errorMessage);
    }

    public static void serializeLivingMultiplier(JsonObject json, @NotNull LivingMultiplier multiplier, String name) {
        if (multiplier == NoneLivingMultiplier.INSTANCE) {
            return;
        }
        JsonObject multiplierJson = new JsonObject();
        LivingMultiplier.Serializer serializer = multiplier.getSerializer();
        serializer.serialize(multiplierJson, multiplier);
        Identifier serializerId = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        multiplierJson.addProperty("type", serializerId.toString());
        json.add(name, multiplierJson);
    }

    public static @NotNull LivingEntityPredicate deserializeLivingCondition(JsonObject json, String name) {
        if (!json.has(name)) {
            return NoneLivingEntityPredicate.INSTANCE;
        }
        JsonObject conditionJson = json.getAsJsonObject(name);
        Identifier serializerId = Identifier.parse(conditionJson.get("type").getAsString());
        LivingEntityPredicate.Serializer serializer = PSTRegistries.LIVING_CONDITIONS.get().getValue(serializerId);
        String errorMessage = "Unknown living condition: " + serializerId;
        return deserializeObject(serializer, conditionJson, errorMessage);
    }

    public static void serializeLivingCondition(JsonObject json, @NotNull LivingEntityPredicate condition, String name) {
        if (condition == NoneLivingEntityPredicate.INSTANCE) {
            return;
        }
        JsonObject conditionJson = new JsonObject();
        LivingEntityPredicate.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        Identifier serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        conditionJson.addProperty("type", serializerId.toString());
        json.add(name, conditionJson);
    }

    public static @NotNull MobEffectPredicate deserializeMobEffectCondition(JsonObject json, String name) {
        if (!json.has(name)) {
            if (json.has("effect_type")) {
                MobEffectType effectType = MobEffectType.fromName(json.get("effect_type").getAsString());
                return new MobEffectTypePredicate(effectType);
            }
            return NoneMobEffectPredicate.INSTANCE;
        }
        JsonObject conditionJson = json.getAsJsonObject(name);
        Identifier serializerId = Identifier.parse(conditionJson.get("type").getAsString());
        MobEffectPredicate.Serializer serializer = PSTRegistries.MOB_EFFECT_PREDICATES.get().getValue(serializerId);
        String errorMessage = "Unknown mob effect condition: " + serializerId;
        return deserializeObject(serializer, conditionJson, errorMessage);
    }

    public static void serializeMobEffectCondition(JsonObject json, @NotNull MobEffectPredicate condition, String name) {
        if (condition == NoneMobEffectPredicate.INSTANCE) {
            return;
        }
        JsonObject conditionJson = new JsonObject();
        MobEffectPredicate.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        Identifier serializerId = PSTRegistries.MOB_EFFECT_PREDICATES.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        conditionJson.addProperty("type", serializerId.toString());
        json.add(name, conditionJson);
    }

    @NotNull
    public static DamageCondition deserializeDamageCondition(JsonObject json) {
        return deserializeDamageCondition(json, "damage_condition");
    }

    @NotNull
    public static DamageCondition deserializeDamageCondition(JsonObject json, String name) {
        if (!json.has(name)) {
            return NoneDamageCondition.INSTANCE;
        }
        JsonObject conditionJson = json.getAsJsonObject(name);
        Identifier serializerId = Identifier.parse(conditionJson.get("type").getAsString());
        DamageCondition.Serializer serializer = PSTRegistries.DAMAGE_CONDITIONS.get().getValue(serializerId);
        String errorMessage = "Unknown damage condition: " + serializerId;
        return deserializeObject(serializer, conditionJson, errorMessage);
    }

    public static void serializeDamageCondition(JsonObject json, @NotNull DamageCondition condition) {
        serializeDamageCondition(json, condition, "damage_condition");
    }

    public static void serializeDamageCondition(JsonObject json, @NotNull DamageCondition condition, String name) {
        JsonObject conditionJson = new JsonObject();
        DamageCondition.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        Identifier serializerId = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(serializer);
        conditionJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
        json.add(name, conditionJson);
    }

    public static @NotNull ItemStackPredicate deserializeItemPredicate(JsonObject json) {
        return deserializeItemPredicate(json, "item_condition");
    }

    public static @NotNull ItemStackPredicate deserializeItemPredicate(JsonObject json, String name) {
        if (!json.has(name)) {
            return NoneItemStackPredicate.INSTANCE;
        }
        JsonObject conditionJson = json.getAsJsonObject(name);
        Identifier serializerId = Identifier.parse(conditionJson.get("type").getAsString());
        ItemStackPredicate.Serializer serializer = PSTRegistries.ITEM_CONDITIONS.get().getValue(serializerId);
        String errorMessage = "Unknown item condition: " + serializerId;
        return deserializeObject(serializer, conditionJson, errorMessage);
    }

    public static void serializeItemPredicate(JsonObject json, @NotNull ItemStackPredicate condition) {
        serializeItemPredicate(json, condition, "item_condition");
    }

    public static void serializeItemPredicate(JsonObject json, @NotNull ItemStackPredicate condition, String name) {
        if (condition == NoneItemStackPredicate.INSTANCE) {
            return;
        }
        JsonObject conditionJson = new JsonObject();
        ItemStackPredicate.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        Identifier serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey(serializer);
        conditionJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
        json.add(name, conditionJson);
    }

    public static @NotNull SkillEventListener deserializeEventListener(JsonObject json) {
        JsonObject eventJson = json.getAsJsonObject("event_listener");
        Identifier serializerId = Identifier.parse(eventJson.get("type").getAsString());
        SkillEventListener.Serializer serializer = PSTRegistries.EVENT_LISTENERS.get().getValue(serializerId);
        String errorMessage = "Unknown event listener: " + serializerId;
        return deserializeObject(serializer, eventJson, errorMessage);
    }

    public static void serializeEventListener(JsonObject json, @NotNull SkillEventListener condition) {
        JsonObject conditionJson = new JsonObject();
        SkillEventListener.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        Identifier serializerId = PSTRegistries.EVENT_LISTENERS.get().getKey(serializer);
        conditionJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
        json.add("event_listener", conditionJson);
    }

    public static @Nullable MobEffect deserializeMobEffect(JsonObject json) {
        if (!json.has("effect")) {
            return null;
        }
        Identifier effectId = Identifier.parse(json.get("effect").getAsString());
        
        return BuiltInRegistries.MOB_EFFECT.get(effectId)
                .map(net.minecraft.core.Holder::value)
                .orElse(null);
    }
    public static void serializeMobEffect(JsonObject json, MobEffect effect) {
        Identifier effectId = BuiltInRegistries.MOB_EFFECT.getKey(effect);
        json.addProperty("effect", Objects.requireNonNull(effectId).toString());
    }

    public static @Nullable PotionStackPredicate.Type deserializePotionType(JsonObject json) {
        return PotionStackPredicate.Type.byName(json.get("potion_type").getAsString());
    }

    public static void serializePotionType(JsonObject json, PotionStackPredicate.Type type) {
        json.addProperty("potion_type", type.getName());
    }

    public static MobEffectInstance deserializeEffectInstance(JsonObject json) {
        MobEffect effect = Objects.requireNonNull(deserializeMobEffect(json));
        int duration = json.get("duration").getAsInt();
        int amplifier = json.get("amplifier").getAsInt();
        return new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), duration, amplifier);
    }

    public static void serializeEffectInstance(JsonObject json, MobEffectInstance effect) {
        serializeMobEffect(json, effect.getEffect().value());
        json.addProperty("duration", effect.getDuration());
        json.addProperty("amplifier", effect.getAmplifier());
    }

    public static FloatFunction<?> deserializeValueProvider(JsonObject json) {
        JsonObject providerJson = json.getAsJsonObject("value_provider");
        String type = providerJson.get("type").getAsString();
        Identifier serializerId = Identifier.parse(type);
        FloatFunction.Serializer serializer = PSTRegistries.FLOAT_FUNCTIONS.get().getValue(serializerId);
        String errorMessage = "Unknown value provider: " + serializerId;
        return deserializeObject(serializer, providerJson, errorMessage);
    }

    public static void serializeValueProvider(JsonObject json, FloatFunction<?> provider) {
        Identifier serializerId = PSTRegistries.FLOAT_FUNCTIONS.get().getKey(provider.getSerializer());
        JsonObject bonusJson = new JsonObject();
        provider.getSerializer().serialize(bonusJson, provider);
        bonusJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
        json.add("value_provider", bonusJson);
    }

    @Nullable
    public static Attribute deserializeAttribute(CompoundTag tag) {
        
        Identifier attributeId = Identifier.parse(tag.getString("attribute").orElse(""));
        
        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeId)
                .map(net.minecraft.core.Holder::value)
                .orElse(null);
        if (attribute == null) {
            SkillTreeMod.LOGGER.error("Attribute {} doesn't exist!", attributeId);
        }
        return attribute;
    }

    public static void serializeAttribute(CompoundTag tag, Attribute attribute) {
        Identifier attributeId = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        Objects.requireNonNull(attributeId);
        tag.putString("attribute", attributeId.toString());
    }

    @NotNull
    public static AttributeModifier deserializeAttributeModifier(CompoundTag tag) {
        
        Identifier id = Identifier.parse(tag.getString("id").orElse(""));
        double amount = tag.getDouble("amount").orElse(0.0);
        AttributeModifier.Operation operation = deserializeOperation(tag);
        return new AttributeModifier(id, amount, operation);
    }

    public static void serializeAttributeModifier(CompoundTag tag, AttributeModifier modifier) {
        tag.putString("id", modifier.id().toString());
        tag.putDouble("amount", modifier.amount());
        serializeOperation(tag, modifier.operation());
    }

    @NotNull
    public static AttributeModifier.Operation deserializeOperation(CompoundTag tag) {
        
        String opName = tag.getString("operation").orElse("").toUpperCase();
        try {
            return AttributeModifier.Operation.valueOf(opName);
        } catch (IllegalArgumentException e) {
            return AttributeModifier.Operation.ADD_VALUE;
        }
    }
    public static void serializeOperation(CompoundTag tag, AttributeModifier.Operation operation) {
        
        tag.putString("operation", operation.name().toLowerCase(java.util.Locale.ROOT));
    }

    public static @NotNull LivingMultiplier deserializeLivingMultiplier(CompoundTag tag, String name) {
        if (!tag.contains(name)) {
            return NoneLivingMultiplier.INSTANCE;
        }
        
        CompoundTag multiplierTag = tag.getCompound(name).orElse(new CompoundTag());
        Identifier serializerId = Identifier.parse(multiplierTag.getString("type").orElse(""));
        LivingMultiplier.Serializer serializer = PSTRegistries.LIVING_MULTIPLIERS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(multiplierTag);
    }

    public static void serializeLivingMultiplier(CompoundTag tag, @NotNull LivingMultiplier multiplier, String name) {
        LivingMultiplier.Serializer serializer = multiplier.getSerializer();
        CompoundTag multiplierTag = serializer.serialize(multiplier);
        Identifier serializerId = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(serializer);
        multiplierTag.putString("type", Objects.requireNonNull(serializerId).toString());
        tag.put(name, multiplierTag);
    }

    public static @NotNull LivingEntityPredicate deserializeLivingCondition(CompoundTag tag, String name) {
        
        CompoundTag conditionTag = tag.getCompound(name).orElse(new CompoundTag());
        Identifier serializerId = Identifier.parse(conditionTag.getString("type").orElse(""));
        LivingEntityPredicate.Serializer serializer = PSTRegistries.LIVING_CONDITIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeLivingCondition(CompoundTag tag, @NotNull LivingEntityPredicate condition, String name) {
        LivingEntityPredicate.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        Identifier serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        conditionTag.putString("type", serializerId.toString());
        tag.put(name, conditionTag);
    }

    public static @NotNull MobEffectPredicate deserializeMobEffectCondition(CompoundTag tag, String name) {
        
        CompoundTag conditionTag = tag.getCompound(name).orElse(new CompoundTag());
        Identifier serializerId = Identifier.parse(conditionTag.getString("type").orElse(""));
        MobEffectPredicate.Serializer serializer = PSTRegistries.MOB_EFFECT_PREDICATES.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeMobEffectCondition(CompoundTag tag, @NotNull MobEffectPredicate condition, String name) {
        MobEffectPredicate.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        Identifier serializerId = PSTRegistries.MOB_EFFECT_PREDICATES.get().getKey(serializer);
        Objects.requireNonNull(serializerId);
        conditionTag.putString("type", serializerId.toString());
        tag.put(name, conditionTag);
    }

    public static @NotNull DamageCondition deserializeDamageCondition(CompoundTag tag) {
        return deserializeDamageCondition(tag, "damage_condition");
    }

    public static @NotNull DamageCondition deserializeDamageCondition(CompoundTag tag, String name) {
        
        CompoundTag conditionTag = tag.getCompound(name).orElse(new CompoundTag());
        Identifier serializerId = Identifier.parse(conditionTag.getString("type").orElse(""));
        DamageCondition.Serializer serializer = PSTRegistries.DAMAGE_CONDITIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeDamageCondition(CompoundTag tag, @NotNull DamageCondition condition) {
        serializeDamageCondition(tag, condition, "damage_condition");
    }

    public static void serializeDamageCondition(CompoundTag tag, @NotNull DamageCondition condition, String name) {
        DamageCondition.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        Identifier serializerId = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(serializer);
        conditionTag.putString("type", Objects.requireNonNull(serializerId).toString());
        tag.put(name, conditionTag);
    }

    public static @NotNull ItemStackPredicate deserializeItemPredicate(CompoundTag tag) {
        
        CompoundTag conditionTag = tag.getCompound("item_condition").orElse(new CompoundTag());
        Identifier serializerId = Identifier.parse(conditionTag.getString("type").orElse(""));
        ItemStackPredicate.Serializer serializer = PSTRegistries.ITEM_CONDITIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeItemPredicate(CompoundTag tag, @NotNull ItemStackPredicate condition) {
        ItemStackPredicate.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        Identifier serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey(serializer);
        conditionTag.putString("type", Objects.requireNonNull(serializerId).toString());
        tag.put("item_condition", conditionTag);
    }

    public static @NotNull SkillEventListener deserializeEventListener(CompoundTag tag) {
        
        CompoundTag conditionTag = tag.getCompound("event_listener").orElse(new CompoundTag());
        Identifier serializerId = Identifier.parse(conditionTag.getString("type").orElse(""));
        SkillEventListener.Serializer serializer = PSTRegistries.EVENT_LISTENERS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeEventListener(CompoundTag tag, @NotNull SkillEventListener condition) {
        SkillEventListener.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        Identifier serializerId = PSTRegistries.EVENT_LISTENERS.get().getKey(serializer);
        conditionTag.putString("type", Objects.requireNonNull(serializerId).toString());
        tag.put("event_listener", conditionTag);
    }
    @Nullable
    public static MobEffect deserializeMobEffect(CompoundTag tag) {
        if (!tag.contains("effect")) {
            return null;
        }
        
        Identifier effectId = Identifier.parse(tag.getString("effect").orElse(""));
        
        return BuiltInRegistries.MOB_EFFECT.get(effectId)
                .map(net.minecraft.core.Holder::value)
                .orElse(null);
    }

    public static void serializeMobEffect(CompoundTag tag, MobEffect effect) {
        Identifier effectId = BuiltInRegistries.MOB_EFFECT.getKey(effect);
        tag.putString("effect", Objects.requireNonNull(effectId).toString());
    }

    public static PotionStackPredicate.Type deserializePotionType(CompoundTag tag) {
        
        return PotionStackPredicate.Type.byName(tag.getString("potion_type").orElse(""));
    }

    public static void serializePotionType(CompoundTag tag, PotionStackPredicate.Type type) {
        tag.putString("potion_type", type.getName());
    }

    public static MobEffectInstance deserializeEffectInstance(CompoundTag tag) {
        MobEffect effect = Objects.requireNonNull(deserializeMobEffect(tag));
        
        int duration = tag.getInt("duration").orElse(0);
        int amplifier = tag.getInt("amplifier").orElse(0);
        return new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), duration, amplifier);
    }

    public static void serializeEffectInstance(CompoundTag tag, MobEffectInstance effect) {
        serializeMobEffect(tag, effect.getEffect().value());
        tag.putInt("duration", effect.getDuration());
        tag.putInt("amplifier", effect.getAmplifier());
    }

    public static FloatFunction<?> deserializeValueProvider(CompoundTag tag) {
        
        CompoundTag providerTag = tag.getCompound("value_provider").orElse(new CompoundTag());
        String type = providerTag.getString("type").orElse("");
        Identifier serializerId = Identifier.parse(type);
        FloatFunction.Serializer serializer = PSTRegistries.FLOAT_FUNCTIONS.get().getValue(serializerId);
        return Objects.requireNonNull(serializer).deserialize(providerTag);
    }

    public static void serializeValueProvider(CompoundTag tag, FloatFunction<?> provider) {
        Identifier serializerId = PSTRegistries.FLOAT_FUNCTIONS.get().getKey(provider.getSerializer());
        CompoundTag providerTag = provider.getSerializer().serialize(provider);
        providerTag.putString("type", Objects.requireNonNull(serializerId).toString());
        tag.put("value_provider", providerTag);
    }

    private static <T> T deserializeObject(Serializer<T> serializer, JsonObject jsonObject, String errorMessage) {
        return Objects.requireNonNull(serializer, errorMessage).deserialize(jsonObject);
    }

    public static JsonElement getElement(JsonObject json, String name) {
        JsonElement element = json.get(name);
        return Objects.requireNonNull(element, "Element not found: " + name);
    }

    public static void serializeItemBonus(JsonObject jsonObject, ItemBonus<?> itemBonus) {
        JsonObject itemBonusJson = new JsonObject();
        ItemBonus.Serializer itemBonusSerializer = itemBonus.getSerializer();
        Identifier itemBonusId = PSTRegistries.ITEM_BONUSES.get().getKey(itemBonusSerializer);
        Objects.requireNonNull(itemBonusId);
        itemBonusJson.addProperty("type", itemBonusId.toString());
        itemBonusSerializer.serialize(itemBonusJson, itemBonus);
        jsonObject.add("item_bonus", itemBonusJson);
    }

    public static ItemBonus<?> deserializeItemBonus(JsonObject jsonObject) {
        JsonObject itemBonusJson = jsonObject.get("item_bonus").getAsJsonObject();
        Identifier serializerId = Identifier.parse(itemBonusJson.get("type").getAsString());
        ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return serializer.deserialize(itemBonusJson);
    }

    public static void serializeItemBonus(CompoundTag tag, ItemBonus<?> itemBonus) {
        ItemBonus.Serializer itemBonusSerializer = itemBonus.getSerializer();
        Identifier itemBonusId = PSTRegistries.ITEM_BONUSES.get().getKey(itemBonusSerializer);
        Objects.requireNonNull(itemBonusId);
        CompoundTag itemBonusTag = itemBonusSerializer.serialize(itemBonus);
        itemBonusTag.putString("type", itemBonusId.toString());
        tag.put("item_bonus", itemBonusTag);
    }

    public static ItemBonus<?> deserializeItemBonus(CompoundTag tag) {
        
        CompoundTag itemBonusTag = tag.getCompound("item_bonus").orElse(new CompoundTag());
        Identifier serializerId = Identifier.parse(itemBonusTag.getString("type").orElse(""));
        ItemBonus.Serializer serializer = PSTRegistries.ITEM_BONUSES.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return serializer.deserialize(itemBonusTag);
    }
}