package daripher.skilltree.skill.bonus.predicate.living;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface LivingEntityPredicate extends Predicate<LivingEntity> {
    default String getDescriptionId() {
<<<<<<< Updated upstream
        ResourceLocation id = PSTRegistries.LIVING_CONDITIONS.get().getKey(getSerializer());
        assert id != null;
=======
        Identifier id = PSTRegistries.LIVING_CONDITIONS.get().getKey(getSerializer());
        Objects.requireNonNull(id);
>>>>>>> Stashed changes
        return "living_condition.%s.%s".formatted(id.getNamespace(), id.getPath());
    }

    MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target);

    Serializer getSerializer();

    default void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingEntityPredicate> consumer) {
    }

    interface Serializer extends daripher.skilltree.data.serializers.Serializer<LivingEntityPredicate> {
        LivingEntityPredicate createDefaultInstance();
    }
}
