package daripher.skilltree.skill.bonus.predicate.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.predicate.PSTItemPredicates;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record FoodStackPredicate() implements ItemStackPredicate {
    @Override
    public boolean test(ItemStack stack) {
<<<<<<< Updated upstream
        
=======
>>>>>>> Stashed changes
        return stack.has(DataComponents.FOOD);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        return obj.getClass() == this.getClass();
    }

    @Override
    public int hashCode() {
        return getSerializer().hashCode();
    }

    @Override
    public ItemStackPredicate.Serializer getSerializer() {
        return PSTItemPredicates.FOOD.get();
    }

    public static class Serializer implements ItemStackPredicate.Serializer {
        @Override
        public ItemStackPredicate deserialize(JsonObject json) throws JsonParseException {
            return new FoodStackPredicate();
        }

        @Override
        public void serialize(JsonObject json, ItemStackPredicate condition) {
            if (!(condition instanceof FoodStackPredicate)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public ItemStackPredicate deserialize(CompoundTag tag) {
            return new FoodStackPredicate();
        }

        @Override
        public CompoundTag serialize(ItemStackPredicate condition) {
            if (!(condition instanceof FoodStackPredicate)) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }
<<<<<<< Updated upstream

        
=======
>>>>>>> Stashed changes
        @Override
        public ItemStackPredicate deserialize(RegistryFriendlyByteBuf buf) {
            return new FoodStackPredicate();
        }
<<<<<<< Updated upstream

        
=======
>>>>>>> Stashed changes
        @Override
        public void serialize(RegistryFriendlyByteBuf buf, ItemStackPredicate condition) {
            if (!(condition instanceof FoodStackPredicate)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public ItemStackPredicate createDefaultInstance() {
            return new FoodStackPredicate();
        }
    }
}
