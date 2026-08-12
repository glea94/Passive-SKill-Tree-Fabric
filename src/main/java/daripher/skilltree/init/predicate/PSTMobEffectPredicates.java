package daripher.skilltree.init.predicate;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectIdPredicate;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectPredicate;
import daripher.skilltree.skill.bonus.predicate.effect.MobEffectTypePredicate;
import daripher.skilltree.skill.bonus.predicate.effect.NoneMobEffectPredicate;
import net.minecraft.resources.Identifier;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;

import java.util.List;
import java.util.Objects;

public class PSTMobEffectPredicates {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation(SkillTreeMod.MOD_ID, "mob_effect_conditions");
=======
    public static final Identifier REGISTRY_ID = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "mob_effect_conditions");
>>>>>>> Stashed changes
=======
    public static final Identifier REGISTRY_ID = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "mob_effect_conditions");
>>>>>>> Stashed changes
    public static final DeferredRegister<MobEffectPredicate.Serializer> REGISTRY = DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

    public static final RegistryObject<MobEffectPredicate.Serializer> NONE = REGISTRY.register("none", NoneMobEffectPredicate.Serializer::new);
    public static final RegistryObject<MobEffectPredicate.Serializer> EFFECT_CATEGORY = REGISTRY.register("effect_category", MobEffectTypePredicate.Serializer::new);
    public static final RegistryObject<MobEffectPredicate.Serializer> EFFECT_ID = REGISTRY.register("effect_id", MobEffectIdPredicate.Serializer::new);

    public static List<MobEffectPredicate> defaultInstances() {
        // Alignment 1.21.4: Streams data structures through custom registry endpoints safely
        return PSTRegistries.MOB_EFFECT_PREDICATES.get().getValues().stream()
                .map(MobEffectPredicate.Serializer::createDefaultInstance)
                .toList();
    }

    public static String getName(MobEffectPredicate condition) {
        Identifier id = PSTRegistries.MOB_EFFECT_PREDICATES.get().getKey(condition.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
    }
}
