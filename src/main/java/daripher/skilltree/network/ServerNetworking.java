package daripher.skilltree.network;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.exp.ExpHelper;
import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.OpenSkillTreeEditorMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import daripher.skilltree.network.message.SyncWorkbenchRecipesMessage;
import daripher.skilltree.skill.PassiveSkill;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
<<<<<<< Updated upstream
import net.minecraft.network.FriendlyByteBuf;
=======
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
>>>>>>> Stashed changes
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class ServerNetworking {
    public static void register() {
        // Aligned 1.21.4: Registers server listeners targeting global custom network types
        ServerPlayNetworking.registerGlobalReceiver(LearnSkillMessage.TYPE, (message, context) -> {
            context.server().execute(() -> handleLearnSkill(context.player(), message));
        });

        ServerPlayNetworking.registerGlobalReceiver(GainSkillPointMessage.TYPE, (message, context) -> {
            context.server().execute(() -> handleGainSkillPoint(context.player()));
        });
    }

    private static void handleLearnSkill(ServerPlayer player, LearnSkillMessage message) {
        IPlayerSkills capability = PlayerSkillsProvider.get(player);
        PassiveSkill skill = SkillsReloader.getSkillById(message.getSkillId());
        Objects.requireNonNull(skill);
        if (capability.learnSkill(skill)) {
            skill.learn(player, true);
            // SYNCHRONISATION UNIQUE: Écrit sur le disque dur et met à jour l'écran du joueur proprement
            PlayerSkillsProvider.KEY.sync(player);
        }
    }

    private static void handleGainSkillPoint(ServerPlayer player) {
        IPlayerSkills capability = PlayerSkillsProvider.get(player);
        int skills = capability.getPlayerSkills().size();
        int points = capability.getSkillPoints();
        int level = skills + points;
        if (level >= ServerConfig.max_skill_points) {
            return;
        }
        int cost = ServerConfig.getSkillPointCost(level);
        if (ExpHelper.getPlayerExp(player) < cost) {
            return;
        }
        player.giveExperiencePoints(-cost);
        capability.grantSkillPoints(1);
        // SYNCHRONISATION UNIQUE: Sauvegarde le point et stabilise l'affichage des points restants
        PlayerSkillsProvider.KEY.sync(player);
    }

    public static void sendSyncPlayerSkills(ServerPlayer player) {
        ServerPlayNetworking.send(player, new SyncPlayerSkillsMessage(player));
    }

    public static void sendSyncServerData(ServerPlayer player) {
<<<<<<< Updated upstream
        SyncServerDataMessage message = new SyncServerDataMessage();
        FriendlyByteBuf buf = PacketByteBufs.create();
        message.encode(buf);
        ServerPlayNetworking.send(player, PSTNetworkChannels.SYNC_SERVER_DATA, buf);
    }
}
=======
        // Factual Fix 1.21.4: Pass a null context to allow standard data reloading serializers to populate the outcoming buffer
        ServerPlayNetworking.send(player, new SyncServerDataMessage(null));
    }

    public static void sendSyncWorkbenchRecipes(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        Objects.requireNonNull(server);
        ServerPlayNetworking.send(player, new SyncWorkbenchRecipesMessage(server));
    }

    public static void sendOpenSkillTreeEditor(ServerPlayer player, Identifier treeId) {
        ServerPlayNetworking.send(player, new OpenSkillTreeEditorMessage(treeId));
    }
}
>>>>>>> Stashed changes
