package daripher.skilltree.init;

import daripher.skilltree.init.predicate.*;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectPredicate;
import daripher.skilltree.skill.bonus.predicate.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.requirement.SkillRequirement;
import daripher.skilltree.util.registry.DeferredRegister;

import java.util.function.Supplier;

public class PSTRegistries {
    
    public static final Supplier<DeferredRegister<SkillBonus.Serializer>> SKILL_BONUSES = () -> PSTSkillBonuses.REGISTRY;
    public static final Supplier<DeferredRegister<LivingMultiplier.Serializer>> LIVING_MULTIPLIERS = () -> PSTLivingMultipliers.REGISTRY;
    public static final Supplier<DeferredRegister<LivingEntityPredicate.Serializer>> LIVING_CONDITIONS = () -> PSTLivingEntityPredicates.REGISTRY;
    public static final Supplier<DeferredRegister<DamageCondition.Serializer>> DAMAGE_CONDITIONS = () -> PSTDamagePredicates.REGISTRY;
    public static final Supplier<DeferredRegister<ItemStackPredicate.Serializer>> ITEM_CONDITIONS = () -> PSTItemPredicates.REGISTRY;
    public static final Supplier<DeferredRegister<EnchantmentCondition.Serializer>> ENCHANTMENT_CONDITIONS = () -> PSTEnchantmentPredicates.REGISTRY;
    public static final Supplier<DeferredRegister<SkillEventListener.Serializer>> EVENT_LISTENERS = () -> PSTEventListeners.REGISTRY;
    public static final Supplier<DeferredRegister<FloatFunction.Serializer>> FLOAT_FUNCTIONS = () -> PSTFloatFunctions.REGISTRY;
    public static final Supplier<DeferredRegister<SkillRequirement.Serializer>> SKILL_REQUIREMENTS = () -> PSTSkillRequirements.REGISTRY;
    public static final Supplier<DeferredRegister<ItemBonus.Serializer>> ITEM_BONUSES = () -> PSTItemBonuses.REGISTRY;
    public static final Supplier<DeferredRegister<MobEffectPredicate.Serializer>> MOB_EFFECT_PREDICATES = () -> PSTMobEffectPredicates.REGISTRY;
}
