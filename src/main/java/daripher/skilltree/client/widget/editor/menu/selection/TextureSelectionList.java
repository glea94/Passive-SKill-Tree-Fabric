package daripher.skilltree.client.widget.editor.menu.selection;

import net.minecraft.client.gui.GuiGraphics;
<<<<<<< Updated upstream
import net.minecraft.resources.ResourceLocation;
=======
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
>>>>>>> Stashed changes
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class TextureSelectionList extends SelectionList<Identifier> {
    private int textureWidth;
    private int textureHeight;

    public TextureSelectionList(int x, int y, int elementWidth, int elementHeight, int textureWidth, int textureHeight, Collection<Identifier> elementsList) {
        super(x, y, elementWidth, elementHeight, elementsList);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    protected void renderElement(@NotNull GuiGraphics graphics, int elementIndex, int x, int y) {
<<<<<<< Updated upstream
        ResourceLocation texture = getDisplayedElements().get(elementIndex);
        int textureX = x + (elementWidth - textureWidth) / 2;
        int textureY = y + (elementHeight - textureHeight) / 2;
        graphics.blit(texture, textureX, textureY, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
=======
        Identifier texture = getDisplayedElements().get(elementIndex);

        int textureX = x + (this.elementWidth - textureWidth) / 2;
        int textureY = y + (this.elementHeight - textureHeight) / 2;

        // Fix 1.21.8 : RenderPipelines.GUI_TEXTURED remplace le wrapper RenderType::guiTextured (supprimé)
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, textureX, textureY, 0F, 0F, textureWidth, textureHeight, textureWidth, textureHeight);
>>>>>>> Stashed changes
    }

    public TextureSelectionList setElementTextureSize(int width, int height) {
        textureWidth = width;
        textureHeight = height;
        return this;
    }
}