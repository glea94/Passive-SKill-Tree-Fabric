// Fichier : src/main/java/daripher/skilltree/block/WorkbenchBlock.java
package daripher.skilltree.block;

import daripher.skilltree.inventory.menu.WorkbenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
<<<<<<< Updated upstream
import net.minecraft.world.InteractionHand;
=======
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
>>>>>>> Stashed changes
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorkbenchBlock extends Block {
    private static final Component CONTAINER_TITLE = Component.translatable("container.upgrade");

    // CORRECTION 1.21.4 : BlockBehaviour.Properties doit recevoir son id via setId(...) avant
    // construction (sinon NullPointerException "Block id not set" - même mécanisme que pour Item,
    // voir DeferredRegister).
    public WorkbenchBlock(ResourceLocation id) {
        super(Properties.of().setId(ResourceKey.create(Registries.BLOCK, id)).mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD)
                .ignitedByLava());
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(blockState.getMenuProvider(level, blockPos));
            // add custom stat awarded for block usage?
            return InteractionResult.CONSUME;
        }
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos) {
        return new SimpleMenuProvider((containerId, inventory, player) -> new WorkbenchMenu(containerId, inventory, ContainerLevelAccess.create(level, blockPos)), CONTAINER_TITLE);
    }
}
