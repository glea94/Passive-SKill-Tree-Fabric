package daripher.skilltree.effect;

import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.TickingSkillBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;

public abstract class SkillBonusEffect extends MobEffect {
    private final SkillBonus<?> bonus;

    public SkillBonusEffect(MobEffectCategory category, int color, SkillBonus<?> bonus) {
        super(category, color);
        this.bonus = bonus;
    }

    // CORRECTION 1.21.1 : MobEffect#removeAttributeModifiers a perdu ses paramètres LivingEntity
    // et amplifier ; sa signature vanilla est désormais removeAttributeModifiers(AttributeMap).
    // Conséquence : il n'est plus possible, depuis ce hook, d'identifier le joueur concerné pour
    // appeler bonus.onSkillRemoved(player) comme avant. Si ce comportement redevient nécessaire,
    // il faudra le déclencher ailleurs (par ex. via un mixin sur LivingEntity#removeEffect, qui a
    // lui accès à la fois à l'entité et à l'effet retiré).
    @Override
    public void removeAttributeModifiers(@NotNull AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);
    }

    // CORRECTION 1.21.1 : MobEffect#addAttributeModifiers a perdu son paramètre LivingEntity ;
    // sa signature vanilla est désormais addAttributeModifiers(AttributeMap, int amplifier).
    // Même remarque que ci-dessus concernant bonus.onSkillLearned(player, true).
    @Override
    public void addAttributeModifiers(@NotNull AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);
    }

    // CORRECTION 1.21.1 : MobEffect#isDurationEffectTick(int, int) a été renommé
    // shouldApplyEffectTickThisTick(int, int) (même rôle : décider si ce tick doit déclencher
    // applyEffectTick). L'ancienne méthode ne masque donc plus rien dans la classe mère : elle
    // compilait "par accident" (comme méthode surnuméraire non annotée @Override), mais ne
    // s'exécutait jamais réellement — d'où le comportement resté correct malgré l'absence
    // d'erreur de compilation initiale.
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return bonus instanceof TickingSkillBonus;
    }

    // CORRECTION 1.21.1 : MobEffect#applyEffectTick renvoie désormais un boolean (true = l'effet
    // doit continuer à s'appliquer / rester actif) au lieu de void.
    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayer player && bonus instanceof TickingSkillBonus ticking) {
            ticking.tick(player);
        }
        return true;
    }

    public SkillBonus<?> getBonus() {
        return bonus;
    }
}