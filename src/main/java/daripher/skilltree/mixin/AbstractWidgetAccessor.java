package daripher.skilltree.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Portage Fabric : setHeight/setWidth sont des méthodes ajoutées par les patchs Forge à
 * AbstractWidget (absentes de vanilla/Fabric). Remplacées ici par un accessor Mixin ciblant les
 * champs vanilla sous-jacents directement.
 * <p>
 * NOTE : la partie fgColor a été retirée d'ici (23/07/2026) après un crash au lancement -
 * ni "fgColor" ni "packedFGColor" ne correspondaient au vrai nom du champ vanilla. Comme
 * setFGColor() n'était appelé nulle part dans le mod, Button/Label utilisent maintenant une
 * constante de couleur (0xFFFFFF) directement, sans passer par un accessor - plus fiable que de
 * deviner un nom de champ sans jar décompilé.
 */
@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccessor {
    @Accessor("width")
    void setWidth(int width);

    @Accessor("height")
    void setHeight(int height);
}
