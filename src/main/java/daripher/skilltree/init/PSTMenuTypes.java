package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.inventory.menu.WorkbenchMenu;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class PSTMenuTypes {
    // Aligned 1.21.4: Direct registry linking with BuiltInRegistries.MENU
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, SkillTreeMod.MOD_ID);

    // Artisan Workbench container type registration
    public static final RegistryObject<MenuType<WorkbenchMenu>> ARTISAN_WORKBENCH = REGISTRY.register("artisan_workbench",
            () -> new MenuType<>(WorkbenchMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
