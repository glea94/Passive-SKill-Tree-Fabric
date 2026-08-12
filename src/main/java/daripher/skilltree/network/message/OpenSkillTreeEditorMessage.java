package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenSkillTreeEditorMessage(ResourceLocation treeId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenSkillTreeEditorMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "open_skill_tree_editor"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSkillTreeEditorMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, OpenSkillTreeEditorMessage::treeId,
                    OpenSkillTreeEditorMessage::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}