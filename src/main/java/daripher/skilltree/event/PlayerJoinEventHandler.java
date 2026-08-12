package daripher.skilltree.event;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.network.ServerNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.registries.BuiltInRegistries;
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
        // 1. RECONNEXION : SYNCHRONISATION TECHNIQUE SANS ÉCRASER LA VIE SAUVEGARDÉE
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();

            server.execute(() -> {
                // Ajouté 1.21.5 : synchronise les recettes Workbench vers le client (RecipeAccess client ne les expose plus)
                ServerNetworking.sendSyncWorkbenchRecipes(player);

                // Fix 26.2 : synchronise les arbres de compétences et skills vers le client (jamais appelé auparavant,
                // invisible en solo car client/serveur partagent la même JVM, mais requis sur un serveur dédié)
                ServerNetworking.sendSyncServerData(player);

                if (PlayerSkillsProvider.hasSkills(player)) {
                    // Synchronise l'arbre de compétences Cardinal Components avec le client
                    PlayerSkillsProvider.KEY.sync(player);

                    // Nettoyage réglementaire des paquets d'effets fantômes
                    for (MobEffectInstance effectInstance : player.getActiveEffects()) {
                        // Factual Fix 1.21.4: Update constructor mapping to follow modern status effect packets
                        player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effectInstance, false));
                    }

                    // Envoie les attributs au client sans soigner le joueur
                    syncPlayerAttributesOnly(player);
                }
            });
        });

        // 2. CORRECTION DE LA MORT : TRANSFERT DES DONNÉES À LA RÉAPPARITION
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                if (PlayerSkillsProvider.hasSkills(oldPlayer) && PlayerSkillsProvider.hasSkills(newPlayer)) {
                    IPlayerSkills oldSkills = PlayerSkillsProvider.get(oldPlayer);
                    IPlayerSkills newSkills = PlayerSkillsProvider.get(newPlayer);

                    // Copie stricte des points et compétences de l'ancien vers le nouveau corps
                    newSkills.setSkillPoints(oldSkills.getSkillPoints());
                    newSkills.getPlayerSkills().clear();
                    newSkills.getPlayerSkills().addAll(oldSkills.getPlayerSkills());

                    // Synchronise le nouveau conteneur Cardinal Components pour le joueur vivant
                    PlayerSkillsProvider.KEY.sync(newPlayer);
                }
            }
        });

        // 3. RÉAPPARITION : SOIGNER LE JOUEUR UNIQUE À LA MORT POUR REMPLIR SA NOUVELLE BARRE
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (newPlayer.level().getServer() != null) {
                newPlayer.level().getServer().execute(() -> {
                    // À la résurrection uniquement, on force la vie au maximum
                    syncPlayerAttributesOnly(newPlayer);
                    newPlayer.setHealth(newPlayer.getMaxHealth());
                });
            }
        });
    }

    // Méthode officielle qui synchronise uniquement les données d'affichages sans forcer le soin
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
            // Factual Fix 1.21.4: Modern payload standard for removing active effects requires standard Holders
            player.connection.send(new ClientboundRemoveMobEffectPacket(player.getId(), BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect)));
        }
    }
}