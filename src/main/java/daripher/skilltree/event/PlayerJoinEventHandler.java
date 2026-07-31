package daripher.skilltree.event;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import java.util.List;

public class PlayerJoinEventHandler {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();

            server.execute(() -> {
                if (PlayerSkillsProvider.hasSkills(player)) {
                    // 1. Force Cardinal Components à synchroniser l'arbre au client
                    PlayerSkillsProvider.KEY.sync(player);

                    // 2. NETTOYAGE DES EFFETS FANTÔMES (Sans détruire les cœurs)
                    for (MobEffectInstance effectInstance : player.getActiveEffects()) {
                        player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effectInstance));
                    }

                    // 3. SYNCHRONISATION FORCÉE DES CŒURS (Health Boost)
                    AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
                    if (maxHealth != null) {
                        // Envoi direct du paquet d'attribut officiel à l'écran du joueur
                        player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(), List.of(maxHealth)));

                        // Aligne les points de vie actuels sur le maximum
                        player.setHealth(player.getMaxHealth());
                    }
                }
            });
        });
    }

    public static void forceRemoveIcon(ServerPlayer player, net.minecraft.world.effect.MobEffect effect) {
        if (player != null && player.connection != null) {
            player.connection.send(new ClientboundRemoveMobEffectPacket(player.getId(), effect));
        }
    }
}
