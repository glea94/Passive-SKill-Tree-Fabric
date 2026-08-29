package daripher.skilltree.skill.bonus.player;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.data.client.SkillTreeEditorData;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
public final class GrantItemBonus implements SkillBonus<GrantItemBonus> {
    private Identifier itemId;
    private int amount;
    public GrantItemBonus(Identifier itemId, int amount) {
        this.itemId = itemId;
        this.amount = amount;
    }
    @Override
    public void onSkillLearned(ServerPlayer player, boolean firstTime) {
        if (firstTime) {
            Item item = BuiltInRegistries.ITEM.get(itemId).map(Holder::value).orElse(null);
            if (item == null) {
                SkillTreeEditorData.sendChatMessage("Unknown item: " + itemId, ChatFormatting.DARK_RED);
                return;
            }
            int amountLeft = amount;
            while (amountLeft > 64) {
                amountLeft -= 64;
                player.addItem(new ItemStack(item, 64));
            }
            if (amountLeft > 0) {
                player.addItem(new ItemStack(item, amountLeft));
            }
        }
    }
    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.GRANT_ITEM.get();
    }
    @Override
    public GrantItemBonus copy() {
        return new GrantItemBonus(itemId, amount);
    }
    @Override
    public GrantItemBonus multiply(double multiplier) {
        amount = (int) (amount * multiplier);
        return this;
    }
    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof GrantItemBonus otherBonus)) {
            return false;
        }
        return Objects.equals(otherBonus.itemId, this.itemId);
    }
    @Override
    public GrantItemBonus merge(SkillBonus<?> other) {
        if (!(other instanceof GrantItemBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        return new GrantItemBonus(itemId, amount + otherBonus.amount);
    }
    @Override
    public MutableComponent getSimpleTooltip() {
        Item item = BuiltInRegistries.ITEM.get(itemId).map(Holder::value).orElse(null);
        if (item == null) {
            return Component.literal("Unknown item: " + itemId).withStyle(ChatFormatting.DARK_RED);
        }
        Style style = TooltipHelper.getSkillBonusStyle(isPositive());
        Component itemDescription = item.getName(item.getDefaultInstance());
        if (amount > 1) {
            String amountDescription = TooltipHelper.formatNumber(amount);
            return Component.translatable(getDescriptionId() + ".amount", amountDescription, itemDescription).withStyle(style);
        } else {
            return Component.translatable(getDescriptionId(), itemDescription).withStyle(style);
        }
    }
    @Override
    public boolean isPositive() {
        return true;
    }
    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<GrantItemBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 90, 14, amount).setNumericFilter(v -> v > 0 && v % 1 == 0)
                .setNumericResponder(value -> selectAmount(consumer, value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Item", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        List<Identifier> items = BuiltInRegistries.ITEM.entrySet().stream().map(Map.Entry::getKey).map(ResourceKey::identifier)
                .toList();
        editor.addSelectionMenu(0, 0, 200, items).setValue(itemId).setElementNameGetter(id -> Component.literal(id.toString()))
                .setResponder(id -> selectItemId(id, consumer));
        editor.increaseHeight(19);
    }
    private void selectItemId(Identifier id, Consumer<GrantItemBonus> consumer) {
        setItemId(id);
        consumer.accept(this.copy());
    }
    public void setItemId(Identifier itemId) {
        this.itemId = itemId;
    }
    private void selectAmount(Consumer<GrantItemBonus> consumer, Double value) {
        setAmount(value.intValue());
        consumer.accept(this.copy());
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public GrantItemBonus deserialize(JsonObject json) throws JsonParseException {
            Identifier itemId = Identifier.parse(json.get("item_id").getAsString());
            int amount = SerializationHelper.getElement(json, "amount").getAsInt();
            return new GrantItemBonus(itemId, amount);
        }
        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("item_id", aBonus.itemId.toString());
            json.addProperty("amount", aBonus.amount);
        }
        @Override
        public GrantItemBonus deserialize(CompoundTag tag) {
            Identifier itemId = Identifier.parse(tag.getString("item_id").orElse(""));
            int amount = tag.getInt("amount").orElse(0);
            return new GrantItemBonus(itemId, amount);
        }
        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("item_id", aBonus.itemId.toString());
            tag.putInt("amount", aBonus.amount);
            return tag;
        }
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
        @Override
        public GrantItemBonus deserialize(RegistryFriendlyByteBuf buf) {
            Identifier itemId = buf.readIdentifier();
            int amount = buf.readInt();
            return new GrantItemBonus(itemId, amount);
        }
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
        @Override
        public void serialize(RegistryFriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantItemBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeIdentifier(aBonus.itemId);
            buf.writeInt(aBonus.amount);
        }
        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new GrantItemBonus(BuiltInRegistries.ITEM.getKey(Items.DIAMOND), 64);
        }
    }
}