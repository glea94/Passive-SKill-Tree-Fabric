// Fichier : src/main/java/daripher/skilltree/client/SkillTreeModClient.java
package daripher.skilltree.client;

import daripher.skilltree.client.event.PoisonedWeaponClientEvents;
import daripher.skilltree.client.network.ClientNetworking;
import daripher.skilltree.client.screen.menu.WorkbenchScreen;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.data.client.SkillTexturesData;
import daripher.skilltree.init.PSTMenuTypes;
import daripher.skilltree.init.client.PSTKeybinds;
import daripher.skilltree.skill.bonus.handler.ItemUseMovementSpeedBonusHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class SkillTreeModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfig.load();
        MenuScreens.register(PSTMenuTypes.ARTISAN_WORKBENCH.get(), WorkbenchScreen::new);
        ClientNetworking.register();
        PoisonedWeaponClientEvents.register();
        ItemUseMovementSpeedBonusHandler.register();
        SkillTexturesData.register();
        PSTKeybinds.register();
    }
}