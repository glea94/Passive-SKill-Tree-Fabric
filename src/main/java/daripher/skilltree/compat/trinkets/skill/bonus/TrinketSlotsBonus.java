package daripher.skilltree.compat.trinkets.skill.bonus;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.compat.trinkets.TrinketsCompatibility;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;
import java.util.function.Consumer;

public final class TrinketSlotsBonus implements SkillBonus<TrinketSlotsBonus> {
    private String slotName;
    private int amount;
<<<<<<< Updated upstream
    private final UUID modifierId;
=======
    private final ResourceLocation modifierId;
>>>>>>> Stashed changes

    public TrinketSlotsBonus(String slotName, int amount) {
        this.slotName = slotName;
        this.amount = amount;
<<<<<<< Updated upstream
        this.modifierId = UUID.randomUUID();
    }

    private TrinketSlotsBonus(String slotName, int amount, UUID modifierId) {
=======
        this.modifierId = ResourceLocation.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "trinket_slots_bonus_" + UUID.randomUUID());
    }

    private TrinketSlotsBonus(String slotName, int amount, ResourceLocation modifierId) {
>>>>>>> Stashed changes
        this.slotName = slotName;
        this.amount = amount;
        this.modifierId = modifierId;
    }

    private AttributeInstance getSlotAttributeInstance(ServerPlayer player) {
<<<<<<< Updated upstream
        ResourceLocation attributeId = new ResourceLocation("trinkets", slotName);
        Attribute slotAttribute = BuiltInRegistries.ATTRIBUTE.get(attributeId);
        if (slotAttribute == null) {
=======
        ResourceLocation attributeId = ResourceLocation.fromNamespaceAndPath("trinkets", slotName);
        // Factual Fix 1.21.4: BuiltInRegistries.ATTRIBUTE.get returns an Optional<Holder.Reference<Attribute>>, unwrap straight to a Holder type reference
        Holder<Attribute> slotAttributeHolder = BuiltInRegistries.ATTRIBUTE.get(attributeId)
                .map(holder -> (Holder<Attribute>) (Object) holder)
                .orElse(null);
        if (slotAttributeHolder == null) {
>>>>>>> Stashed changes
            return null;
        }
        // Factual Fix 1.21.4: player.getAttribute strictly takes a Holder<Attribute> as parameter
        return player.getAttribute(slotAttributeHolder);
    }

    @Override
    public void onSkillLearned(ServerPlayer player, boolean firstTime) {
        if (!firstTime) {
            return;
        }
        AttributeInstance attributeInstance = getSlotAttributeInstance(player);
        if (attributeInstance == null) {
            return;
        }
        AttributeModifier modifier = new AttributeModifier(modifierId, amount, AttributeModifier.Operation.ADD_VALUE);
        attributeInstance.addPermanentModifier(modifier);
    }

    @Override
    public void onSkillRemoved(ServerPlayer player) {
        AttributeInstance attributeInstance = getSlotAttributeInstance(player);
        if (attributeInstance == null) {
            return;
        }
        attributeInstance.removeModifier(modifierId);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return TrinketsCompatibility.TRINKET_SLOTS_BONUS.get();
    }

    @Override
    public TrinketSlotsBonus copy() {
        return new TrinketSlotsBonus(slotName, amount, modifierId);
    }

    @Override
    public TrinketSlotsBonus multiply(double multiplier) {
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        return false;
    }

    @Override
    public SkillBonus<TrinketSlotsBonus> merge(SkillBonus<?> other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableComponent getSimpleTooltip() {
        Component slotDescription;
        if (Math.abs(amount) > 1) {
            slotDescription = TooltipHelper.getSlotTooltip(slotName, "plural");
        } else {
            slotDescription = TooltipHelper.getSlotTooltip(slotName);
        }
        MutableComponent tooltip = TooltipHelper.getSkillBonusTooltip(slotDescription, amount, AttributeModifier.Operation.ADD_VALUE);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }
    @Override
    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<TrinketSlotsBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, amount).setNumericResponder(value -> selectAmount(consumer, value));
        editor.increaseHeight(19);
        editor.addTextField(0, 0, 200, 14, slotName).setResponder(value -> selectSlotName(consumer, value));
        editor.increaseHeight(19);
    }

    private void selectSlotName(Consumer<TrinketSlotsBonus> consumer, String slotName) {
        setSlotName(slotName);
        consumer.accept(this.copy());
    }

    private void selectAmount(Consumer<TrinketSlotsBonus> consumer, Double value) {
        setAmount(value.intValue());
        consumer.accept(this.copy());
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public TrinketSlotsBonus deserialize(JsonObject json) throws JsonParseException {
            String slotName = SerializationHelper.getElement(json, "slot").getAsString();
            int amount = SerializationHelper.getElement(json, "amount").getAsInt();
<<<<<<< Updated upstream
            String uuid = SerializationHelper.getElement(json, "modifier_id").getAsString();
            return new TrinketSlotsBonus(slotName, amount, UUID.fromString(uuid));
=======
            String modifierId = SerializationHelper.getElement(json, "modifier_id").getAsString();
            return new TrinketSlotsBonus(slotName, amount, ResourceLocation.parse(modifierId));
>>>>>>> Stashed changes
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof TrinketSlotsBonus aBonus)) {
                throw new IllegalArgumentException("Expected TrinketSlotsBonus instance");
            }
            json.addProperty("slot", aBonus.slotName);
            json.addProperty("amount", aBonus.amount);
            json.addProperty("modifier_id", aBonus.modifierId.toString());
        }

        @Override
        public TrinketSlotsBonus deserialize(CompoundTag tag) {
<<<<<<< Updated upstream
            String slotName = tag.getString("slot");
            int amount = tag.getInt("amount");
            String uuid = tag.getString("modifier_id");
            return new TrinketSlotsBonus(slotName, amount, UUID.fromString(uuid));
=======
            // Factual Fix 1.21.5: getString/getInt renvoient désormais Optional<T>
            String slotName = tag.getString("slot").orElse("");
            int amount = tag.getInt("amount").orElse(0);
            String modifierId = tag.getString("modifier_id").orElse("");
            return new TrinketSlotsBonus(slotName, amount, ResourceLocation.parse(modifierId));
>>>>>>> Stashed changes
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof TrinketSlotsBonus aBonus)) {
                throw new IllegalArgumentException("Expected TrinketSlotsBonus instance");
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("slot", aBonus.slotName);
            tag.putInt("amount", aBonus.amount);
            tag.putString("modifier_id", aBonus.modifierId.toString());
            return tag;
        }

        // Factual Fix 1.21.4: Refactored network signature from FriendlyByteBuf to RegistryFriendlyByteBuf to fulfill your core serialization interface
        @Override
        public TrinketSlotsBonus deserialize(RegistryFriendlyByteBuf buf) {
            String slotName = buf.readUtf();
            int amount = buf.readInt();
<<<<<<< Updated upstream
            String uuid = buf.readUtf();
            return new TrinketSlotsBonus(slotName, amount, UUID.fromString(uuid));
=======
            String modifierId = buf.readUtf();
            return new TrinketSlotsBonus(slotName, amount, ResourceLocation.parse(modifierId));
>>>>>>> Stashed changes
        }

        // Factual Fix 1.21.4: Refactored network signature from FriendlyByteBuf to RegistryFriendlyByteBuf to fulfill your core serialization interface
        @Override
        public void serialize(RegistryFriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof TrinketSlotsBonus aBonus)) {
                throw new IllegalArgumentException("Expected TrinketSlotsBonus instance");
            }
            buf.writeUtf(aBonus.slotName);
            buf.writeInt(aBonus.amount);
            buf.writeUtf(aBonus.modifierId.toString());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new TrinketSlotsBonus("ring", 1);
        }
    }
}