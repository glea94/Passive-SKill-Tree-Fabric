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

    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, SkillTreeMod.MOD_ID);



    public static final RegistryObject<Item> WISDOM_SCROLL = REGISTRY.register("wisdom_scroll", WisdomScrollItem::new);
    public static final RegistryObject<Item> AMNESIA_SCROLL = REGISTRY.register("amnesia_scroll", AmnesiaScrollItem::new);



    public static final RegistryObject<Item> WORKBENCH = REGISTRY.register("workbench", () -> new ModBlockItem(PSTBlocks.WORKBENCH));
}