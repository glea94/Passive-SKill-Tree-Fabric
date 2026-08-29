package daripher.skilltree.skill.requirement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.event.MaceMasteryEvents;
import daripher.skilltree.init.PSTSkillRequirements;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import java.util.Objects;
import java.util.function.Consumer;
public class MaceMasteryKillsRequirement implements SkillRequirement<MaceMasteryKillsRequirement> {
    private int minKills;
    public MaceMasteryKillsRequirement(int minKills) {
        this.minKills = minKills;
    }
    @Override
    public boolean test(Player player) {
        return MaceMasteryEvents.getMaceMasteryKills(player) >= minKills;
    }
    @Override
    public MutableComponent getTooltip() {
        return Component.translatable(getDescriptionId(), minKills);
    }
    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<MaceMasteryKillsRequirement> consumer) {
        editor.addLabel(0, 0, "Min Kills (held item)", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, minKills).setNumericFilter(value -> value == value.intValue())
                .setNumericResponder(value -> selectMinKills(consumer, value));
        editor.increaseHeight(19);
    }
    private void selectMinKills(Consumer<MaceMasteryKillsRequirement> consumer, Double value) {
        setMinKills(value.intValue());
        consumer.accept(this.copy());
    }
    public void setMinKills(int minKills) {
        this.minKills = minKills;
    }
    public int getMinKills() {
        return minKills;
    }
    @Override
    public MaceMasteryKillsRequirement copy() {
        return new MaceMasteryKillsRequirement(minKills);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MaceMasteryKillsRequirement that = (MaceMasteryKillsRequirement) o;
        return minKills == that.minKills;
    }
    @Override
    public int hashCode() {
        return Objects.hash(minKills);
    }
    @Override
    public SkillRequirement.Serializer getSerializer() {
        return PSTSkillRequirements.MACE_MASTERY_KILLS.get();
    }
    public static class Serializer implements SkillRequirement.Serializer {
        @Override
        public SkillRequirement<?> deserialize(JsonObject json) throws JsonParseException {
            int minKills = json.get("min_kills").getAsInt();
            return new MaceMasteryKillsRequirement(minKills);
        }
        @Override
        public void serialize(JsonObject json, SkillRequirement<?> requirement) {
            if (requirement instanceof MaceMasteryKillsRequirement aRequirement) {
                json.addProperty("min_kills", aRequirement.minKills);
            }
        }
        @Override
        public SkillRequirement<?> deserialize(CompoundTag tag) {
            int minKills = tag.getInt("min_kills").orElse(0);
            return new MaceMasteryKillsRequirement(minKills);
        }
        @Override
        public CompoundTag serialize(SkillRequirement<?> requirement) {
            CompoundTag tag = new CompoundTag();
            if (requirement instanceof MaceMasteryKillsRequirement aRequirement) {
                tag.putInt("min_kills", aRequirement.minKills);
            }
            return tag;
        }
        @Override
        public SkillRequirement<?> deserialize(RegistryFriendlyByteBuf buf) {
            int minKills = buf.readInt();
            return new MaceMasteryKillsRequirement(minKills);
        }
        @Override
        public void serialize(RegistryFriendlyByteBuf buf, SkillRequirement<?> requirement) {
            if (requirement instanceof MaceMasteryKillsRequirement aRequirement) {
                buf.writeInt(aRequirement.minKills);
            }
        }
        @Override
        public SkillRequirement<?> createDefaultInstance() {
            return new MaceMasteryKillsRequirement(5);
        }
    }
}