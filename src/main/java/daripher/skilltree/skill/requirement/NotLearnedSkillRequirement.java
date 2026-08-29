package daripher.skilltree.skill.requirement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.init.PSTSkillRequirements;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
public class NotLearnedSkillRequirement implements SkillRequirement<NotLearnedSkillRequirement> {
    private Identifier skillId;
    public NotLearnedSkillRequirement(Identifier skillId) {
        this.skillId = skillId;
    }
    @Override
    public boolean test(Player player) {
        if (!PlayerSkillsProvider.hasSkills(player)) {
            return true;
        }
        NonNullList<PassiveSkill> skills = PlayerSkillsProvider.get(player).getPlayerSkills();
        return skills.stream().map(PassiveSkill::getId).noneMatch(skillId::equals);
    }
    @Override
    public MutableComponent getTooltip() {
        Component skillTitle = TooltipHelper.getSkillTitle(skillId).withStyle(Style.EMPTY.withColor(0xFFD75F));
        return Component.translatable(getDescriptionId(), skillTitle);
    }
    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<NotLearnedSkillRequirement> consumer) {
        editor.addLabel(0, 0, "Skill ID", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        Set<Identifier> skillIDs = SkillsReloader.getSkills().keySet();
        editor.addSelectionMenu(0, 0, 200, skillIDs).setValue(getSkillId()).setElementNameGetter(v -> Component.literal(v.toString()))
                .setResponder(v -> selectSkillId(consumer, v));
        editor.increaseHeight(19);
    }
    private void selectSkillId(Consumer<NotLearnedSkillRequirement> consumer, Identifier id) {
        setSkillId(id);
        consumer.accept(this);
    }
    public void setSkillId(Identifier skillId) {
        this.skillId = skillId;
    }
    @Override
    public NotLearnedSkillRequirement copy() {
        return new NotLearnedSkillRequirement(skillId);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotLearnedSkillRequirement that = (NotLearnedSkillRequirement) o;
        return Objects.equals(skillId, that.skillId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(skillId);
    }
    public Identifier getSkillId() {
        return skillId;
    }
    @Override
    public SkillRequirement.Serializer getSerializer() {
        return PSTSkillRequirements.NOT_LEARNED_SKILL.get();
    }
    public static class Serializer implements SkillRequirement.Serializer {
        @Override
        public SkillRequirement<?> deserialize(JsonObject json) throws JsonParseException {
            Identifier id = Identifier.parse(json.get("skill_id").getAsString());
            return new NotLearnedSkillRequirement(id);
        }
        @Override
        public void serialize(JsonObject json, SkillRequirement<?> requirement) {
            if (requirement instanceof NotLearnedSkillRequirement aRequirement) {
                json.addProperty("skill_id", aRequirement.skillId.toString());
            }
        }
        @Override
        public SkillRequirement<?> deserialize(CompoundTag tag) {
            Identifier id = Identifier.parse(tag.getString("skill_id").orElse(""));
            return new NotLearnedSkillRequirement(id);
        }
        @Override
        public CompoundTag serialize(SkillRequirement<?> requirement) {
            CompoundTag tag = new CompoundTag();
            if (requirement instanceof NotLearnedSkillRequirement aRequirement) {
                tag.putString("skill_id", aRequirement.skillId.toString());
            }
            return tag;
        }
        @Override
        public SkillRequirement<?> deserialize(RegistryFriendlyByteBuf buf) {
            Identifier id = Identifier.parse(buf.readUtf());
            return new NotLearnedSkillRequirement(id);
        }
        @Override
        public void serialize(RegistryFriendlyByteBuf buf, SkillRequirement<?> requirement) {
            if (requirement instanceof NotLearnedSkillRequirement aRequirement) {
                buf.writeUtf(aRequirement.skillId.toString());
            }
        }
        @Override
        public SkillRequirement<?> createDefaultInstance() {
            return new NotLearnedSkillRequirement(Identifier.parse("skilltree:hunter_1"));
        }
    }
}