package daripher.skilltree.command.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.client.SkillTreeEditorData;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * Portage Fabric : RegisterClientCommandsEvent -> ClientCommandRegistrationCallback,
 * TickEvent.ClientTickEvent -> ClientTickEvents.END_CLIENT_TICK.
 */
public class PSTClientCommands {
    public static final SuggestionProvider<FabricClientCommandSource> CLIENT_SKILL_TREE_ID_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(gatherClientSkillTreesPaths(), builder);

    private static ResourceLocation tree_to_display;
    private static int timer;

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> editorCommand = com.mojang.brigadier.builder.LiteralArgumentBuilder.<FabricClientCommandSource>literal("skilltree")
                    .then(com.mojang.brigadier.builder.LiteralArgumentBuilder.<FabricClientCommandSource>literal("editor")
                            .then(com.mojang.brigadier.builder.RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("treeId", StringArgumentType.greedyString())
                                    .suggests(CLIENT_SKILL_TREE_ID_PROVIDER)
                                    .executes(PSTClientCommands::displaySkillTreeEditor)));
            dispatcher.register(editorCommand);
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> delayedCommandExecution());
    }

    private static void delayedCommandExecution() {
        if (timer > 0) {
            timer--;
            return;
        }
        if (tree_to_display != null) {
            Minecraft.getInstance().setScreen(new SkillTreeEditorScreen(tree_to_display));
            tree_to_display = null;
        }
    }

    private static int displaySkillTreeEditor(CommandContext<FabricClientCommandSource> ctx) {
        String treeIdArg = ctx.getArgument("treeId", String.class).toLowerCase(Locale.ROOT);
        PSTClientCommands.tree_to_display = new ResourceLocation(treeIdArg);
        PSTClientCommands.timer = 1;
        return 1;
    }

    @NotNull
    private static Stream<String> gatherClientSkillTreesPaths() {
        return Stream.concat(SkillTreesReloader.getSkillTrees().keySet().stream(), SkillTreeEditorData.getEditorTreesIDs().stream())
                .map(ResourceLocation::toString);
    }
}
