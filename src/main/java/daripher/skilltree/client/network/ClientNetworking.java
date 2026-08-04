// Fichier : src/main/java/daripher/skilltree/client/network/ClientNetworking.java
package daripher.skilltree.client.network;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.PSTNetworkChannels;
import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.OpenSkillTreeEditorMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import daripher.skilltree.skill.PassiveSkill;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

<<<<<<< Updated upstream
/**
 * Portage Fabric : remplace la partie DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ...) /
 * @OnlyIn(Dist.CLIENT) de SyncPlayerSkillsMessage côté Forge. Sous Fabric, cette séparation se
 * fait simplement en gardant ce code dans le package client (jamais référencé par le code
 * commun ni par l'entrypoint serveur), donc jamais chargé sur un serveur dédié - même résultat
 * qu'avec DistExecutor, par la structure du code plutôt que par un test à l'exécution.
 */
=======
>>>>>>> Stashed changes
public class ClientNetworking {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PSTNetworkChannels.SYNC_SERVER_DATA, (client, handler, buf, responseSender) -> {
            SyncServerDataMessage message = SyncServerDataMessage.decode(buf);
            client.execute(() -> {
<<<<<<< Updated upstream
                // le decode() a déjà appliqué les données (SkillsReloader/SkillTreesReloader), comme dans la version Forge.
=======
>>>>>>> Stashed changes
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(PSTNetworkChannels.SYNC_PLAYER_SKILLS, (client, handler, buf, responseSender) -> {
            SyncPlayerSkillsMessage message = SyncPlayerSkillsMessage.decode(buf);
            client.execute(() -> handleSyncPlayerSkills(client, message));
<<<<<<< Updated upstream
=======
        });
        ClientPlayNetworking.registerGlobalReceiver(PSTNetworkChannels.OPEN_SKILL_TREE_EDITOR, (client, handler, buf, responseSender) -> {
            OpenSkillTreeEditorMessage message = OpenSkillTreeEditorMessage.decode(buf);
            client.execute(() -> client.setScreen(new SkillTreeEditorScreen(message.treeId)));
>>>>>>> Stashed changes
        });
    }

    private static void handleSyncPlayerSkills(Minecraft minecraft, SyncPlayerSkillsMessage message) {
        assert minecraft.player != null;
        IPlayerSkills capability = PlayerSkillsProvider.get(minecraft.player);
        capability.getPlayerSkills().clear();
        message.learnedSkills.stream().map(SkillsReloader::getSkillById).filter(Objects::nonNull)
                .forEach(capability.getPlayerSkills()::add);
        capability.setSkillPoints(message.skillPoints);
        if (minecraft.screen instanceof SkillTreeScreen screen) {
            screen.updateSkillPoints(capability.getSkillPoints());
            screen.init();
        }
    }

    public static void sendLearnSkill(PassiveSkill skill) {
        LearnSkillMessage message = new LearnSkillMessage(skill);
        FriendlyByteBuf buf = PacketByteBufs.create();
        message.encode(buf);
        ClientPlayNetworking.send(PSTNetworkChannels.LEARN_SKILL, buf);
    }

    public static void sendGainSkillPoint() {
        GainSkillPointMessage message = new GainSkillPointMessage();
        FriendlyByteBuf buf = PacketByteBufs.create();
        message.encode(buf);
        ClientPlayNetworking.send(PSTNetworkChannels.GAIN_SKILL_POINT, buf);
    }
}