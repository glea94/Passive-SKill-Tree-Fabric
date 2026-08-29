package daripher.skilltree.client.widget.editor.menu.selection;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.client.widget.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SelectionMenu<T> extends EditorMenu {
    private @NotNull Consumer<T> responder = v -> {
    };
    private final SelectionList<T> selectionList;
    private final Runnable onInit;
    private boolean requiresSearch = true;

    public SelectionMenu(SkillTreeEditor editor, @Nullable EditorMenu previousMenu, SelectionList<T> selectionList, Runnable onInit) {
        super(editor, previousMenu);
        this.selectionList = selectionList;
        this.onInit = onInit;
    }

    @Override
    public void init() {
        clearWidgets();
        editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> editor.selectMenu(previousMenu));
        editor.increaseHeight(29);
<<<<<<< Updated upstream

        if (requiresSearch) {
            
=======
        if (requiresSearch) {
>>>>>>> Stashed changes
            TextField searchField = editor.addTextField(0, 0, 200, 14, "");
            searchField.setHint("Search");
            searchField.setFocused(true);
            searchField.setResponder(selectionList::setSearchString);
            editor.increaseHeight(19);
        }

        selectionList.setX(editor.getWidgetsX(0));
        selectionList.setY(editor.getWidgetsY(0));

        onInit.run();
        editor.increaseHeight(selectionList.getHeight() + 10);

        selectionList.setResponder(responder);
        addWidget(selectionList);
    }

    public SelectionMenu<T> setResponder(@NotNull Consumer<T> responder) {
        this.responder = responder;
        return this;
    }

    public SelectionMenu<T> setRequiresSearch(boolean requiresSearch) {
        this.requiresSearch = requiresSearch;
        return this;
    }
}
