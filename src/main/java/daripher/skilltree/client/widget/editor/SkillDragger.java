package daripher.skilltree.client.widget.editor;

import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class SkillDragger extends AbstractWidget {
    private boolean gridSnapEnabled;
    private boolean showGrid;
    private int gridSizeX = 40;
    private int gridSizeY = 40;
    private final SkillTreeEditor editor;
    private double totalDragX;
    private double totalDragY;

    public SkillDragger(SkillTreeEditor editor) {
        
        super(0, 0, 0, 0, Component.empty());
        this.editor = editor;
    }

    @Override
    protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (showGrid) {
            int width = editor.getScreenWidth();
            int height = editor.getScreenHeight();
            float gridX = this.gridSizeX * editor.getZoom();
            float gridY = this.gridSizeY * editor.getZoom();
            float gridCenterX = width / 2f + editor.getScrollX() % gridX;
            float gridCenterY = height / 2f + editor.getScrollY() % gridY;
            graphics.pose().pushMatrix();
            graphics.pose().translate(gridCenterX, gridCenterY);
            for (int i = -width / 2 / (int) gridX; i < width / gridX; i++) {
                float x = gridX * i;
                graphics.fill((int) (-1 + x), -height, (int) (1 + x), height, 0x55CFCFCF);
            }
            for (int i = -height / 2 / (int) gridY - 1; i < height / gridY; i++) {
                float y = gridY * i;
                graphics.fill(-width, (int) (-1 + y), width, (int) (1 + y), 0x55CFCFCF);
            }
            graphics.pose().popMatrix();
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dragX, double dragY) {
        if (mouseButtonEvent.button() == 0 && mouseButtonEvent.hasControlDown() && !editor.getSelectedSkills().isEmpty()) {
            dragX = dragX / editor.getZoom();
            dragY = dragY / editor.getZoom();
            if (gridSnapEnabled) {
                totalDragX += dragX;
                totalDragY += dragY;
                if (Math.abs(totalDragX) >= gridSizeX) {
                    float actualDragX = (float) (gridSizeX * Math.signum(totalDragX));
                    dragSelectedSkills(actualDragX, 0);
                    totalDragX -= actualDragX;
                }
                if (Math.abs(totalDragY) >= gridSizeY) {
                    float actualDragY = (float) (gridSizeY * Math.signum(totalDragY));
                    dragSelectedSkills(0, actualDragY);
                    totalDragY -= actualDragY;
                }
            } else {
                dragSelectedSkills((float) dragX, (float) dragY);
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        totalDragX = totalDragY = 0;
        return super.mouseReleased(mouseButtonEvent);
    }

    private void dragSelectedSkills(float x, float y) {
        editor.getSelectedSkills().forEach(skill -> dragSkill(x, y, skill));
        editor.updateSkillConnections();
        editor.saveSelectedSkills();
    }

    private void dragSkill(float x, float y, PassiveSkill skill) {
        float xPosition = skill.getPositionX() + x;
        float yPosition = skill.getPositionY() + y;
        if (gridSnapEnabled) {
            xPosition -= xPosition % gridSizeX;
            yPosition -= yPosition % gridSizeY;
        }
        skill.setPosition(xPosition, yPosition);
        editor.getSkillButtons().removeIf(button -> button.skill == skill);
        editor.addSkillButton(skill);
    }

    public boolean isGridSnapEnabled() {
        return gridSnapEnabled;
    }

    public void setGridSnapEnabled(boolean gridSnapEnabled) {
        this.gridSnapEnabled = gridSnapEnabled;
    }

    public int getGridSizeX() {
        return gridSizeX;
    }

    public void setGridSizeX(int gridSizeX) {
        this.gridSizeX = Math.max(gridSizeX, 1);
    }

    public int getGridSizeY() {
        return gridSizeY;
    }

    public void setGridSizeY(int gridSizeY) {
        this.gridSizeY = Math.max(gridSizeY, 1);
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }
}