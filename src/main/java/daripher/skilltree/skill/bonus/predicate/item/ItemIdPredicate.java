package daripher.skilltree.skill.bonus.predicate.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.predicate.PSTItemPredicates;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
<<<<<<< Updated upstream
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
=======
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Objects;
import java.util.function.Consumer;

public final class ItemIdPredicate implements ItemStackPredicate {
    private Identifier id;

    public ItemIdPredicate(Identifier id) {
        this.id = id;
    }

    @Override
    public boolean test(ItemStack stack) {
        return BuiltInRegistries.ITEM.get(id) == stack.getItem();
    }

    @Override
    public String getDescriptionId() {
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item != null) {
            return item.getDescriptionId();
        }
        return ItemStackPredicate.super.getDescriptionId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ItemIdPredicate that = (ItemIdPredicate) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public ItemStackPredicate.Serializer getSerializer() {
        return PSTItemPredicates.ITEM_ID.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemStackPredicate> consumer) {
        editor.addLabel(0, 0, "Item Id", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addTextField(0, 0, 200, 14, id.toString()).setSoftFilter(ItemIdPredicate::isItemId)
                .setResponder(text -> selectItemId(consumer, text));
        editor.increaseHeight(19);
    }

    private void selectItemId(Consumer<ItemStackPredicate> consumer, String text) {
<<<<<<< Updated upstream
        setId(new ResourceLocation(text));
=======
        setId(Identifier.parse(text));
>>>>>>> Stashed changes
        consumer.accept(this);
    }

    private static boolean isItemId(String text) {
<<<<<<< Updated upstream
        if (!ResourceLocation.isValidResourceLocation(text)) {
            return false;
        }
        return BuiltInRegistries.ITEM.containsKey(new ResourceLocation(text));
=======
        if (Identifier.tryParse(text) == null) {
            return false;
        }
        return BuiltInRegistries.ITEM.containsKey(Identifier.parse(text));
>>>>>>> Stashed changes
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public static class Serializer implements ItemStackPredicate.Serializer {
        @Override
        public ItemStackPredicate deserialize(JsonObject json) throws JsonParseException {
<<<<<<< Updated upstream
            ResourceLocation id = new ResourceLocation(json.get("id").getAsString());
=======
            Identifier id = Identifier.parse(json.get("id").getAsString());
>>>>>>> Stashed changes
            return new ItemIdPredicate(id);
        }

        @Override
        public void serialize(JsonObject json, ItemStackPredicate condition) {
            if (!(condition instanceof ItemIdPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("id", aCondition.id.toString());
        }

        @Override
        public ItemStackPredicate deserialize(CompoundTag tag) {
            Tag idTag = tag.get("id");
            Objects.requireNonNull(idTag);
<<<<<<< Updated upstream
            ResourceLocation id = new ResourceLocation(idTag.getAsString());
=======
            // Fix 1.21.5 : Tag.getAsString() renommé Tag.asString(), retourne Optional<String>
            Identifier id = Identifier.parse(idTag.asString().orElseThrow());
>>>>>>> Stashed changes
            return new ItemIdPredicate(id);
        }

        @Override
        public CompoundTag serialize(ItemStackPredicate condition) {
            if (!(condition instanceof ItemIdPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("id", aCondition.id.toString());
            return tag;
        }

        @Override
<<<<<<< Updated upstream
        public ItemStackPredicate deserialize(FriendlyByteBuf buf) {
            return new ItemIdPredicate(new ResourceLocation(buf.readUtf()));
=======
        public ItemStackPredicate deserialize(RegistryFriendlyByteBuf buf) {
            return new ItemIdPredicate(Identifier.parse(buf.readUtf()));
>>>>>>> Stashed changes
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemStackPredicate condition) {
            if (!(condition instanceof ItemIdPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            buf.writeUtf(aCondition.id.toString());
        }

        @Override
        public ItemStackPredicate createDefaultInstance() {
<<<<<<< Updated upstream
            return new ItemIdPredicate(new ResourceLocation("minecraft:shield"));
=======
            return new ItemIdPredicate(Identifier.parse("minecraft:shield"));
>>>>>>> Stashed changes
        }
    }
}
