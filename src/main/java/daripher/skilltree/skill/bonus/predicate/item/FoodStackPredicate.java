package daripher.skilltree.skill.bonus.predicate.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.predicate.PSTItemPredicates;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record FoodStackPredicate() implements ItemStackPredicate {
    @Override
    public boolean test(ItemStack stack) {
        // CORRECTION 1.21.1 : On vérifie si l'item possède le Data Component FOOD natif
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

        @Override
        public ItemStackPredicate deserialize(FriendlyByteBuf buf) {
            return new FoodStackPredicate();
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemStackPredicate condition) {
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
