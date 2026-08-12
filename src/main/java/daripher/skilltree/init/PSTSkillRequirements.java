package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.requirement.*;
import net.minecraft.resources.ResourceLocation;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;

import java.util.List;
import java.util.Objects;

public class PSTSkillRequirements {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation(SkillTreeMod.MOD_ID, "skill_requirements");
=======
    public static final Identifier REGISTRY_ID = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "skill_requirements");
>>>>>>> Stashed changes
=======
    public static final Identifier REGISTRY_ID = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "skill_requirements");
>>>>>>> Stashed changes
    public static final DeferredRegister<SkillRequirement.Serializer> REGISTRY = DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

    public static final RegistryObject<SkillRequirement.Serializer> STAT_VALUE = REGISTRY.register("stat_value", StatRequirement.Serializer::new);
    public static final RegistryObject<SkillRequirement.Serializer> NUMERIC_VALUE = REGISTRY.register("numeric_value", NumericValueRequirement.Serializer::new);
    public static final RegistryObject<SkillRequirement.Serializer> ADVANCEMENT = REGISTRY.register("advancement", AdvancementRequirement.Serializer::new);
    public static final RegistryObject<SkillRequirement.Serializer> LEARNED_SKILL = REGISTRY.register("learned_skill", LearnedSkillRequirement.Serializer::new);

    @SuppressWarnings("rawtypes")
    public static List<SkillRequirement> requirementList() {
        // Alignment 1.21.4: Streams data structures through custom registry endpoints safely
        return PSTRegistries.SKILL_REQUIREMENTS.get().getValues().stream()
                .map(SkillRequirement.Serializer::createDefaultInstance)
                .map(SkillRequirement.class::cast)
                .toList();
    }

    public static String getName(SkillRequirement<?> bonus) {
        ResourceLocation id = PSTRegistries.SKILL_REQUIREMENTS.get().getKey(bonus.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
    }
}
