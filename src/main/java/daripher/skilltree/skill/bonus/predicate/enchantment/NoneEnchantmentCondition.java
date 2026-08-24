package daripher.skilltree.skill.bonus.predicate.enchantment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.predicate.PSTEnchantmentPredicates;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public enum NoneEnchantmentCondition implements EnchantmentCondition {
    INSTANCE;

    @Override
    public boolean met(ItemStack stack) {
        return true;
    }

    @Override
    public EnchantmentCondition.Serializer getSerializer() {
        return PSTEnchantmentPredicates.NONE.get();
    }

    public static class Serializer implements EnchantmentCondition.Serializer {
        @Override
        public EnchantmentCondition deserialize(JsonObject json) throws JsonParseException {
            return NoneEnchantmentCondition.INSTANCE;
        }

        @Override
        public void serialize(JsonObject json, EnchantmentCondition condition) {
            if (condition != NoneEnchantmentCondition.INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public EnchantmentCondition deserialize(CompoundTag tag) {
            return NoneEnchantmentCondition.INSTANCE;
        }

        @Override
        public CompoundTag serialize(EnchantmentCondition condition) {
            if (condition != NoneEnchantmentCondition.INSTANCE) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }


        @Override
        public EnchantmentCondition deserialize(RegistryFriendlyByteBuf buf) {
            return NoneEnchantmentCondition.INSTANCE;
        }


        @Override
        public void serialize(RegistryFriendlyByteBuf buf, EnchantmentCondition condition) {
            if (condition != NoneEnchantmentCondition.INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public EnchantmentCondition createDefaultInstance() {
            return NoneEnchantmentCondition.INSTANCE;
        }
    }
}
