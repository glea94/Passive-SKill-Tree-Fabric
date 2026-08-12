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

<<<<<<< Updated upstream
    public void encode(FriendlyByteBuf buf) {
        NetworkHelper.writePassiveSkills(buf, SkillsReloader.getSkills().values());
        NetworkHelper.writePassiveSkillTrees(buf, SkillTreesReloader.getSkillTrees().values());
=======
    public static SyncServerDataMessage decode(RegistryFriendlyByteBuf buf) {
        // Fix 1.21.8 (identique au fix 1.21.9/1.21.10/1.21.11/26.1.2/26.2) : buf.copy() copie les octets lisibles
        // SANS avancer le readerIndex du buffer d'origine. Le framework de paquets vanilla vérifie après decode()
        // que readerIndex == writerIndex ; comme rien n'était "consommé", il rejetait le paquet avec "found X
        // bytes extra". buf.readBytes(int) fait la même copie mais avance le readerIndex du buffer source, ce qui
        // consomme correctement tout le payload.
        return new SyncServerDataMessage(new RegistryFriendlyByteBuf(buf.readBytes(buf.readableBytes()), buf.registryAccess()));
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        if (this.dataBuffer != null) {
            buf.writeBytes(this.dataBuffer);
        } else {
            NetworkHelper.writePassiveSkills(buf, SkillsReloader.getSkills().values());
            NetworkHelper.writePassiveSkillTrees(buf, SkillTreesReloader.getSkillTrees().values());
        }
    }

    public RegistryFriendlyByteBuf getDataBuffer() {
        return this.dataBuffer;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
>>>>>>> Stashed changes
    }
}