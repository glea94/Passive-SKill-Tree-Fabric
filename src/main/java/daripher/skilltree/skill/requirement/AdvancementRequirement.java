package daripher.skilltree.skill.requirement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTSkillRequirements;
import daripher.skilltree.mixin.ClientAdvancementsAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
public class AdvancementRequirement implements SkillRequirement<AdvancementRequirement> {
    private Identifier advancementId;
    public AdvancementRequirement(Identifier advancementId) {
        this.advancementId = advancementId;
    }
    @Override
    public boolean test(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return testServer(serverPlayer);
        }
        return testClient();
    }
    private boolean testServer(ServerPlayer player) {
<<<<<<< Updated upstream



=======
>>>>>>> Stashed changes
        ServerAdvancementManager manager = player.level().getServer().getAdvancements();
        AdvancementHolder advancement = manager.get(advancementId);
        if (advancement == null) return false;
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        return playerAdvancements.getOrStartProgress(advancement).isDone();
    }
    private boolean testClient() {
        ClientAdvancements advancements = Minecraft.getInstance().getConnection().getAdvancements();
        AdvancementHolder advancement = advancements.get(advancementId);
        if (advancement == null) return false;
        AdvancementProgress progress = ((ClientAdvancementsAccessor) advancements).getProgress().get(advancement);
        return progress != null && progress.isDone();
    }
    public static List<Identifier> getAdvancementIds() {
        ClientAdvancements advancements = Minecraft.getInstance().getConnection().getAdvancements();
        return advancements.getTree().nodes().stream()
                .map(AdvancementNode::holder)
                .map(AdvancementHolder::id)
                .toList();
    }
    @Override
    public MutableComponent getTooltip() {
        return Component.translatable(getDescriptionId(), Component.literal(advancementId.toString()));
    }
    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<AdvancementRequirement> consumer) {
        editor.addLabel(0, 0, "Advancement", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        List<Identifier> advancementIds = getAdvancementIds();
        editor.addSelectionMenu(0, 0, 200, advancementIds).setValue(advancementId)
                .setElementNameGetter(v -> Component.literal(v.toString()))
                .setResponder(v -> selectAdvancementId(consumer, v));
        editor.increaseHeight(19);
    }
    private void selectAdvancementId(Consumer<AdvancementRequirement> consumer, Identifier id) {
        setAdvancementId(id);
        consumer.accept(this.copy());
    }
    public void setAdvancementId(Identifier advancementId) {
        this.advancementId = advancementId;
    }
    public Identifier getAdvancementId() {
        return advancementId;
    }
    @Override
    public AdvancementRequirement copy() {
        return new AdvancementRequirement(advancementId);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdvancementRequirement that = (AdvancementRequirement) o;
        return Objects.equals(advancementId, that.advancementId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(advancementId);
    }
    @Override
    public SkillRequirement.Serializer getSerializer() {
        return PSTSkillRequirements.ADVANCEMENT.get();
    }
    public static class Serializer implements SkillRequirement.Serializer {
        @Override
        public SkillRequirement<?> deserialize(JsonObject json) throws JsonParseException {
            Identifier id = Identifier.parse(json.get("advancement").getAsString());
            return new AdvancementRequirement(id);
        }
        @Override
        public void serialize(JsonObject json, SkillRequirement<?> requirement) {
            if (requirement instanceof AdvancementRequirement aRequirement) {
                json.addProperty("advancement", aRequirement.advancementId.toString());
            }
        }
        @Override
        public SkillRequirement<?> deserialize(CompoundTag tag) {
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
            Identifier id = Identifier.parse(tag.getString("advancement").orElse(""));
            return new AdvancementRequirement(id);
        }
        @Override
        public CompoundTag serialize(SkillRequirement<?> requirement) {
            CompoundTag tag = new CompoundTag();
            if (requirement instanceof AdvancementRequirement aRequirement) {
                tag.putString("advancement", aRequirement.advancementId.toString());
            }
            return tag;
        }
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
        @Override
        public SkillRequirement<?> deserialize(RegistryFriendlyByteBuf buf) {
            Identifier id = Identifier.parse(buf.readUtf());
            return new AdvancementRequirement(id);
        }
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
        @Override
        public void serialize(RegistryFriendlyByteBuf buf, SkillRequirement<?> requirement) {
            if (requirement instanceof AdvancementRequirement aRequirement) {
                buf.writeUtf(aRequirement.advancementId.toString());
            }
        }
        @Override
        public SkillRequirement<?> createDefaultInstance() {
            return new AdvancementRequirement(Identifier.withDefaultNamespace("story/mine_stone"));
        }
    }
}