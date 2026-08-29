package daripher.skilltree.skill.bonus.player;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import java.util.function.Consumer;
public final class CheatDeathBonus implements SkillBonus<CheatDeathBonus> {
    private int cooldownTicks;
    public CheatDeathBonus(int cooldownTicks) {
        this.cooldownTicks = cooldownTicks;
    }
    public int getCooldownTicks() {
        return cooldownTicks;
    }
    public void setCooldownTicks(int cooldownTicks) {
        this.cooldownTicks = cooldownTicks;
    }
    @Override
    public SkillBonus.Serializer getSerializer() {
        return PSTSkillBonuses.CHEAT_DEATH.get();
    }
    @Override
    public CheatDeathBonus copy() {
        return new CheatDeathBonus(cooldownTicks);
    }
    @Override
    public CheatDeathBonus multiply(double multiplier) {
        return this;
    }
    @Override
    public boolean canMerge(SkillBonus<?> other) {
        return other instanceof CheatDeathBonus;
    }
    @Override
    public SkillBonus<CheatDeathBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof CheatDeathBonus otherBonus)) {
            throw new IllegalArgumentException();
        }
        return new CheatDeathBonus(Math.min(cooldownTicks, otherBonus.cooldownTicks));
    }
    @Override
    public MutableComponent getSimpleTooltip() {
        String minutesDescription = TooltipHelper.formatNumber(cooldownTicks / 20f / 60f);
        MutableComponent tooltip = Component.translatable(getDescriptionId(), minutesDescription);
        return tooltip.withStyle(TooltipHelper.getSkillBonusStyle(isPositive()));
    }
    @Override
    public boolean isPositive() {
        return true;
    }
    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<CheatDeathBonus> consumer) {
        editor.addLabel(0, 0, "Cooldown (ticks)", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 90, 14, cooldownTicks).setNumericFilter(value -> value.intValue() >= 1 && value.intValue() == value)
                .setNumericResponder(value -> selectCooldown(consumer, value));
        editor.increaseHeight(19);
    }
    private void selectCooldown(Consumer<CheatDeathBonus> consumer, Double value) {
        setCooldownTicks(value.intValue());
        consumer.accept(this.copy());
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CheatDeathBonus that)) {
            return false;
        }
        return this.cooldownTicks == that.cooldownTicks;
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(cooldownTicks);
    }
    public static class Serializer implements SkillBonus.Serializer {
        @Override
        public CheatDeathBonus deserialize(JsonObject json) throws JsonParseException {
            int cooldownTicks = json.has("cooldown_ticks") ? json.get("cooldown_ticks").getAsInt() : 6000;
            return new CheatDeathBonus(cooldownTicks);
        }
        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof CheatDeathBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            json.addProperty("cooldown_ticks", aBonus.cooldownTicks);
        }
        @Override
        public CheatDeathBonus deserialize(CompoundTag tag) {
            int cooldownTicks = tag.getInt("cooldown_ticks").orElse(6000);
            return new CheatDeathBonus(cooldownTicks);
        }
        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof CheatDeathBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            CompoundTag tag = new CompoundTag();
            tag.putInt("cooldown_ticks", aBonus.cooldownTicks);
            return tag;
        }
        @Override
        public CheatDeathBonus deserialize(RegistryFriendlyByteBuf buf) {
            return new CheatDeathBonus(buf.readVarInt());
        }
        @Override
        public void serialize(RegistryFriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof CheatDeathBonus aBonus)) {
                throw new IllegalArgumentException();
            }
            buf.writeVarInt(aBonus.cooldownTicks);
        }
        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new CheatDeathBonus(6000);
        }
    }
}