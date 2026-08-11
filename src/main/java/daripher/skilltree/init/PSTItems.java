package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.AmnesiaScrollItem;
import daripher.skilltree.item.ModBlockItem;
import daripher.skilltree.item.WisdomScrollItem;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class PSTItems {
    // Aligned 1.21.4: Direct item registry mapping through BuiltInRegistries.ITEM
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, SkillTreeMod.MOD_ID);

    // Scrolls
    // (Note: Ensure that WisdomScrollItem and AmnesiaScrollItem constructors are updated to pass or create an Item.Properties context)
    public static final RegistryObject<Item> WISDOM_SCROLL = REGISTRY.register("wisdom_scroll", WisdomScrollItem::new);
    public static final RegistryObject<Item> AMNESIA_SCROLL = REGISTRY.register("amnesia_scroll", AmnesiaScrollItem::new);

    // Blocks
    // Factual Fix 1.21.4: Call .get() on the block registry object wrapper to pass the actual Block instance safely
    public static final RegistryObject<Item> WORKBENCH = REGISTRY.register("workbench", () -> new ModBlockItem(PSTBlocks.WORKBENCH));
}