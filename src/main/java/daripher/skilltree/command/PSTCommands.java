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
    public static final SuggestionProvider<CommandSourceStack> SKILL_ID_SUGGESTION = (ctx, builder) -> SharedSuggestionProvider.suggest(gatherSkillIds(), builder);
    public static final SuggestionProvider<CommandSourceStack> SKILL_TREE_ID_SUGGESTION = (ctx, builder) -> SharedSuggestionProvider.suggest(gatherSkillTreesIds(), builder);
    public static final ResourceLocation DEFAULT_SKILL_TREE_ID = ResourceLocation.fromNamespaceAndPath("skilltree", "tree");
    public static final String AMOUNT_ARGUMENT_NAME = "amount";
    public static final String PLAYER_ARGUMENT_NAME = "player";
    public static final String SKILL_ID_ARGUMENT_NAME = "skill_id";
    public static final String SKILL_TREE_ID_ARGUMENT_NAME = "skill_tree_id";

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
    }

    private static void registerCommands(com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher) {
        var resetCommand = getRootCommand().then(getResetCommand().then(getPlayerArgument().executes(PSTCommands::executeResetCommand)));
        dispatcher.register(resetCommand);

        var addPointsCommand = getRootCommand().then(getPointsSubCommand().then(getAddSubCommand().then(getPlayerArgument().then(getAmountArgument().executes(PSTCommands::executeAddPointsCommand)))));
        dispatcher.register(addPointsCommand);

        var setPointsCommand = getRootCommand().then(getPointsSubCommand().then(getSetSubCommand().then(getPlayerArgument().then(getAmountArgument().executes(PSTCommands::executeSetPointsCommand)))));
        dispatcher.register(setPointsCommand);

        var grantSkillCommand = getRootCommand().then(getGrantSkillSubCommand().then(getPlayerArgument().then(getSkillArgument().executes(PSTCommands::executeGrantSkillCommand))));
        dispatcher.register(grantSkillCommand);

        var editorCommand = getRootCommand().then(getEditorSubCommand()
                .executes(PSTCommands::executeEditorCommand)
                .then(getSkillTreeArgument().executes(PSTCommands::executeEditorCommand)));
        dispatcher.register(editorCommand);
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
            treeId = ctx.getArgument(SKILL_TREE_ID_ARGUMENT_NAME, ResourceLocation.class);
        } catch (IllegalArgumentException e) {
            treeId = DEFAULT_SKILL_TREE_ID;
        }
        ServerNetworking.sendOpenSkillTreeEditor(player, treeId);
        return 1;
    }

    private static int executeGrantSkillCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, PLAYER_ARGUMENT_NAME);
        ResourceLocation skillId = ctx.getArgument(SKILL_ID_ARGUMENT_NAME, ResourceLocation.class);
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        if (skillsCapability.grantSkill(SkillsReloader.getSkillById(skillId))) {
            ServerNetworking.sendSyncPlayerSkills(player);
            Component skillName = TooltipHelper.getSkillTitle(skillId);
            player.sendSystemMessage(Component.translatable("skilltree.message.grant_skill_command", skillName)
                    .withStyle(ChatFormatting.YELLOW));
        }
        return 1;
    }

    private static boolean hasPermission(CommandSourceStack commandSourceStack) {
        return commandSourceStack.hasPermission(2);
    }

    @NotNull
    private static Stream<String> gatherSkillTreesIds() {
        return SkillTreesReloader.getSkillTrees().keySet().stream().map(ResourceLocation::toString);
    }

    @NotNull
    private static Stream<String> gatherSkillIds() {
        return SkillsReloader.getSkills().keySet().stream().map(ResourceLocation::toString);
    }
}