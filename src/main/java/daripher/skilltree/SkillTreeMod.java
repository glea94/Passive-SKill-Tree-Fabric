package daripher.skilltree;

import daripher.skilltree.command.PSTCommands;
import daripher.skilltree.compat.trinkets.TrinketsCompatibility;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.init.*;
import daripher.skilltree.init.predicate.*;
import daripher.skilltree.network.ServerNetworking;
import daripher.skilltree.skill.bonus.handler.*;
import daripher.skilltree.skill.bonus.item.ItemBonusHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Portage Fabric de la classe principale du mod (remplace @Mod + le constructeur
 * FMLJavaModLoadingContext de Forge par ModInitializer.onInitialize()).
 */
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

    /**
     * Sous Forge, chaque DeferredRegister s'enregistrait sur l'event bus. Sous Fabric,
     * l'enregistrement de chaque registry se fait dès le chargement de sa classe (voir
     * daripher.skilltree.util.registry.DeferredRegister) : il suffit donc de "toucher" chaque
     * classe PSTxxx pour déclencher son initialisation statique, dans le même ordre que
     * l'original pour rester lisible.
     */
    private static void registerModRegistries() {
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
        touch(PSTRegistries.class);
        PSTBrewingRecipes.addRecipes();
        PSTLootModifiers.register();
        SkillsReloader.register();
        SkillTreesReloader.register();
    }

    private static void registerConfigs() {
        ServerConfig.load();
    }

    /**
     * Tous les handlers qui utilisaient @Mod.EventBusSubscriber côté Forge s'enregistrent ici
     * explicitement (voir chaque handler pour le détail de son portage individuel, étape 5).
     */
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
    }

    private static void registerCommands() {
        PSTCommands.register();
    }

    private static void registerNetwork() {
        ServerNetworking.register();
    }

    private static void registerCompatibilities() {
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            LOGGER.info("Trinkets detected, enabling accessory slot compatibility");
        }
        // Inconditionnel, comme côté Forge où PSTSkillBonuses.REGISTRY.register("curio_slots", ...)
        // s'exécutait toujours : le bonus existe dans le registre même sans Trinkets installé,
        // il ne fait simplement rien de spécial dans ce cas.
        touch(TrinketsCompatibility.class);
    }

    private static void touch(Class<?> clazz) {
        try {
            Class.forName(clazz.getName(), true, clazz.getClassLoader());
        } catch (ClassNotFoundException exception) {
            LOGGER.error("Couldn't initialize {}", clazz.getName(), exception);
        }
    }
}
