package daripher.skilltree;

import daripher.skilltree.command.PSTCommands;
import daripher.skilltree.compat.trinkets.TrinketsCompatibility;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.init.*;
import daripher.skilltree.init.predicate.*;
import daripher.skilltree.network.PSTNetworkChannels;
import daripher.skilltree.network.ServerNetworking;
import daripher.skilltree.skill.bonus.handler.*;
import daripher.skilltree.skill.bonus.item.ItemBonusHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SkillTreeMod implements ModInitializer {
    public static final String MOD_ID = "skilltree";
    public static final Logger LOGGER = LogManager.getLogger(SkillTreeMod.MOD_ID);

    @Override
    public void onInitialize() {
        registerModRegistries();
        registerConfigs();
        registerEventHandlers();
        registerNetwork();
        registerCommands();
        registerCompatibilities();
    }

    private static void registerModRegistries() {
        touch(PSTRegistries.class);
        touch(PSTStats.class);

        touch(PSTItems.class);
        touch(PSTBlocks.class);
        touch(PSTMobEffects.class);
        touch(PSTPotions.class);
        touch(PSTCreativeTabs.class);
        touch(PSTDamageTypes.class);
        touch(PSTEnchantmentCategories.class);
        touch(PSTTags.class);
        touch(PSTRecipeSerializers.class);
        touch(PSTRecipeTypes.class);
        touch(PSTMenuTypes.class);
        touch(PSTSkillBonuses.class);
        touch(PSTLivingMultipliers.class);
        touch(PSTLivingEntityPredicates.class);
        touch(PSTDamagePredicates.class);
        touch(PSTItemPredicates.class);
        touch(PSTEnchantmentPredicates.class);
        touch(PSTMobEffectPredicates.class);
        touch(PSTEventListeners.class);
        touch(PSTFloatFunctions.class);
        touch(PSTSkillRequirements.class);
        touch(PSTItemBonuses.class);

        PSTBrewingRecipes.addRecipes();
        PSTLootModifiers.register();
        SkillsReloader.register();
        SkillTreesReloader.register();
    }

    private static void registerConfigs() {
        ServerConfig.load();
    }

    private static void registerEventHandlers() {
        OutgoingDamageBonusHandler.register();
        IncomingDamageBonusHandler.register();
        DamageConversionBonusHandler.register();
        SkillBonusHandlerUtils.register();
        CriticalHitChanceBonusHandler.register();
        CriticalHitDamageBonusHandler.register();
        ArrowRetrievalChanceBonusHandler.register();
        ProjectileSpeedBonusHandler.register();
        ProjectileDuplicationBonusHandler.register();
        TickingSkillBonusHandler.register();
        BlockBreakSpeedBonusHandler.register();
        IncomingHealingBonusHandler.register();
        HealthReservationBonusHandler.register();
        DamageAvoidanceBonusHandler.register();
        CheatDeathBonusHandler.register();
        daripher.skilltree.event.PoisonedWeaponEvents.register();
        EffectImmunityBonusHandler.register();
        EffectImmunityBypassBonusHandler.register();
        EffectDurationBonusHandler.register();
        ExperienceGainMultiplierBonusHandler.register();
        JumpHeightBonusHandler.register();
        StealthBonusHandler.register();
        daripher.skilltree.event.EquipmentChangeDetector.register();
        ItemBonusHandler.register();
        ItemUsagePreventionBonusHandler.register();
        EventListenerBonusHandler.register();
        GrindstoneBonusHandler.register();
        RepairEfficiencyBonusHandler.register();
        ItemUsageSpeedBonusHandler.register();
        daripher.skilltree.event.PlayerJoinEventHandler.register();
    }

    private static void registerCommands() {
        PSTCommands.register();
    }

    private static void registerNetwork() {
        PSTNetworkChannels.register();
        ServerNetworking.register();
    }

    private static void registerCompatibilities() {
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            LOGGER.info("Trinkets detected, enabling accessory slot compatibility");
        }
        touch(TrinketsCompatibility.class);
    }

    private static void touch(Class<?> clazz) {
        try {
            Class.forName(clazz.getName(), true, clazz.getClassLoader());
        } catch (ClassNotFoundException exception) {
            LOGGER.error("Couldn't initialize class {}", clazz.getName(), exception);
        }
    }
}