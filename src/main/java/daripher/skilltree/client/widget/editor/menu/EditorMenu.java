package daripher.skilltree.client.widget.editor.menu;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.group.WidgetGroup;
import net.minecraft.client.gui.components.AbstractWidget;
import org.jetbrains.annotations.Nullable;

public abstract class EditorMenu extends WidgetGroup<AbstractWidget> {
    protected final SkillTreeEditor editor;
    public final @Nullable EditorMenu previousMenu;

    public EditorMenu(SkillTreeEditor editor, @Nullable EditorMenu previousMenu) {
        // Factual Fix 1.21.4: Direct assignment parameters matched with standard layout guidelines
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
