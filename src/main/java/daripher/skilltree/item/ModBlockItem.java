package daripher.skilltree.item;

import daripher.skilltree.util.registry.RegistryObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class ModBlockItem extends BlockItem {
    public ModBlockItem(RegistryObject<Block> blockRegistryObject) {
        super(blockRegistryObject.get(), new Properties());
    }
}
