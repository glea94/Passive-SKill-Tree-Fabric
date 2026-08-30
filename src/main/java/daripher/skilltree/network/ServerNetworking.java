package daripher.skilltree.network;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.event.MaceMasteryEvents;
import daripher.skilltree.exp.ExpHelper;
import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.OpenSkillTreeEditorMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import daripher.skilltree.network.message.SyncWorkbenchRecipesMessage;
import daripher.skilltree.skill.PassiveSkill;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.Objects;
public class ServerNetworking {
    public static void register() {
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
            // NOUVEAU : si le nœud appris est un nœud mace_mastery_X et que le joueur a déjà
            // atteint le seuil de kills correspondant, la teinte et les enchantements de sa
            // masse sont recalculés immédiatement (sinon ils ne l'étaient qu'au kill suivant).
            MaceMasteryEvents.onSkillLearned(player, skill.getId());
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
        PlayerSkillsProvider.KEY.sync(player);
    }
    public static void sendSyncPlayerSkills(ServerPlayer player) {
        ServerPlayNetworking.send(player, new SyncPlayerSkillsMessage(player));
    }
    public static void sendSyncServerData(ServerPlayer player) {
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