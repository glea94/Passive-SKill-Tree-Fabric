package daripher.skilltree.compat.trinkets.skill.bonus;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.compat.trinkets.TrinketsCompatibility;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Portage/réécriture de compat/curios/skill/bonus/CurioSlotsBonus.java contre Trinkets API.
 * <p>
 * Différence de mécanisme avec Curios (assumée) : Curios modifiait directement l'inventaire de
 * slots (inv.addPermanentSlotModifiers). Trinkets expose à la place un Attribute vanilla par
 * groupe/slot (ex. "trinkets:ring/ring") que l'on modifie via AttributeInstance.addPermanentModifier
 * comme n'importe quel attribut vanilla (max_health, armor...) - le nombre de slots disponibles
 * suit alors la valeur de cet attribut. CONFIANCE MODÉRÉE sur le format exact de l'identifiant
 * ("trinkets:" + slotName, à confirmer une fois la dépendance Trinkets résolue dans Gradle).
 */
public final class TrinketSlotsBonus implements SkillBonus<TrinketSlotsBonus> {
    private String slotName;
    private int amount;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    private final UUID modifierId;
=======
    private final Identifier modifierId;
>>>>>>> Stashed changes
=======
    private final Identifier modifierId;
>>>>>>> Stashed changes

    public TrinketSlotsBonus(String slotName, int amount) {
        this.slotName = slotName;
        this.amount = amount;
        this.modifierId = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "trinket_slots_bonus_" + UUID.randomUUID());
    }

<<<<<<< Updated upstream
    private TrinketSlotsBonus(String slotName, int amount, UUID modifierId) {
=======
        this.modifierId = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "trinket_slots_bonus_" + UUID.randomUUID());
    }

    private TrinketSlotsBonus(String slotName, int amount, Identifier modifierId) {
>>>>>>> Stashed changes
=======
    private TrinketSlotsBonus(String slotName, int amount, Identifier modifierId) {
>>>>>>> Stashed changes
        this.slotName = slotName;
        this.amount = amount;
        this.modifierId = modifierId;
    }

    private AttributeInstance getSlotAttributeInstance(ServerPlayer player) {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        ResourceLocation attributeId = new ResourceLocation("trinkets", slotName);
        Attribute slotAttribute = BuiltInRegistries.ATTRIBUTE.get(attributeId);
        if (slotAttribute == null) {
=======
=======
>>>>>>> Stashed changes
        Identifier attributeId = Identifier.fromNamespaceAndPath("trinkets", slotName);
        // Factual Fix 1.21.4: BuiltInRegistries.ATTRIBUTE.get returns an Optional<Holder.Reference<Attribute>>, unwrap straight to a Holder type reference
        Holder<Attribute> slotAttributeHolder = BuiltInRegistries.ATTRIBUTE.get(attributeId)
                .map(holder -> (Holder<Attribute>) (Object) holder)
                .orElse(null);
        if (slotAttributeHolder == null) {
            return null;
        }
        return player.getAttribute(slotAttribute);
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
        AttributeModifier modifier = new AttributeModifier(modifierId, "SkillBonus", amount, AttributeModifier.Operation.ADDITION);
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
        MutableComponent tooltip = TooltipHelper.getSkillBonusTooltip(slotDescription, amount, AttributeModifier.Operation.ADDITION);
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
            String modifierId = SerializationHelper.getElement(json, "modifier_id").getAsString();
            return new TrinketSlotsBonus(slotName, amount, Identifier.parse(modifierId));
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof TrinketSlotsBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("slot", aBonus.slotName);
            json.addProperty("amount", aBonus.amount);
            json.addProperty("modifier_id", aBonus.modifierId.toString());
        }

        @Override
        public TrinketSlotsBonus deserialize(CompoundTag tag) {
            // Factual Fix 1.21.5: getString/getInt renvoient désormais Optional<T>
            String slotName = tag.getString("slot").orElse("");
            int amount = tag.getInt("amount").orElse(0);
            String modifierId = tag.getString("modifier_id").orElse("");
            return new TrinketSlotsBonus(slotName, amount, Identifier.parse(modifierId));
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof TrinketSlotsBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("slot", aBonus.slotName);
            tag.putInt("amount", aBonus.amount);
            tag.putString("modifier_id", aBonus.modifierId.toString());
            return tag;
        }

        @Override
        public TrinketSlotsBonus deserialize(FriendlyByteBuf buf) {
            String slotName = buf.readUtf();
            int amount = buf.readInt();
            String modifierId = buf.readUtf();
            return new TrinketSlotsBonus(slotName, amount, Identifier.parse(modifierId));
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof TrinketSlotsBonus aBonus)) {
                throw new IllegalArgumentException();
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
