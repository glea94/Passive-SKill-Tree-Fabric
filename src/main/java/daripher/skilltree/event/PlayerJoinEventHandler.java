package daripher.skilltree.event;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.inventory.menu.WorkbenchMenu;
import daripher.skilltree.network.ServerNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import java.util.List;
public class PlayerJoinEventHandler {
    public static void register() {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> WorkbenchMenu.clearRecipeCache());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            server.execute(() -> {
                ServerNetworking.sendSyncWorkbenchRecipes(player);
                replayInventoryChangedCriteria(player);
                ServerNetworking.sendSyncServerData(player);
                if (PlayerSkillsProvider.hasSkills(player)) {
                    PlayerSkillsProvider.KEY.sync(player);
                    for (MobEffectInstance effectInstance : player.getActiveEffects()) {
                        player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effectInstance, false));
                    }
                    syncPlayerAttributesOnly(player);
                }
            });
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                if (PlayerSkillsProvider.hasSkills(oldPlayer) && PlayerSkillsProvider.hasSkills(newPlayer)) {
                    IPlayerSkills oldSkills = PlayerSkillsProvider.get(oldPlayer);
                    IPlayerSkills newSkills = PlayerSkillsProvider.get(newPlayer);
                    newSkills.setSkillPoints(oldSkills.getSkillPoints());
                    newSkills.getPlayerSkills().clear();
                    newSkills.getPlayerSkills().addAll(oldSkills.getPlayerSkills());
                    PlayerSkillsProvider.KEY.sync(newPlayer);
                }
            }
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (newPlayer.level().getServer() != null) {
                newPlayer.level().getServer().execute(() -> {
                    syncPlayerAttributesOnly(newPlayer);
                    newPlayer.setHealth(newPlayer.getMaxHealth());
                });
            }
        });
    }
    private static void replayInventoryChangedCriteria(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty()) {
                CriteriaTriggers.INVENTORY_CHANGED.trigger(player, inventory, stack);
            }
        }
    }
    private static void syncPlayerAttributesOnly(ServerPlayer player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(), List.of(maxHealth)));
        }
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(), List.of(speed)));
        }
    }
    public static void forceRemoveIcon(ServerPlayer player, net.minecraft.world.effect.MobEffect effect) {
        if (player != null && player.connection != null) {
            player.connection.send(new ClientboundRemoveMobEffectPacket(player.getId(), BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect)));
        }
    }
}