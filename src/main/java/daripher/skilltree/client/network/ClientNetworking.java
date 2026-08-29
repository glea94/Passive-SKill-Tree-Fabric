package daripher.skilltree.client.network;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.OpenSkillTreeEditorMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import daripher.skilltree.network.message.SyncWorkbenchRecipesMessage;
import daripher.skilltree.skill.PassiveSkill;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.Objects;

public class ClientNetworking {
    public static void register() {
<<<<<<< Updated upstream
        
        ClientPlayNetworking.registerGlobalReceiver(SyncServerDataMessage.TYPE, (message, context) -> {
            
            
            
=======
        ClientPlayNetworking.registerGlobalReceiver(SyncServerDataMessage.TYPE, (message, context) -> {
>>>>>>> Stashed changes
            RegistryFriendlyByteBuf buf = message.getDataBuffer();
            SkillsReloader.loadFromByteBuf(buf);
            SkillTreesReloader.loadFromByteBuf(buf);
        });

        ClientPlayNetworking.registerGlobalReceiver(SyncPlayerSkillsMessage.TYPE, (message, context) -> {
            handleSyncPlayerSkills(context.client(), message);
        });

        ClientPlayNetworking.registerGlobalReceiver(OpenSkillTreeEditorMessage.TYPE, (message, context) -> {
            handleOpenSkillTreeEditor(context.client(), message);
        });
<<<<<<< Updated upstream

        
=======
>>>>>>> Stashed changes
        ClientPlayNetworking.registerGlobalReceiver(SyncWorkbenchRecipesMessage.TYPE, (message, context) -> {
            ClientWorkbenchRecipeCache.set(message.recipes);
        });
    }

    private static void handleSyncPlayerSkills(Minecraft minecraft, SyncPlayerSkillsMessage message) {
        assert minecraft.player != null;
        IPlayerSkills capability = PlayerSkillsProvider.get(minecraft.player);
        capability.getPlayerSkills().clear();
        message.learnedSkills.stream().map(SkillsReloader::getSkillById).filter(Objects::nonNull)
                .forEach(capability.getPlayerSkills()::add);
        capability.setSkillPoints(message.skillPoints);
        if (minecraft.gui.screen() instanceof SkillTreeScreen screen) {
            screen.updateSkillPoints(capability.getSkillPoints());
            screen.init();
        }
    }

    private static void handleOpenSkillTreeEditor(Minecraft minecraft, OpenSkillTreeEditorMessage message) {
        minecraft.gui.setScreen(new SkillTreeEditorScreen(message.treeId()));
    }

    public static void sendLearnSkill(PassiveSkill skill) {
        ClientPlayNetworking.send(new LearnSkillMessage(skill));
    }

    public static void sendGainSkillPoint() {
<<<<<<< Updated upstream
        
        ClientPlayNetworking.send(new GainSkillPointMessage());

        
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.gui.screen() instanceof SkillTreeScreen screen) {
            IPlayerSkills capability = PlayerSkillsProvider.get(minecraft.player);
            
            capability.setSkillPoints(capability.getSkillPoints() + 1);
            
=======
        ClientPlayNetworking.send(new GainSkillPointMessage());
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.gui.screen() instanceof SkillTreeScreen screen) {
            IPlayerSkills capability = PlayerSkillsProvider.get(minecraft.player);
            capability.setSkillPoints(capability.getSkillPoints() + 1);
>>>>>>> Stashed changes
            screen.updateSkillPoints(capability.getSkillPoints());
            screen.init();
        }
    }
}