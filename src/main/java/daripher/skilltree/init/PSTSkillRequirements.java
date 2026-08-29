package daripher.skilltree.init;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.requirement.*;
import net.minecraft.resources.Identifier;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;
import java.util.List;
import java.util.Objects;
public class PSTSkillRequirements {
    public static final Identifier REGISTRY_ID = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "skill_requirements");
    public static final DeferredRegister<SkillRequirement.Serializer> REGISTRY = DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);
    public static final RegistryObject<SkillRequirement.Serializer> STAT_VALUE = REGISTRY.register("stat_value", StatRequirement.Serializer::new);
    public static final RegistryObject<SkillRequirement.Serializer> NUMERIC_VALUE = REGISTRY.register("numeric_value", NumericValueRequirement.Serializer::new);
    public static final RegistryObject<SkillRequirement.Serializer> ADVANCEMENT = REGISTRY.register("advancement", AdvancementRequirement.Serializer::new);
    public static final RegistryObject<SkillRequirement.Serializer> LEARNED_SKILL = REGISTRY.register("learned_skill", LearnedSkillRequirement.Serializer::new);
    public static final RegistryObject<SkillRequirement.Serializer> NOT_LEARNED_SKILL = REGISTRY.register("not_learned_skill", NotLearnedSkillRequirement.Serializer::new);
    public static final RegistryObject<SkillRequirement.Serializer> MACE_MASTERY_KILLS = REGISTRY.register("mace_mastery_kills", MaceMasteryKillsRequirement.Serializer::new);
    @SuppressWarnings("rawtypes")
    public static List<SkillRequirement> requirementList() {
        return PSTRegistries.SKILL_REQUIREMENTS.get().getValues().stream()
                .map(SkillRequirement.Serializer::createDefaultInstance)
                .map(SkillRequirement.class::cast)
                .toList();
    }
    public static String getName(SkillRequirement<?> bonus) {
        Identifier id = PSTRegistries.SKILL_REQUIREMENTS.get().getKey(bonus.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
    }
}