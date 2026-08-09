package daripher.skilltree.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.ServerNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class PSTCommands {
    // Factual Fix 1.21.4: Use suggestResource directly with ResourceLocation streams for perfect efficiency
    public static final SuggestionProvider<CommandSourceStack> SKILL_ID_SUGGESTION = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(SkillsReloader.getSkillIds().stream(), builder);

    public static final SuggestionProvider<CommandSourceStack> SKILL_TREE_ID_SUGGESTION = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(SkillTreesReloader.getSkillTrees().keySet().stream(), builder);

    public static final ResourceLocation DEFAULT_SKILL_TREE_ID = ResourceLocation.fromNamespaceAndPath("skilltree", "tree");
    public static final String AMOUNT_ARGUMENT_NAME = "amount";
    public static final String PLAYER_ARGUMENT_NAME = "player";
    public static final String SKILL_ID_ARGUMENT_NAME = "skill_id";
    public static final String SKILL_TREE_ID_ARGUMENT_NAME = "skill_tree_id";

    public static void register() {
        // En 1.21.4, la signature d'enregistrement Fabric v2 prend l'environnement de commande proprement
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
    }

    private static void registerCommands(com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher) {
        // Factual Fix 1.21.4: Group all sub-commands into one main builder to reduce tree duplication overhead
        LiteralArgumentBuilder<CommandSourceStack> baseCommand = getRootCommand();

        baseCommand.then(getResetCommand().then(getPlayerArgument().executes(PSTCommands::executeResetCommand)));

        baseCommand.then(getPointsSubCommand()
                .then(getAddSubCommand().then(getPlayerArgument().then(getAmountArgument().executes(PSTCommands::executeAddPointsCommand))))
                .then(getSetSubCommand().then(getPlayerArgument().then(getAmountArgument().executes(PSTCommands::executeSetPointsCommand)))));

        baseCommand.then(getGrantSkillSubCommand().then(getPlayerArgument().then(getSkillArgument().executes(PSTCommands::executeGrantSkillCommand))));

        baseCommand.then(getEditorSubCommand()
                .executes(PSTCommands::executeEditorCommand)
                .then(getSkillTreeArgument().executes(PSTCommands::executeEditorCommand)));

        dispatcher.register(baseCommand);
    }

    private static @NotNull LiteralArgumentBuilder<CommandSourceStack> getGrantSkillSubCommand() {
        return Commands.literal("grant_skill");
    }

    private static @NotNull LiteralArgumentBuilder<CommandSourceStack> getEditorSubCommand() {
        return Commands.literal("editor");
    }

    private static @NotNull LiteralArgumentBuilder<CommandSourceStack> getSetSubCommand() {
        return Commands.literal("set");
    }

    private static @NotNull LiteralArgumentBuilder<CommandSourceStack> getAddSubCommand() {
        return Commands.literal("add");
    }

    private static @NotNull LiteralArgumentBuilder<CommandSourceStack> getResetCommand() {
        return Commands.literal("reset");
    }

    private static @NotNull LiteralArgumentBuilder<CommandSourceStack> getPointsSubCommand() {
        return Commands.literal("points");
    }

    private static LiteralArgumentBuilder<CommandSourceStack> getRootCommand() {
        return Commands.literal("skilltree").requires(PSTCommands::hasPermission);
    }
    private static @NotNull RequiredArgumentBuilder<CommandSourceStack, EntitySelector> getPlayerArgument() {
        return Commands.argument(PLAYER_ARGUMENT_NAME, EntityArgument.player());
    }

    private static @NotNull RequiredArgumentBuilder<CommandSourceStack, Integer> getAmountArgument() {
        return Commands.argument(AMOUNT_ARGUMENT_NAME, IntegerArgumentType.integer());
    }

    private static @NotNull RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> getSkillArgument() {
        return Commands.argument(SKILL_ID_ARGUMENT_NAME, ResourceLocationArgument.id()).suggests(SKILL_ID_SUGGESTION);
    }

    private static @NotNull RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> getSkillTreeArgument() {
        return Commands.argument(SKILL_TREE_ID_ARGUMENT_NAME, ResourceLocationArgument.id()).suggests(SKILL_TREE_ID_SUGGESTION);
    }

    private static int executeResetCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, PLAYER_ARGUMENT_NAME);
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        skillsCapability.resetTree(player);
        player.sendSystemMessage(Component.translatable("skilltree.message.reset_command").withStyle(ChatFormatting.YELLOW));
        ServerNetworking.sendSyncPlayerSkills(player);
        return 1;
    }

    private static int executeAddPointsCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, PLAYER_ARGUMENT_NAME);
        int amount = IntegerArgumentType.getInteger(ctx, AMOUNT_ARGUMENT_NAME);
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        skillsCapability.setSkillPoints(amount + skillsCapability.getSkillPoints());
        player.sendSystemMessage(Component.translatable("skilltree.message.point_command").withStyle(ChatFormatting.YELLOW));
        ServerNetworking.sendSyncPlayerSkills(player);
        return 1;
    }

    private static int executeSetPointsCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, PLAYER_ARGUMENT_NAME);
        int amount = IntegerArgumentType.getInteger(ctx, AMOUNT_ARGUMENT_NAME);
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        skillsCapability.setSkillPoints(amount);
        ServerNetworking.sendSyncPlayerSkills(player);
        return 1;
    }

    private static int executeEditorCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ResourceLocation treeId;
        try {
            // Factual Fix 1.21.4: Standardize resource key extractions via ResourceLocationArgument helper
            treeId = ResourceLocationArgument.getId(ctx, SKILL_TREE_ID_ARGUMENT_NAME);
        } catch (IllegalArgumentException e) {
            treeId = DEFAULT_SKILL_TREE_ID;
        }
        ServerNetworking.sendOpenSkillTreeEditor(player, treeId);
        return 1;
    }

    private static int executeGrantSkillCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, PLAYER_ARGUMENT_NAME);
        // Factual Fix 1.21.4: Standardize resource key extractions via ResourceLocationArgument helper
        ResourceLocation skillId = ResourceLocationArgument.getId(ctx, SKILL_ID_ARGUMENT_NAME);
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);

        var skillInstance = SkillsReloader.getSkillById(skillId);
        if (skillInstance != null && skillsCapability.grantSkill(skillInstance)) {
            ServerNetworking.sendSyncPlayerSkills(player);
            Component skillName = TooltipHelper.getSkillTitle(skillId);
            player.sendSystemMessage(Component.translatable("skilltree.message.grant_skill_command", skillName)
                    .withStyle(ChatFormatting.YELLOW));
            return 1;
        }
        return 0;
    }

    private static boolean hasPermission(CommandSourceStack commandSourceStack) {
        return commandSourceStack.hasPermission(2);
    }
}
