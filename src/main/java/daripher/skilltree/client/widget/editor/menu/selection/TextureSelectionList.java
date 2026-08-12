package daripher.skilltree.client.widget.editor.menu.selection;

import net.minecraft.client.gui.GuiGraphics;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
=======
import net.minecraft.client.renderer.RenderPipelines;
>>>>>>> Stashed changes
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class TextureSelectionList extends SelectionList<ResourceLocation> {
    private int textureWidth;
    private int textureHeight;

    public TextureSelectionList(int x, int y, int elementWidth, int elementHeight, int textureWidth, int textureHeight, Collection<ResourceLocation> elementsList) {
        super(x, y, elementWidth, elementHeight, elementsList);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    protected void renderElement(@NotNull GuiGraphics graphics, int elementIndex, int x, int y) {
        ResourceLocation texture = getDisplayedElements().get(elementIndex);
<<<<<<< Updated upstream
        int textureX = x + (elementWidth - textureWidth) / 2;
        int textureY = y + (elementHeight - textureHeight) / 2;
        graphics.blit(texture, textureX, textureY, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
=======

        int textureX = x + (this.elementWidth - textureWidth) / 2;
        int textureY = y + (this.elementHeight - textureHeight) / 2;

        // Fix 1.21.8 : RenderPipelines.GUI_TEXTURED remplace le wrapper RenderType::guiTextured (supprimé)
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, textureX, textureY, 0F, 0F, textureWidth, textureHeight, textureWidth, textureHeight);
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    }

    public TextureSelectionList setElementTextureSize(int width, int height) {
        textureWidth = width;
        textureHeight = height;
        return this;
    }
}