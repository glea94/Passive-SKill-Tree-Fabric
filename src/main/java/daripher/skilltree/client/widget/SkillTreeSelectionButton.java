package daripher.skilltree.client.widget;
import daripher.skilltree.client.screen.SkillTreeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
public class SkillTreeSelectionButton extends Button {
    private final Identifier skillTreeId;
    public SkillTreeSelectionButton(int x, int y, int width, int height, Identifier skillTreeId) {
        super(x, y, width, height, Component.translatable(skillTreeId.toString()));
        setPressFunc(b -> onPress(skillTreeId));
        this.skillTreeId = skillTreeId;
    }
    private static void onPress(Identifier skillTreeId) {
        getMinecraft().setScreen(new SkillTreeScreen(skillTreeId));
    }
    @Override
    protected void renderBackground(@NotNull GuiGraphicsExtractor graphics) {
        String texturesFolder = "textures/icons/skill_tree/";
        Identifier texture = skillTreeId.withPrefix(texturesFolder).withSuffix(".png");
        int v = getTextureVariant() * 19;
        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0F, (float) v, currentWidth, currentHeight, 19, 57);
    }
    @Override
    protected void renderText(@NotNull GuiGraphicsExtractor graphics) {
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
    }
    private static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }
}