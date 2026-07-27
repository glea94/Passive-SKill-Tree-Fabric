package daripher.skilltree.compat.trinkets;

import daripher.skilltree.compat.trinkets.skill.bonus.TrinketSlotsBonus;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.util.registry.RegistryObject;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Portage/réécriture de compat/curios/CuriosCompatibility.java contre Trinkets API (Curios n'a
 * pas de build maintenue pour Fabric 1.20.1, cf. décision prise avec l'utilisateur en début de
 * projet).
 * <p>
 * CONFIANCE MODÉRÉE : le nom exact de l'API Trinkets pour obtenir/modifier dynamiquement la
 * capacité d'un slot par attribut (utilisé ici dans TrinketSlotsBonus) est basé sur ma
 * connaissance générale de Trinkets, pas vérifié contre la doc/le jar en direct - à confirmer
 * avant compilation (regarder dev.emi.trinkets.api.TrinketsApi et les classes du package
 * dev.emi.trinkets.api dans les sources Trinkets une fois la dépendance Gradle résolue).
 * <p>
 * VOLONTAIREMENT PAS PORTÉ ICI : applyCantUseItemBonus (CurioEquipEvent -> DENY). Trinkets
 * n'expose pas d'event global "un objet vient d'être équipé, à valider" aussi simplement que
 * Curios ; la vraie équivalence Trinkets passe par l'interface Trinket implémentée par chaque
 * objet (canEquip) ou par un mixin sur TrinketComponent - à traiter séparément si besoin, pas de
 * raccourci pris ici.
 */
public enum TrinketsCompatibility {
    INSTANCE;

    public static final RegistryObject<SkillBonus.Serializer> TRINKET_SLOTS_BONUS = PSTSkillBonuses.REGISTRY.register("curio_slots", TrinketSlotsBonus.Serializer::new);

    public Stream<ItemStack> getTrinkets(LivingEntity living) {
        List<ItemStack> trinkets = new ArrayList<>();
        TrinketsApi.getTrinketComponent(living).ifPresent(component -> {
            component.getAllEquipped().forEach(pair -> trinkets.add(pair.getB()));
        });
        return trinkets.stream();
    }
}
