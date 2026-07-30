package daripher.skilltree.event;

import daripher.skilltree.util.event.PSTEventBus;

/**
 * Point central d'enregistrement/déclenchement de tous les events "maison" du mod qui remplacent
 * des events Forge sans équivalent direct dans Fabric API. Un bus par type d'event, comme des
 * "canaux" séparés plutôt qu'un event bus global filtré par type comme chez Forge - même résultat.
 * <p>
 * État actuel (étape events, en cours) : LIVING_HURT est le premier porté (8 handlers Forge
 * l'utilisaient). Les ~18 autres types d'event du mod seront ajoutés ici au fur et à mesure des
 * prochaines étapes (voir le tableau de classification donné dans le chat).
 */
public class PSTEvents {
    public static final PSTEventBus<LivingHurtPSTEvent> LIVING_HURT = new PSTEventBus<>();
    public static final PSTEventBus<CriticalHitPSTEvent> CRITICAL_HIT = new PSTEventBus<>();
    public static final PSTEventBus<BreakSpeedPSTEvent> BREAK_SPEED = new PSTEventBus<>();
    public static final PSTEventBus<LivingFallPSTEvent> LIVING_FALL = new PSTEventBus<>();
    public static final PSTEventBus<LivingVisibilityPSTEvent> LIVING_VISIBILITY = new PSTEventBus<>();
    public static final PSTEventBus<LivingEquipmentChangePSTEvent> LIVING_EQUIPMENT_CHANGE = new PSTEventBus<>();
    public static final PSTEventBus<AnvilUpdatePSTEvent> ANVIL_UPDATE = new PSTEventBus<>();
    public static final PSTEventBus<LivingHealPSTEvent> LIVING_HEAL = new PSTEventBus<>();
    public static final PSTEventBus<LivingAttackPSTEvent> LIVING_ATTACK = new PSTEventBus<>();
    public static final PSTEventBus<MobEffectApplicablePSTEvent> MOB_EFFECT_APPLICABLE = new PSTEventBus<>();
    public static final PSTEventBus<MobEffectAddedPSTEvent> MOB_EFFECT_ADDED = new PSTEventBus<>();
    public static final PSTEventBus<LivingExperienceDropPSTEvent> LIVING_EXPERIENCE_DROP = new PSTEventBus<>();
    public static final PSTEventBus<ItemTooltipPSTEvent> ITEM_TOOLTIP = new PSTEventBus<>();
    public static final PSTEventBus<LivingEntityUseItemFinishPSTEvent> ITEM_USE_FINISH = new PSTEventBus<>();
}
