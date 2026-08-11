package daripher.skilltree.skill.bonus.predicate.enchantment;

import daripher.skilltree.init.PSTRegistries;
<<<<<<< Updated upstream
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
=======
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
>>>>>>> Stashed changes

import java.util.Objects;

public interface EnchantmentCondition {
    boolean met(EnchantmentCategory category);

    default String getDescriptionId() {
        Identifier id = PSTRegistries.ENCHANTMENT_CONDITIONS.get().getKey(getSerializer());
        Objects.requireNonNull(id);
        return "enchantment_condition.%s.%s".formatted(id.getNamespace(), id.getPath());
    }

    EnchantmentCondition.Serializer getSerializer();

    interface Serializer extends daripher.skilltree.data.serializers.Serializer<EnchantmentCondition> {
        EnchantmentCondition createDefaultInstance();
    }
}
