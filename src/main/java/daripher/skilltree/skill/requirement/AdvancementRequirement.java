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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class AdvancementRequirement implements SkillRequirement<AdvancementRequirement> {
    private ResourceLocation advancementId;

    public AdvancementRequirement(ResourceLocation advancementId) {
        this.advancementId = advancementId;
    }

    // CORRECTION 1.21.1 : test(Player) est la seule signature du Predicate<Player> hérité par
    // SkillRequirement<T>. L'ancien test(ServerPlayer) n'overridait rien, il fallait donc unifier
    // la logique serveur/client dans une seule méthode, en dispatchant selon le type réel du joueur.
    @Override
    public boolean test(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return testServer(serverPlayer);
        }
        return testClient();
    }

    private boolean testServer(ServerPlayer player) {
        ServerAdvancementManager manager = player.server.getAdvancements();
        AdvancementHolder advancement = manager.get(advancementId);
        if (advancement == null) return false;
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        return playerAdvancements.getOrStartProgress(advancement).isDone();
    }

    // CORRECTION 1.21.1 : ClientAdvancements n'a plus de getProgress(AdvancementHolder) public.
    // On passe par le mixin accessor ClientAdvancementsAccessor pour lire directement le champ
    // privé "progress" (Map<AdvancementHolder, AdvancementProgress>) de la classe vanilla.
    private boolean testClient() {
        ClientAdvancements advancements = Minecraft.getInstance().getConnection().getAdvancements();
        AdvancementHolder advancement = advancements.get(advancementId);
        if (advancement == null) return false;
        AdvancementProgress progress = ((ClientAdvancementsAccessor) advancements).getProgress().get(advancement);
        return progress != null && progress.isDone();
    }

    // CORRECTION 1.21.1 : AdvancementTree n'a plus de getAllAdvancements(). La méthode officielle
    // équivalente est nodes(), qui renvoie une Collection<AdvancementNode> (et non plus directement
    // des AdvancementHolder) ; il faut donc passer par node.holder().id() pour retrouver l'id.
    public static List<ResourceLocation> getAdvancementIds() {
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
        List<ResourceLocation> advancementIds = getAdvancementIds();
        editor.addSelectionMenu(0, 0, 200, advancementIds).setValue(advancementId)
                .setElementNameGetter(v -> Component.literal(v.toString()))
                .setResponder(v -> selectAdvancementId(consumer, v));
        editor.increaseHeight(19);
    }

    private void selectAdvancementId(Consumer<AdvancementRequirement> consumer, ResourceLocation id) {
        setAdvancementId(id);
        consumer.accept(this.copy());
    }

    public void setAdvancementId(ResourceLocation advancementId) {
        this.advancementId = advancementId;
    }

    public ResourceLocation getAdvancementId() {
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

    // CORRECTION 1.21.1 : le contrat correct n'est pas "SkillRequirementSerializer" (une classe GSON
    // interne, non générique, qui ne définit ni deserialize(JsonObject) ni serialize(FriendlyByteBuf, T)).
    // Le vrai type attendu par le registre (PSTSkillRequirements.ADVANCEMENT, typé
    // RegistryObject<SkillRequirement.Serializer>) est l'interface imbriquée SkillRequirement.Serializer,
    // exactement comme dans LearnedSkillRequirement, NumericValueRequirement et StatRequirement.
    public static class Serializer implements SkillRequirement.Serializer {
        @Override
        public SkillRequirement<?> deserialize(JsonObject json) throws JsonParseException {
            ResourceLocation id = ResourceLocation.parse(json.get("advancement").getAsString());
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
            ResourceLocation id = ResourceLocation.parse(tag.getString("advancement"));
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

        @Override
        public SkillRequirement<?> deserialize(FriendlyByteBuf buf) {
            ResourceLocation id = ResourceLocation.parse(buf.readUtf());
            return new AdvancementRequirement(id);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillRequirement<?> requirement) {
            if (requirement instanceof AdvancementRequirement aRequirement) {
                buf.writeUtf(aRequirement.advancementId.toString());
            }
        }

        @Override
        public SkillRequirement<?> createDefaultInstance() {
            return new AdvancementRequirement(ResourceLocation.withDefaultNamespace("story/mine_stone"));
        }
    }
}