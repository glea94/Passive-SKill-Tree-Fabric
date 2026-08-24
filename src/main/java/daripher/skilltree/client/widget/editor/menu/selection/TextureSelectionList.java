package daripher.skilltree.client.widget.editor.menu.selection;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
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
    protected void renderElement(@NotNull GuiGraphicsExtractor graphics, int elementIndex, int x, int y) {
        Identifier texture = getDisplayedElements().get(elementIndex);

        int textureX = x + (this.elementWidth - textureWidth) / 2;
        int textureY = y + (this.elementHeight - textureHeight) / 2;


        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, textureX, textureY, 0F, 0F, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    public TextureSelectionList setElementTextureSize(int width, int height) {
        textureWidth = width;
        textureHeight = height;
        return this;
    }
}