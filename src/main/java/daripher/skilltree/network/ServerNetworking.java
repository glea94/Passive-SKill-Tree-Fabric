package daripher.skilltree.network;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.exp.ExpHelper;
import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import daripher.skilltree.skill.PassiveSkill;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
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
        ServerPlayNetworking.registerGlobalReceiver(PSTNetworkChannels.LEARN_SKILL, (server, player, handler, buf, responseSender) -> {
            LearnSkillMessage message = LearnSkillMessage.decode(buf);
            server.execute(() -> handleLearnSkill(player, message));
        });
        ServerPlayNetworking.registerGlobalReceiver(PSTNetworkChannels.GAIN_SKILL_POINT, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> handleGainSkillPoint(player));
        });
    }

    private static void handleLearnSkill(ServerPlayer player, LearnSkillMessage message) {
        IPlayerSkills capability = PlayerSkillsProvider.get(player);
        PassiveSkill skill = SkillsReloader.getSkillById(message.getSkillId());
        Objects.requireNonNull(skill);
        if (capability.learnSkill(skill)) {
            skill.learn(player, true);
            // SYNCHRONISATION UNIQUE : Écrit sur le disque dur et met à jour l'écran du joueur proprement
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
        // SYNCHRONISATION UNIQUE : Sauvegarde le point et stabilise l'affichage des points restants
        PlayerSkillsProvider.KEY.sync(player);
    }

    public static void sendSyncPlayerSkills(ServerPlayer player) {
        SyncPlayerSkillsMessage message = new SyncPlayerSkillsMessage(player);
        FriendlyByteBuf buf = PacketByteBufs.create();
        message.encode(buf);
        ServerPlayNetworking.send(player, PSTNetworkChannels.SYNC_PLAYER_SKILLS, buf);
    }

    public static void sendSyncServerData(ServerPlayer player) {
        SyncServerDataMessage message = new SyncServerDataMessage();
        FriendlyByteBuf buf = PacketByteBufs.create();
        message.encode(buf);
        ServerPlayNetworking.send(player, PSTNetworkChannels.SYNC_SERVER_DATA, buf);
    }
<<<<<<< Updated upstream
}
=======

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
