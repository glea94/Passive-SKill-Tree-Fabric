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
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
=======
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
=======
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

<<<<<<< Updated upstream
<<<<<<< Updated upstream
public final class AdvancementRequirement implements SkillRequirement<AdvancementRequirement> {
    private ResourceLocation advancementId;
=======
public class AdvancementRequirement implements SkillRequirement<AdvancementRequirement> {
    private Identifier advancementId;
>>>>>>> Stashed changes
=======
public class AdvancementRequirement implements SkillRequirement<AdvancementRequirement> {
    private Identifier advancementId;
>>>>>>> Stashed changes

    public AdvancementRequirement(Identifier advancementId) {
        this.advancementId = advancementId;
    }

    @Override
    public boolean test(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return testServer(serverPlayer);
        }
<<<<<<< Updated upstream
=======
        return testClient();
    }

    private boolean testServer(ServerPlayer player) {
        // Factual Fix 1.21.8 : champ ServerPlayer#server devenu private. Pattern player.level().getServer()
        // confirmé par décompilation de ServerPlayer lui-même (utilisé en interne, ex. loadAndSpawnEnderPearl :
        // "this.level().getServer().getLevel(...)"), et ServerPlayer#level() renvoie bien ServerLevel.
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
>>>>>>> Stashed changes
    }

    @Override
    public MutableComponent getTooltip() {
        return Component.translatable(getDescriptionId(), Component.literal(advancementId.toString()));
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<AdvancementRequirement> consumer) {
        editor.addLabel(0, 0, "Advancement", ChatFormatting.GOLD);
        editor.increaseHeight(19);
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        List<ResourceLocation> advancementIds = advancements.getAdvancements().getAllAdvancements().stream().map(Advancement::getId)
                .toList();
        editor.addSelectionMenu(0, 0, 200, advancementIds).setValue(getAdvancementId())
                .setElementNameGetter(v -> Component.literal(v.toString())).setResponder(v -> selectAdvancementId(consumer, v));
=======
=======
>>>>>>> Stashed changes
        List<Identifier> advancementIds = getAdvancementIds();
        editor.addSelectionMenu(0, 0, 200, advancementIds).setValue(advancementId)
                .setElementNameGetter(v -> Component.literal(v.toString()))
                .setResponder(v -> selectAdvancementId(consumer, v));
>>>>>>> Stashed changes
        editor.increaseHeight(19);
    }

    private void selectAdvancementId(Consumer<AdvancementRequirement> consumer, Identifier id) {
        setAdvancementId(id);
        consumer.accept(this.copy());
    }

    public void setAdvancementId(Identifier advancementId) {
        this.advancementId = advancementId;
    }

<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
=======
>>>>>>> Stashed changes
    public Identifier getAdvancementId() {
        return advancementId;
    }

>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
<<<<<<< Updated upstream
            ResourceLocation id = new ResourceLocation(json.get("advancement").getAsString());
=======
            Identifier id = Identifier.parse(json.get("advancement").getAsString());
>>>>>>> Stashed changes
=======
            Identifier id = Identifier.parse(json.get("advancement").getAsString());
>>>>>>> Stashed changes
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
            ResourceLocation id = new ResourceLocation(tag.getString("advancement"));
=======
            // Factual Fix 1.21.5: getString renvoie désormais Optional<String>
            Identifier id = Identifier.parse(tag.getString("advancement").orElse(""));
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
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

        // Factual Fix 1.21.4: Refactored signature from FriendlyByteBuf to RegistryFriendlyByteBuf
        @Override
<<<<<<< Updated upstream
        public SkillRequirement<?> deserialize(FriendlyByteBuf buf) {
            ResourceLocation id = new ResourceLocation(buf.readUtf());
=======
        public SkillRequirement<?> deserialize(RegistryFriendlyByteBuf buf) {
            Identifier id = Identifier.parse(buf.readUtf());
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
            return new AdvancementRequirement(id);
        }

        // Factual Fix 1.21.4: Refactored signature from FriendlyByteBuf to RegistryFriendlyByteBuf
        @Override
        public void serialize(RegistryFriendlyByteBuf buf, SkillRequirement<?> requirement) {
            if (requirement instanceof AdvancementRequirement aRequirement) {
                buf.writeUtf(aRequirement.advancementId.toString());
            }
        }

        @Override
        public SkillRequirement<?> createDefaultInstance() {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
            return new AdvancementRequirement(new ResourceLocation("minecraft:adventure/hero_of_the_village"));
=======
            return new AdvancementRequirement(Identifier.withDefaultNamespace("story/mine_stone"));
>>>>>>> Stashed changes
=======
            return new AdvancementRequirement(Identifier.withDefaultNamespace("story/mine_stone"));
>>>>>>> Stashed changes
        }
    }
}