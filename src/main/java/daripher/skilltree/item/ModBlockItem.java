package daripher.skilltree.item;

import daripher.skilltree.util.registry.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class ModBlockItem extends BlockItem {
    // CORRECTION 1.21.4 : Item.Properties doit recevoir son id via setId(...) avant construction
    // (sinon NullPointerException "Item id not set" - BlockItem(Block, Properties) ne dérive pas
    // l'id du Block, il fait juste super(properties) - voir DeferredRegister / WisdomScrollItem).
    public ModBlockItem(ResourceLocation id, RegistryObject<Block> blockRegistryObject) {
        super(blockRegistryObject.get(), new Properties().setId(ResourceKey.create(Registries.ITEM, id)));
    }
}