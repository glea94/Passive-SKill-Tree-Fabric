package daripher.skilltree.skill.bonus.predicate.enchantment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.predicate.PSTEnchantmentPredicates;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class WeaponEnchantmentCondition implements EnchantmentCondition {

    @Override
    public boolean met(ItemStack stack) {
        return stack.is(ItemTags.WEAPON_ENCHANTABLE)
                || stack.is(ItemTags.BOW_ENCHANTABLE)
                || stack.is(ItemTags.CROSSBOW_ENCHANTABLE)
                || stack.is(ItemTags.TRIDENT_ENCHANTABLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSerializer());
    }

    @Override
    public EnchantmentCondition.Serializer getSerializer() {
        return PSTEnchantmentPredicates.WEAPON.get();
    }

    public static class Serializer implements EnchantmentCondition.Serializer {
        @Override
        public EnchantmentCondition deserialize(JsonObject json) throws JsonParseException {
            return new WeaponEnchantmentCondition();
        }

        @Override
        public void serialize(JsonObject json, EnchantmentCondition condition) {
            if (!(condition instanceof WeaponEnchantmentCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public EnchantmentCondition deserialize(CompoundTag tag) {
            return new WeaponEnchantmentCondition();
        }

        @Override
        public CompoundTag serialize(EnchantmentCondition condition) {
            if (!(condition instanceof WeaponEnchantmentCondition)) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }
<<<<<<< Updated upstream

        
=======
>>>>>>> Stashed changes
        @Override
        public EnchantmentCondition deserialize(RegistryFriendlyByteBuf buf) {
            return new WeaponEnchantmentCondition();
        }
<<<<<<< Updated upstream

        
=======
>>>>>>> Stashed changes
        @Override
        public void serialize(RegistryFriendlyByteBuf buf, EnchantmentCondition condition) {
            if (!(condition instanceof WeaponEnchantmentCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public EnchantmentCondition createDefaultInstance() {
            return new WeaponEnchantmentCondition();
        }
    }
}
