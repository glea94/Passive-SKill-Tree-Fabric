package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.block.WorkbenchBlock;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public class PSTBlocks {
    // Aligned 1.21.4: Direct registry linking with BuiltInRegistries.BLOCK
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK, SkillTreeMod.MOD_ID);

    // Crafting stations
    public static final RegistryObject<Block> WORKBENCH = REGISTRY.register("workbench", WorkbenchBlock::new);
}
