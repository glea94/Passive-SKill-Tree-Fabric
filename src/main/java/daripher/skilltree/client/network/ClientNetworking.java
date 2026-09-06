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

public class ClientNetworking {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PSTNetworkChannels.SYNC_SERVER_DATA, (client, handler, buf, responseSender) -> {
            SyncServerDataMessage message = SyncServerDataMessage.decode(buf);
            client.execute(() -> {
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(PSTNetworkChannels.SYNC_PLAYER_SKILLS, (client, handler, buf, responseSender) -> {
            SyncPlayerSkillsMessage message = SyncPlayerSkillsMessage.decode(buf);
            client.execute(() -> handleSyncPlayerSkills(client, message));
        });
        ClientPlayNetworking.registerGlobalReceiver(PSTNetworkChannels.OPEN_SKILL_TREE_EDITOR, (client, handler, buf, responseSender) -> {
            OpenSkillTreeEditorMessage message = OpenSkillTreeEditorMessage.decode(buf);
            client.execute(() -> client.setScreen(new SkillTreeEditorScreen(message.treeId)));
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