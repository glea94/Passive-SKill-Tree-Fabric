package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.multiplier.FloatFunctionMultiplier;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import net.minecraft.resources.Identifier;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;

import java.util.List;
import java.util.Objects;

public class PSTLivingMultipliers {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation(SkillTreeMod.MOD_ID, "skill_bonus_multipliers");
=======
    public static final Identifier REGISTRY_ID = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "skill_bonus_multipliers");
>>>>>>> Stashed changes
=======
    public static final Identifier REGISTRY_ID = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "skill_bonus_multipliers");
>>>>>>> Stashed changes
    public static final DeferredRegister<LivingMultiplier.Serializer> REGISTRY = DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

    public static final RegistryObject<LivingMultiplier.Serializer> NONE = REGISTRY.register("none", NoneLivingMultiplier.Serializer::new);
    public static final RegistryObject<LivingMultiplier.Serializer> NUMERIC_VALUE = REGISTRY.register("numeric_value", FloatFunctionMultiplier.Serializer::new);

    public static List<LivingMultiplier> multiplierList() {
        // Alignment 1.21.4: Streams data structures through custom registry endpoints safely
        return PSTRegistries.LIVING_MULTIPLIERS.get().getValues().stream()
                .map(LivingMultiplier.Serializer::createDefaultInstance)
                .toList();
    }

    public static String getName(LivingMultiplier condition) {
        Identifier id = PSTRegistries.LIVING_MULTIPLIERS.get().getKey(condition.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
    }
}
