package daripher.skilltree.block;

import daripher.skilltree.inventory.menu.WorkbenchMenu;
import daripher.skilltree.util.registry.DeferredRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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

    public WorkbenchBlock() {

        super(Properties.of().setId(ResourceKey.create(Registries.BLOCK, DeferredRegister.currentId()))
                .mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD)
                .ignitedByLava());
    }


    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull BlockHitResult blockHitResult) {

        if (level.isClientSide()) {

            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(blockState.getMenuProvider(level, blockPos));

            return InteractionResult.SUCCESS;
        }
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos) {
        return new SimpleMenuProvider((containerId, inventory, player) -> new WorkbenchMenu(containerId, inventory, ContainerLevelAccess.create(level, blockPos)), CONTAINER_TITLE);
    }
}