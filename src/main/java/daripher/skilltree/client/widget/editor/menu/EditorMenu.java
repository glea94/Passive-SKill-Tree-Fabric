package daripher.skilltree.client.widget.editor.menu;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.group.WidgetGroup;
import net.minecraft.client.gui.components.AbstractWidget;
import org.jetbrains.annotations.Nullable;

public abstract class EditorMenu extends WidgetGroup<AbstractWidget> {
    protected final SkillTreeEditor editor;
    public final @Nullable EditorMenu previousMenu;
<<<<<<< Updated upstream

    public EditorMenu(SkillTreeEditor editor, @Nullable EditorMenu previousMenu) {
        
=======
    public EditorMenu(SkillTreeEditor editor, @Nullable EditorMenu previousMenu) {
>>>>>>> Stashed changes
        super(0, 0, 0, 0);
        this.setX(0);
        this.setY(0);

        this.editor = editor;
        this.previousMenu = previousMenu;
    }

    public abstract void init();

    @FunctionalInterface
    protected interface MenuConstructor {
        EditorMenu construct(SkillTreeEditor editor, EditorMenu previousMenu);
    }
}
