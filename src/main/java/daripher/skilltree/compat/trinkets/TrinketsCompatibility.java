package daripher.skilltree.compat.trinkets;

import daripher.skilltree.compat.trinkets.skill.bonus.TrinketSlotsBonus;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.util.registry.RegistryObject;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public enum TrinketsCompatibility {
    INSTANCE;

    // Registers accessory slot bonus serializer within the active registration tree
    public static final RegistryObject<SkillBonus.Serializer> TRINKET_SLOTS_BONUS = PSTSkillBonuses.REGISTRY.register("curio_slots", TrinketSlotsBonus.Serializer::new);

    public Stream<ItemStack> getTrinkets(LivingEntity living) {
        List<ItemStack> trinkets = new ArrayList<>();

        // Safely map and grab item elements from all active slot inventory grids
        for (TrinketSlotAccess slot : TrinketsApi.getAttachment(living).allEquipped(false)) {
            trinkets.add(slot.get());
        }
        return trinkets.stream();
    }
}