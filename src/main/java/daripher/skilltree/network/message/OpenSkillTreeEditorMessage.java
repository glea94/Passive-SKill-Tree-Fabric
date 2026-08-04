// Fichier : src/main/java/daripher/skilltree/network/message/OpenSkillTreeEditorMessage.java
package daripher.skilltree.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class OpenSkillTreeEditorMessage {
    public final ResourceLocation treeId;

    public OpenSkillTreeEditorMessage(ResourceLocation treeId) {
        this.treeId = treeId;
    }

    public static OpenSkillTreeEditorMessage decode(FriendlyByteBuf buf) {
        return new OpenSkillTreeEditorMessage(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(treeId);
    }
}