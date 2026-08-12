package daripher.skilltree.init.predicate;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.predicate.damage.*;
import net.minecraft.resources.Identifier;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;

import java.util.List;
import java.util.Objects;

public class PSTDamagePredicates {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation(SkillTreeMod.MOD_ID, "damage_conditions");
=======
    public static final Identifier REGISTRY_ID = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "damage_conditions");
>>>>>>> Stashed changes
=======
    public static final Identifier REGISTRY_ID = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "damage_conditions");
>>>>>>> Stashed changes
    public static final DeferredRegister<DamageCondition.Serializer> REGISTRY = DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

    public static final RegistryObject<DamageCondition.Serializer> NONE = REGISTRY.register("none", NoneDamageCondition.Serializer::new);
    public static final RegistryObject<DamageCondition.Serializer> PROJECTILE = REGISTRY.register("projectile", ProjectileDamageCondition.Serializer::new);
    public static final RegistryObject<DamageCondition.Serializer> MELEE = REGISTRY.register("melee", MeleeDamageCondition.Serializer::new);
    public static final RegistryObject<DamageCondition.Serializer> MAGIC = REGISTRY.register("magic", MagicDamageCondition.Serializer::new);
    public static final RegistryObject<DamageCondition.Serializer> FALL = REGISTRY.register("fall", FallDamageCondition.Serializer::new);
    public static final RegistryObject<DamageCondition.Serializer> FIRE = REGISTRY.register("fire", FireDamageCondition.Serializer::new);
    public static final RegistryObject<DamageCondition.Serializer> POISON = REGISTRY.register("poison", PoisonDamageCondition.Serializer::new);
    public static final RegistryObject<DamageCondition.Serializer> THORNS = REGISTRY.register("thorns", ThornsDamageCondition.Serializer::new);

    public static List<DamageCondition> conditionsList() {
        // Alignment 1.21.4: Stream registry values directly to populate condition dropdown maps cleanly
        return PSTRegistries.DAMAGE_CONDITIONS.get().getValues().stream()
                .map(DamageCondition.Serializer::createDefaultInstance)
                .toList();
    }

    public static String getName(DamageCondition condition) {
        Identifier id = PSTRegistries.DAMAGE_CONDITIONS.get().getKey(condition.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
    }
}
