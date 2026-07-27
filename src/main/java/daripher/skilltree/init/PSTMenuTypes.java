package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.inventory.menu.WorkbenchMenu;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

/**
 * Portage Fabric : la partie "enregistrement du type de menu" reste ici (commune
 * client/serveur). L'association écran <-> menu (MenuScreens.register, classe client-only côté
 * rendu) est déplacée dans daripher.skilltree.client.SkillTreeModClient, car MenuScreens
 * n'existe que sur le client et Fabric sépare strictement entrypoint commun/client
 * (contrairement à Forge où @OnlyIn(Dist.CLIENT) suffisait dans la même classe).
 */
public class PSTMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, SkillTreeMod.MOD_ID);

    public static final RegistryObject<MenuType<WorkbenchMenu>> ARTISAN_WORKBENCH = REGISTRY.register("artisan_workbench", () -> new MenuType<>(WorkbenchMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
