package daripher.skilltree.skill.bonus.predicate.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.predicate.PSTItemPredicates;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
=======
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
=======
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Consumer;

public class ItemTagPredicate implements ItemStackPredicate {
    private ResourceLocation tagId;

    public ItemTagPredicate(ResourceLocation tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.is(TagKey.create(Registries.ITEM, tagId));
    }

    @Override
    public String getDescriptionId() {
        return "item_tag.%s".formatted(tagId.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ItemTagPredicate that = (ItemTagPredicate) o;
        return Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId);
    }

    @Override
    public ItemStackPredicate.Serializer getSerializer() {
        return PSTItemPredicates.TAG.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemStackPredicate> consumer) {
        editor.addLabel(0, 0, "Tag", ChatFormatting.GREEN);
        editor.increaseHeight(19);
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        editor.addTextField(0, 0, 200, 14, tagId.toString()).setSoftFilter(ResourceLocation::isValidResourceLocation)
=======
        editor.addTextField(0, 0, 200, 14, tagId.toString()).setSoftFilter(text -> Identifier.tryParse(text) != null)
>>>>>>> Stashed changes
=======
        editor.addTextField(0, 0, 200, 14, tagId.toString()).setSoftFilter(text -> Identifier.tryParse(text) != null)
>>>>>>> Stashed changes
                .setResponder(text -> selectTagId(consumer, text));
        editor.increaseHeight(19);
    }

    private void selectTagId(Consumer<ItemStackPredicate> consumer, String text) {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        setTagId(new ResourceLocation(text));
=======
        setTagId(Identifier.parse(text));
>>>>>>> Stashed changes
=======
        setTagId(Identifier.parse(text));
>>>>>>> Stashed changes
        consumer.accept(this);
    }

    public void setTagId(ResourceLocation tagId) {
        this.tagId = tagId;
    }

    public static class Serializer implements ItemStackPredicate.Serializer {
        @Override
        public ItemStackPredicate deserialize(JsonObject json) throws JsonParseException {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
            ResourceLocation tagId = new ResourceLocation(json.get("tag_id").getAsString());
=======
            Identifier tagId = Identifier.parse(json.get("tag_id").getAsString());
>>>>>>> Stashed changes
=======
            Identifier tagId = Identifier.parse(json.get("tag_id").getAsString());
>>>>>>> Stashed changes
            return new ItemTagPredicate(tagId);
        }

        @Override
        public void serialize(JsonObject json, ItemStackPredicate condition) {
            if (!(condition instanceof ItemTagPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("tag_id", aCondition.tagId.toString());
        }

        @Override
        public ItemStackPredicate deserialize(CompoundTag tag) {
            // Factual Fix 1.21.5: getString renvoie désormais Optional<String>
            Identifier tagId = Identifier.parse(tag.getString("tag_id").orElse(""));
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
            return new ItemTagPredicate(tagId);
        }

        @Override
        public CompoundTag serialize(ItemStackPredicate condition) {
            if (!(condition instanceof ItemTagPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("tag_id", aCondition.tagId.toString());
            return tag;
        }

        // Factual Fix 1.21.4: Refactored signature from FriendlyByteBuf to RegistryFriendlyByteBuf
        @Override
        public ItemStackPredicate deserialize(RegistryFriendlyByteBuf buf) {
            Identifier tagId = Identifier.parse(buf.readUtf());
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
            return new ItemTagPredicate(tagId);
        }

        // Factual Fix 1.21.4: Refactored signature from FriendlyByteBuf to RegistryFriendlyByteBuf
        @Override
        public void serialize(RegistryFriendlyByteBuf buf, ItemStackPredicate condition) {
            if (!(condition instanceof ItemTagPredicate aCondition)) {
                throw new IllegalArgumentException();
            }
            buf.writeUtf(aCondition.tagId.toString());
        }

        @Override
        public ItemStackPredicate createDefaultInstance() {
            return new ItemTagPredicate(ItemTags.SWORDS.location());
        }
    }
}