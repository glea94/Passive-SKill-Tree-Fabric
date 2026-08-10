package daripher.skilltree.network.message;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenSkillTreeEditorMessage(Identifier treeId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenSkillTreeEditorMessage> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "open_skill_tree_editor"));

    // Aligned 1.21.4: Using modern streamlined composite codecs with full RegistryFriendlyByteBuf tracking
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSkillTreeEditorMessage> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC, OpenSkillTreeEditorMessage::treeId,
                    OpenSkillTreeEditorMessage::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
