package daripher.skilltree.skill.requirement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTSkillRequirements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;

import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class StatRequirement implements SkillRequirement<StatRequirement> {
    private Identifier statTypeId;
    private Identifier statId;
    private int minValue;

    public StatRequirement(Identifier statTypeId, Identifier statId, int minValue) {
        this.statTypeId = statTypeId;
        this.statId = statId;
        this.minValue = minValue;
    }

    @Override
    public boolean test(Player player) {
        StatType<?> statType = BuiltInRegistries.STAT_TYPE.get(statTypeId);
        Objects.requireNonNull(statType);
        int statValue = getStatValue(player, statType);
        return statValue >= minValue;
    }

    @Override
    public MutableComponent getTooltip() {
        StatType<?> statType = BuiltInRegistries.STAT_TYPE.get(statTypeId);
        if (statType == null) {
            return Component.literal("Unknown stat type: " + statTypeId).withStyle(ChatFormatting.RED);
        }
        if (statType == Stats.CUSTOM) {
<<<<<<< Updated upstream
            ResourceLocation originalStatId = Stats.CUSTOM.getRegistry().get(statId);
=======
            Identifier originalStatId = Stats.CUSTOM.getRegistry().get(statId).map(Holder::value).orElse(null);
>>>>>>> Stashed changes
            if (originalStatId == null) {
                return Component.literal("Unknown stat: " + statId).withStyle(ChatFormatting.RED);
            }
            String statIdString = originalStatId.toString().replace(':', '.');
            Component statName = Component.translatable("stat." + statIdString);
            Stat<Identifier> stat = Stats.CUSTOM.get(originalStatId);
            String formattedMinValue = stat.format(minValue).replace(".00", "");
            return Component.literal(statName.getString() + ": " + formattedMinValue);
        }
        if (statType == Stats.ENTITY_KILLED) {
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(statId);
            if (entityType == null) {
                return Component.literal("Unknown entity: " + statId).withStyle(ChatFormatting.RED);
            }
            Component entityName = entityType.getDescription();
            return Component.translatable(getDescriptionId() + ".killed", minValue, entityName);
        }
        if (statType == Stats.ENTITY_KILLED_BY) {
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(statId);
            if (entityType == null) {
                return Component.literal("Unknown entity: " + statId).withStyle(ChatFormatting.RED);
            }
            Component entityName = entityType.getDescription();
            return Component.translatable(getDescriptionId() + ".killed_by", entityName, minValue);
        } else {
            Item item = BuiltInRegistries.ITEM.get(statId);
            if (item == null) {
                return Component.literal("Unknown item: " + statId).withStyle(ChatFormatting.RED);
            }
            Component itemName = item.getDescription();
            return Component.literal(statType.getDisplayName().getString() + " " + itemName.getString() + ": " + minValue);
        }
    }

    private <T> int getStatValue(Player player, @NotNull StatType<T> statType) {
        StatsCounter playerStats = getPlayerStats(player);
        int statValue;
        if (statType == Stats.CUSTOM) {
<<<<<<< Updated upstream
            ResourceLocation originalStatId = Stats.CUSTOM.getRegistry().get(statId);
=======
            Identifier originalStatId = Stats.CUSTOM.getRegistry().get(statId).map(Holder::value).orElse(null);
>>>>>>> Stashed changes
            if (originalStatId == null) {
                return 0;
            }
            statValue = playerStats.getValue(Stats.CUSTOM, originalStatId);
        } else {
            T stat = statType.getRegistry().get(statId);
            Objects.requireNonNull(stat);
            statValue = playerStats.getValue(statType, stat);
        }
        return statValue;
    }

    private StatsCounter getPlayerStats(Player player) {
        if (player.level().isClientSide) {
            return getClientPlayerStats(player);
        }
        return ((ServerPlayer) player).getStats();
    }

    private static StatsCounter getClientPlayerStats(Player player) {
        return ((LocalPlayer) player).getStats();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<StatRequirement> consumer) {
        editor.addLabel(0, 0, "Stat Type", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        Set<Identifier> statTypeIds = BuiltInRegistries.STAT_TYPE.keySet();
        editor.addSelectionMenu(0, 0, 200, statTypeIds).setValue(getStatTypeId()).setElementNameGetter(v -> Component.literal(v.toString()))
                .setResponder(v -> selectStatType(consumer, v));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Stat", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        StatType<?> statType = BuiltInRegistries.STAT_TYPE.get(getStatTypeId());
        Objects.requireNonNull(statType);
        Set<Identifier> statIds = statType.getRegistry().keySet();
        editor.addSelectionMenu(0, 0, 200, statIds).setValue(getStatId()).setElementNameGetter(v -> Component.literal(v.toString()))
                .setResponder(v -> selectStat(consumer, v));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Min Value", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, minValue).setNumericFilter(value -> value == value.intValue())
                .setNumericResponder(value -> selectMinValue(consumer, value));
        editor.increaseHeight(19);
    }

    private void selectMinValue(Consumer<StatRequirement> consumer, Double value) {
        setMinValue(value.intValue());
        consumer.accept(this);
    }

    private void selectStat(Consumer<StatRequirement> consumer, Identifier statId) {
        setStatId(statId);
        consumer.accept(this);
    }

    private void selectStatType(Consumer<StatRequirement> consumer, Identifier statTypeId) {
        setStatTypeId(statTypeId);
        StatType<?> statType = BuiltInRegistries.STAT_TYPE.get(getStatTypeId());
        Objects.requireNonNull(statType);
        Set<Identifier> statIds = statType.getRegistry().keySet();
        statIds.stream().findFirst().ifPresent(this::setStatId);
        consumer.accept(this);
    }

    public void setStatId(Identifier statId) {
        this.statId = statId;
    }

    public void setStatTypeId(Identifier statTypeId) {
        this.statTypeId = statTypeId;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    @Override
    public StatRequirement copy() {
        return new StatRequirement(statTypeId, statId, minValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatRequirement that = (StatRequirement) o;
        return minValue == that.minValue && Objects.equals(statTypeId, that.statTypeId) && Objects.equals(statId, that.statId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statTypeId, statId, minValue);
    }

    public Identifier getStatTypeId() {
        return statTypeId;
    }

    public Identifier getStatId() {
        return statId;
    }

    @Override
    public SkillRequirement.Serializer getSerializer() {
        return PSTSkillRequirements.STAT_VALUE.get();
    }

    public static class Serializer implements SkillRequirement.Serializer {
        @Override
        public SkillRequirement<?> deserialize(JsonObject json) throws JsonParseException {
<<<<<<< Updated upstream
            ResourceLocation statTypeId = new ResourceLocation(json.get("statTypeId").getAsString());
            ResourceLocation statId = new ResourceLocation(json.get("statId").getAsString());
=======
            Identifier statTypeId = Identifier.parse(json.get("statTypeId").getAsString());
            Identifier statId = Identifier.parse(json.get("statId").getAsString());
>>>>>>> Stashed changes
            int minValue = json.get("minValue").getAsInt();
            return new StatRequirement(statTypeId, statId, minValue);
        }

        @Override
        public void serialize(JsonObject json, SkillRequirement<?> requirement) {
            if (requirement instanceof StatRequirement aRequirement) {
                json.addProperty("statTypeId", aRequirement.statTypeId.toString());
                json.addProperty("statId", aRequirement.statId.toString());
                json.addProperty("minValue", aRequirement.minValue);
            }
        }

        @Override
        public SkillRequirement<?> deserialize(CompoundTag tag) {
<<<<<<< Updated upstream
            ResourceLocation statTypeId = new ResourceLocation(tag.getString("statTypeId"));
            ResourceLocation statId = new ResourceLocation(tag.getString("statId"));
            int minValue = tag.getInt("minValue");
=======
            // Factual Fix 1.21.5: getString/getInt renvoient désormais Optional<T>
            Identifier statTypeId = Identifier.parse(tag.getString("statTypeId").orElse(""));
            Identifier statId = Identifier.parse(tag.getString("statId").orElse(""));
            int minValue = tag.getInt("minValue").orElse(0);
>>>>>>> Stashed changes
            return new StatRequirement(statTypeId, statId, minValue);
        }

        @Override
        public CompoundTag serialize(SkillRequirement<?> requirement) {
            CompoundTag tag = new CompoundTag();
            if (requirement instanceof StatRequirement aRequirement) {
                tag.putString("statTypeId", aRequirement.statTypeId.toString());
                tag.putString("statId", aRequirement.statId.toString());
                tag.putInt("minValue", aRequirement.minValue);
            }
            return tag;
        }

        @Override
<<<<<<< Updated upstream
        public SkillRequirement<?> deserialize(FriendlyByteBuf buf) {
            ResourceLocation statTypeId = new ResourceLocation(buf.readUtf());
            ResourceLocation statId = new ResourceLocation(buf.readUtf());
=======
        public SkillRequirement<?> deserialize(RegistryFriendlyByteBuf buf) {
            Identifier statTypeId = Identifier.parse(buf.readUtf());
            Identifier statId = Identifier.parse(buf.readUtf());
>>>>>>> Stashed changes
            int minValue = buf.readInt();
            return new StatRequirement(statTypeId, statId, minValue);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillRequirement<?> requirement) {
            if (requirement instanceof StatRequirement aRequirement) {
                buf.writeUtf(aRequirement.statTypeId.toString());
                buf.writeUtf(aRequirement.statId.toString());
                buf.writeInt(aRequirement.minValue);
            }
        }

        @Override
        public SkillRequirement<?> createDefaultInstance() {
<<<<<<< Updated upstream
            return new StatRequirement(BuiltInRegistries.STAT_TYPE.getKey(Stats.CUSTOM), Stats.DEATHS, 1);
=======
            // Factual Fix 1.21.4: Resolve custom stat type using its direct official registry identifier location to fix argument mismatch
            Identifier customStatType = Stats.CUSTOM.getRegistry().key().identifier();
            return new StatRequirement(customStatType, Stats.DEATHS, 1);
>>>>>>> Stashed changes
        }
    }
}
