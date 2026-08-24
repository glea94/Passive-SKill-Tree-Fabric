package daripher.skilltree.init.predicate;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.predicate.enchantment.ArmorEnchantmentCondition;
import daripher.skilltree.skill.bonus.predicate.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.predicate.enchantment.NoneEnchantmentCondition;
import daripher.skilltree.skill.bonus.predicate.enchantment.WeaponEnchantmentCondition;
import net.minecraft.resources.Identifier;
import daripher.skilltree.util.registry.DeferredRegister;
import daripher.skilltree.util.registry.RegistryObject;

import java.util.List;
import java.util.Objects;

public class PSTEnchantmentPredicates {
    public static final Identifier REGISTRY_ID = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "enchantment_conditions");
    public static final DeferredRegister<EnchantmentCondition.Serializer> REGISTRY = DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

    public static final RegistryObject<EnchantmentCondition.Serializer> NONE = REGISTRY.register("none", NoneEnchantmentCondition.Serializer::new);
    public static final RegistryObject<EnchantmentCondition.Serializer> ARMOR = REGISTRY.register("armor", ArmorEnchantmentCondition.Serializer::new);
    public static final RegistryObject<EnchantmentCondition.Serializer> WEAPON = REGISTRY.register("weapon", WeaponEnchantmentCondition.Serializer::new);

    public static List<EnchantmentCondition> conditionsList() {
        
        return PSTRegistries.ENCHANTMENT_CONDITIONS.get().getValues().stream()
                .map(EnchantmentCondition.Serializer::createDefaultInstance)
                .toList();
    }

    public static String getName(EnchantmentCondition condition) {
        Identifier id = PSTRegistries.ENCHANTMENT_CONDITIONS.get().getKey(condition.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
    }
}
