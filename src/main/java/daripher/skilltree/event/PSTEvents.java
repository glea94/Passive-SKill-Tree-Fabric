package daripher.skilltree.event;
import daripher.skilltree.util.event.PSTEventBus;
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
