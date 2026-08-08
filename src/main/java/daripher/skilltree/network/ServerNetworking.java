package daripher.skilltree.network;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.exp.ExpHelper;
import daripher.skilltree.init.PSTRecipeTypes;
import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.OpenSkillTreeEditorMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import daripher.skilltree.network.message.SyncWorkbenchRecipesMessage;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchVanillaCraftingRecipe;
import daripher.skilltree.skill.PassiveSkill;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
<<<<<<< Updated upstream
import net.minecraft.network.FriendlyByteBuf;
=======
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
>>>>>>> Stashed changes
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
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

    public static void sendOpenSkillTreeEditor(ServerPlayer player, ResourceLocation treeId) {
        ServerPlayNetworking.send(player, new OpenSkillTreeEditorMessage(treeId));
    }

    // Portage 1.21.4 : Level#getRecipeManager() a disparu côté client (cf. RecipeAccess), donc plus
    // aucun accès natif à la liste des recettes du workbench dans ClientLevel. On reconstruit ici,
    // côté serveur uniquement (RecipeManager frais via ServerLevel#recipeAccess()), exactement la
    // même liste que WorkbenchMenu#getAllWorkbenchRecipes() (branche serveur), et on l'envoie au
    // joueur à la connexion pour remplir son WorkbenchRecipeClientCache.
    public static void sendSyncWorkbenchRecipes(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        RecipeManager recipeManager = level.recipeAccess();
        List<RecipeHolder<CraftingRecipe>> vanillaCraftingRecipes = recipeManager.getRecipes().stream()
                .filter(recipe -> recipe.value().getType() == RecipeType.CRAFTING)
                .map(recipe -> (RecipeHolder<CraftingRecipe>) recipe)
                .filter(recipe -> !recipe.value().isSpecial())
                .toList();
        List<RecipeHolder<AbstractWorkbenchRecipe>> workbenchRecipes = recipeManager.getRecipes().stream()
                .filter(recipe -> recipe.value().getType() == PSTRecipeTypes.WORKBENCH)
                .map(recipe -> (RecipeHolder<AbstractWorkbenchRecipe>) recipe)
                .toList();
        List<AbstractWorkbenchRecipe> allRecipes = new ArrayList<>();
        workbenchRecipes.forEach(holder -> {
            AbstractWorkbenchRecipe recipe = holder.value();
            recipe.setId(holder.id().location());
            allRecipes.add(recipe);
        });
        vanillaCraftingRecipes.forEach(holder -> allRecipes.add(new WorkbenchVanillaCraftingRecipe(holder, level.registryAccess())));
        ServerPlayNetworking.send(player, new SyncWorkbenchRecipesMessage(allRecipes));
    }
}
>>>>>>> Stashed changes
