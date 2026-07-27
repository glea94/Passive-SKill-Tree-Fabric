package daripher.skilltree.network.message;

import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Portage Fabric : classe déjà indépendante de Forge à part le type NetworkEvent.Context du
 * receive(), retiré ici. decode() applique directement les données reçues (comportement
 * identique à l'original), encode() est inchangé.
 */
public class SyncServerDataMessage {
    public static SyncServerDataMessage decode(FriendlyByteBuf buf) {
        SkillsReloader.loadFromByteBuf(buf);
        SkillTreesReloader.loadFromByteBuf(buf);
        return new SyncServerDataMessage();
    }

    public void encode(FriendlyByteBuf buf) {
        NetworkHelper.writePassiveSkills(buf, SkillsReloader.getSkills().values());
        NetworkHelper.writePassiveSkillTrees(buf, SkillTreesReloader.getSkillTrees().values());
    }
}
