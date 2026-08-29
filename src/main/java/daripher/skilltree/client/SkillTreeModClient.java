package daripher.skilltree.client;
import daripher.skilltree.client.event.MaceMasteryTooltipClientEvents;
import daripher.skilltree.client.event.PoisonedWeaponClientEvents;
import daripher.skilltree.client.hud.SkillProgressHudOverlay;
import daripher.skilltree.client.network.ClientNetworking;
import daripher.skilltree.client.screen.menu.WorkbenchScreen;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.data.client.SkillTexturesData;
import daripher.skilltree.init.PSTMenuTypes;
import daripher.skilltree.init.client.PSTKeybinds;
import daripher.skilltree.skill.bonus.handler.ItemUseMovementSpeedBonusHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.Identifier;
public class SkillTreeModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfig.load();
        MenuScreens.register(PSTMenuTypes.ARTISAN_WORKBENCH.get(), WorkbenchScreen::new);
        ClientNetworking.register();
        PoisonedWeaponClientEvents.register();
        MaceMasteryTooltipClientEvents.register();
        ItemUseMovementSpeedBonusHandler.register();
        SkillTexturesData.register();
        PSTKeybinds.register();
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.INFO_BAR,
                Identifier.fromNamespaceAndPath("skilltree", "skill_progress_hud"),
                SkillProgressHudOverlay::render);
    }
}