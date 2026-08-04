package daripher.skilltree.client.network;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import daripher.skilltree.skill.PassiveSkill;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

import java.util.Objects;

/**
 * Portage Fabric : Gestion des paquets réseau côté Client pour la 1.21.1.
 * Intègre un rafraîchissement prédictif (feinte visuelle) pour corriger le lag de désynchronisation.
 */
public class ClientNetworking {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(SyncServerDataMessage.TYPE, (message, context) -> {
            context.client().execute(() -> {
                // le decode() du STREAM_CODEC a déjà appliqué les données (SkillsReloader/SkillTreesReloader), comme dans la version Forge.
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SyncPlayerSkillsMessage.TYPE, (message, context) -> {
            context.client().execute(() -> handleSyncPlayerSkills(context.client(), message));
<<<<<<< Updated upstream
=======
        });
        ClientPlayNetworking.registerGlobalReceiver(OpenSkillTreeEditorMessage.TYPE, (message, context) -> {
            context.client().execute(() -> handleOpenSkillTreeEditor(context.client(), message));
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

    private static void handleOpenSkillTreeEditor(Minecraft minecraft, OpenSkillTreeEditorMessage message) {
        minecraft.setScreen(new SkillTreeEditorScreen(message.treeId()));
    }

    public static void sendLearnSkill(PassiveSkill skill) {
        ClientPlayNetworking.send(new LearnSkillMessage(skill));
    }

    public static void sendGainSkillPoint() {
        // 1. Envoi réel du paquet au serveur
        ClientPlayNetworking.send(new GainSkillPointMessage());

        // 2. FEINTE VISUELLE : Rafraîchissement instantané de l'affichage client
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.screen instanceof SkillTreeScreen screen) {
            IPlayerSkills capability = PlayerSkillsProvider.get(minecraft.player);
            // On incrémente artificiellement le compteur local
            capability.setSkillPoints(capability.getSkillPoints() + 1);
            // On force l'écran de l'arbre de compétences à se redessiner immédiatement
            screen.updateSkillPoints(capability.getSkillPoints());
            screen.init();
        }
    }
}
